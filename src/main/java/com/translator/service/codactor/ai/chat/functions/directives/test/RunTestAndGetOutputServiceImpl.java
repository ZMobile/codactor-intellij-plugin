package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.translator.service.codactor.ai.modification.authorization.VerifyIsTestFileService;
import org.jetbrains.annotations.Nullable;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class RunTestAndGetOutputServiceImpl implements RunTestAndGetOutputService {
    private final Project project;
    private final VerifyIsTestFileService verifyIsTestFileService;

    @Inject
    public RunTestAndGetOutputServiceImpl(Project project,
                                          VerifyIsTestFileService verifyIsTestFileService) {
        this.project = project;
        this.verifyIsTestFileService = verifyIsTestFileService;
    }

    public String runTestAndGetOutput(String filePath) throws Exception {
        filePath = filePath.replace("\\", "/");
        VirtualFile virtualFile = project.getBaseDir().getFileSystem().findFileByPath(filePath);
        if (virtualFile == null) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        PsiJavaFile psiJavaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(virtualFile);
        if (psiJavaFile == null) {
            throw new IllegalArgumentException("Unable to locate the Java file: " + filePath);
        }

        PsiClass psiClass = psiJavaFile.getClasses()[0];
        if (!verifyIsTestFileService.isTestFile(project, filePath)) {
            throw new IllegalArgumentException("No test class found in file: " + filePath);
        }

        // Convert PsiClass to Class using reflection
        String className = psiClass.getQualifiedName();
        if (className == null) {
            throw new IllegalArgumentException("Unable to determine class name for file: " + filePath);
        }

        // Redirect System.out to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        StringBuilder resultString = new StringBuilder();
        try {
            Class<?> testClass = Class.forName(className);
            Result result = JUnitCore.runClasses(testClass);
            for (Failure failure : result.getFailures()) {
                resultString.append("\n").append(failure.toString());
            }
            resultString.append("\nSuccess: ").append(result.wasSuccessful());
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
