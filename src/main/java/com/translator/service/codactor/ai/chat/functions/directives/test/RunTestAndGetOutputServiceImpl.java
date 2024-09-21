package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.intellij.openapi.project.Project;
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

    public String runTestAndGetOutput(String filePath) throws Exception {
        // Normalize file path

        filePath = filePath.replace("\\", "/");


        // Extract the class name from the file name (without extension)
        String isolatedClassName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
        // Assume the package can be inferred from the file path structure, e.g., src/main/java/com/example/MyClass.java


        String buildOutputParentDirectoryPath = relevantBuildOutputLocatorService.locateRelevantBuildOutput(filePath);
        String buildOutputPath = buildOutputParentDirectoryPath + "/" + isolatedClassName + ".class";
        File buildOutputFile = new File(buildOutputPath);
        System.out.println("Build output file exists: " + buildOutputFile.exists());
        if (!buildOutputFile.exists()) {
            throw new Exception("Build output file does not exist: " + buildOutputPath + ". Please try again in a few moments after the compilation has completed.");
        }

        String packagePath = filePath.substring(filePath.indexOf("java/") + 5, filePath.lastIndexOf("/")).replace("/", ".");
        String className = packagePath + "." + isolatedClassName;
        // Redirect System.out to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);
        //File file = new File(filePath);

        StringBuilder resultString = new StringBuilder();

        try {
            System.out.println("Running test: " + className);
            final Class<?>[] testClass = new Class<?>[1];
            try {
                testClass[0] = Class.forName(className);
            } catch (ClassNotFoundException e) {
                testClass[0] = dynamicClassLoaderService.dynamicallyLoadClass(filePath);
            }
            Result result = JUnitCore.runClasses(testClass[0]);
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
