package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.translator.service.codactor.ai.modification.authorization.VerifyIsTestFileService;
import com.translator.service.codactor.ide.file.FileCreatorService;
import com.translator.service.codactor.io.CodactorRelevantBuildOutputLocatorService;
import com.translator.service.codactor.io.CodactorRelevantBuildOutputLocatorServiceImpl;
import org.jetbrains.annotations.Nullable;
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
    private final CodactorRelevantBuildOutputLocatorService codactorRelevantBuildOutputLocatorService;

    @Inject
    public RunTestAndGetOutputServiceImpl(Project project,
                                          VerifyIsTestFileService verifyIsTestFileService,
                                          CodactorRelevantBuildOutputLocatorService codactorRelevantBuildOutputLocatorService) {
        this.project = project;
        this.verifyIsTestFileService = verifyIsTestFileService;
        this.codactorRelevantBuildOutputLocatorService = codactorRelevantBuildOutputLocatorService;
    }

    public String runTestAndGetOutput(String filePath) throws Exception {
        // Normalize file path

        filePath = filePath.replace("\\", "/");


        // Extract the class name from the file name (without extension)
        String isolatedClassName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
        // Assume the package can be inferred from the file path structure, e.g., src/main/java/com/example/MyClass.java


        System.out.println("Class not found in classpath, attempting to load from build output");
        String buildOutputParentDirectoryPath = codactorRelevantBuildOutputLocatorService.locateRelevantBuildOutput(filePath);
        String buildOutputPath = buildOutputParentDirectoryPath + "/" + isolatedClassName + ".class";
        File buildOutputFile = new File(buildOutputPath);
        System.out.println("Build output file exists: " + buildOutputFile.exists());
        if (!buildOutputFile.exists()) {
            throw new Exception("Build output file does not exist: " + buildOutputPath);
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

                System.out.println("Build output file exists: " + buildOutputFile.exists());
                String buildOutputRootDirPath;
                if (buildOutputFile.getAbsolutePath().contains("/main/")) {
                    buildOutputRootDirPath = buildOutputFile.getAbsolutePath().substring(0,
                            buildOutputFile.getAbsolutePath().indexOf("build/classes/java/main/") + "build/classes/java/main/".length());
                } else if (buildOutputFile.getAbsolutePath().contains("/test/")) {
                    buildOutputRootDirPath = buildOutputFile.getAbsolutePath().substring(0,
                            buildOutputFile.getAbsolutePath().indexOf("build/classes/java/test/") + "build/classes/java/test/".length());
                } else {
                    throw new Exception("Could not determine build output root directory");
                }
                File buildOutputRootDir = new File(buildOutputRootDirPath);
                System.out.println("Build output root dir path: " + buildOutputRootDirPath);
                URL[] urls = { buildOutputRootDir.toURI().toURL() };
                URLClassLoader urlClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
                testClass[0] = urlClassLoader.loadClass(className);
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
