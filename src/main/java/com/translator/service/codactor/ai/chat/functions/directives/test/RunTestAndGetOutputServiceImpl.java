package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.intellij.openapi.project.Project;
import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;
import com.translator.model.codactor.ai.chat.function.directive.test.TestClassInfoResource;
import com.translator.service.codactor.ai.modification.authorization.VerifyIsTestFileService;
import com.translator.service.codactor.io.CustomURLClassLoader;
import com.translator.service.codactor.io.DynamicClassLoaderService;
import com.translator.service.codactor.io.DynamicClassLoaderServiceImpl;
import com.translator.service.codactor.io.RelevantBuildOutputLocatorService;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunTestAndGetOutputServiceImpl implements RunTestAndGetOutputService {
    private final Project project;
    private final VerifyIsTestFileService verifyIsTestFileService;
    private final RelevantBuildOutputLocatorService relevantBuildOutputLocatorService;
    private final DynamicClassLoaderService dynamicClassLoaderService;

    @Inject
    public RunTestAndGetOutputServiceImpl(Project project,
                                          VerifyIsTestFileService verifyIsTestFileService,
                                          RelevantBuildOutputLocatorService relevantBuildOutputLocatorService,
                                          DynamicClassLoaderService dynamicClassLoaderService) {
        this.project = project;
        this.verifyIsTestFileService = verifyIsTestFileService;
        this.relevantBuildOutputLocatorService = relevantBuildOutputLocatorService;
        this.dynamicClassLoaderService = dynamicClassLoaderService;
    }

    public String runTestAndGetOutput(CreateAndRunUnitTestDirectiveSession createAndRunUnitTestDirectiveSession) throws Exception {
        // Normalize file path

        String testFilePath = createAndRunUnitTestDirectiveSession.getTestFilePath().replace("\\", "/");
        String testedFilePath = createAndRunUnitTestDirectiveSession.getFilePath().replace("\\", "/");

        // Extract the class name from the file name (without extension)
        String isolatedClassName = testFilePath.substring(testFilePath.lastIndexOf("/") + 1, testFilePath.lastIndexOf("."));
        // Assume the package can be inferred from the file path structure, e.g., src/main/java/com/example/MyClass.java


        String buildOutputParentDirectoryPath = relevantBuildOutputLocatorService.locateRelevantBuildOutput(testFilePath);
        String buildOutputPath = buildOutputParentDirectoryPath + "/" + isolatedClassName + ".class";
        File buildOutputFile = new File(buildOutputPath);
        System.out.println("Build output file exists: " + buildOutputFile.exists());
        if (!buildOutputFile.exists()) {
            throw new Exception("Build output file does not exist: " + buildOutputPath + ". Please try again in a few moments after the compilation has completed.");
        }

        String testFilePackagePath = testFilePath.substring(testFilePath.indexOf("java/") + 5, testFilePath.lastIndexOf("/")).replace("/", ".");
        String testFileClassName = testFilePackagePath + "." + isolatedClassName;
        // Redirect System.out to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);
        //File file = new File(filePath);

        StringBuilder resultString = new StringBuilder();
        try {
            List<String> targetFilePaths = new ArrayList<>();
            targetFilePaths.add(testFilePath);
            targetFilePaths.add(testedFilePath);
            CustomURLClassLoader classLoader = dynamicClassLoaderService.dynamicallyLoadClass(targetFilePaths);
            Result result = (Result) classLoader.loadClass(JUnitCore.class.getName())
                    .getMethod("runClasses", Class[].class)
                    .invoke(null, (Object) new Class[]{classLoader.loadClass(testFileClassName)});

            for (Failure failure : result.getFailures()) {
                resultString.append("\n").append(failure.toString());
            }
            resultString.append("\nSuccess: ").append(result.wasSuccessful());
        } catch (Exception e) {
            e.printStackTrace();
            return e.getStackTrace().toString();
        } finally {
            // Restore the original System.out
            System.setOut(originalOut);
            String systemOutOutput = outputStream.toString();
            if (!systemOutOutput.isEmpty()) {
                resultString.append("\nSystem.out output:\n").append(systemOutOutput);
            }
        }

        return resultString.toString();
    }

    public Map<String, Result> runTestsAndGetOutputs(String implementationFilePath, List<String> unitTestFilePaths) throws Exception {
        Map<String, Result> filePathToResultsMap = new HashMap<>();

        // Normalize file paths and validate the existence of build output files
        List<TestClassInfoResource> testClassInfoResources = new ArrayList<>();
        for (String testFilePath : unitTestFilePaths) {
            String isolatedClassName = testFilePath.substring(testFilePath.lastIndexOf("/") + 1, testFilePath.lastIndexOf("."));
            String testFilePackagePath = testFilePath.substring(testFilePath.indexOf("java/") + 5, testFilePath.lastIndexOf("/")).replace("/", ".");
            String testFileClassName = testFilePackagePath + "." + isolatedClassName;
            String buildOutputParentDirectoryPath = relevantBuildOutputLocatorService.locateRelevantBuildOutput(testFilePath);
            String buildOutputPath = buildOutputParentDirectoryPath + "/" + isolatedClassName + ".class";
            if (buildOutputParentDirectoryPath == null) {
                throw new Exception("Build output path could not be located for " + testFilePath);
            }
            TestClassInfoResource testClassInfoResource = new TestClassInfoResource.Builder()
                    .withClassName(testFileClassName)
                    .withPath(testFilePath)
                    .withBuildOutputPath(buildOutputPath)
                    .build();
            testClassInfoResources.add(testClassInfoResource);
            File buildOutputFile = new File(buildOutputPath);
            System.out.println("Build output file exists for " + testFileClassName + ": " + buildOutputFile.exists());
        }

        // Redirect System.out to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        try {
            // Prepare to dynamically load test classes
            List<String> targetFilePaths = new ArrayList<>(unitTestFilePaths);
            targetFilePaths.add(implementationFilePath);
            CustomURLClassLoader classLoader = dynamicClassLoaderService.dynamicallyLoadClass(targetFilePaths);

            // Run each test class
            for (TestClassInfoResource testClassInfoResource : testClassInfoResources) {
                StringBuilder resultString = new StringBuilder();
                try {
                    // Dynamically load and run the test class
                    Result result = (Result) classLoader.loadClass(JUnitCore.class.getName())
                            .getMethod("runClasses", Class[].class)
                            .invoke(null, (Object) new Class[]{classLoader.loadClass(testClassInfoResource.getClassName())});

                    for (Failure failure : result.getFailures()) {
                        resultString.append("\n").append(failure.toString());
                    }
                    resultString.append("\nSuccess: ").append(result.wasSuccessful());
                    filePathToResultsMap.put(testClassInfoResource.getPath(), result);
                } catch (Exception e) {
                    e.printStackTrace();
                    resultString.append("\nError: ").append(e.getMessage());
                } finally {
                    // Capture System.out output for this test
                    String systemOutOutput = outputStream.toString();
                    if (!systemOutOutput.isEmpty()) {
                        resultString.append("\nSystem.out output:\n").append(systemOutOutput);
                    }
                    //results.add(resultString.toString());
                    System.out.println(resultString);
                    outputStream.reset(); // Clear the output stream for the next test
                }
            }
        } finally {
            // Restore the original System.out
            System.setOut(originalOut);
        }

        return filePathToResultsMap;
    }
}
