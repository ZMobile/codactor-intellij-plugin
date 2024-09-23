package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.intellij.openapi.project.Project;
import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;
import com.translator.service.codactor.ai.modification.authorization.VerifyIsTestFileService;
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
            URLClassLoader classLoader = dynamicClassLoaderService.dynamicallyLoadClass(testedFilePath);
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
}
