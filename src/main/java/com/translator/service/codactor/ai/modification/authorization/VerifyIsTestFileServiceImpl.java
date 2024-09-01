package com.translator.service.codactor.ai.modification.authorization;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;

public class VerifyIsTestFileServiceImpl implements VerifyIsTestFileService {
    @Override
    public boolean isTestFile(Project project, String filePath) {
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath.replace('\\', '/'));
        return file != null && isTestFile(project, file);
    }

    public boolean isTestFile(Project project, VirtualFile file) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile == null) {
            return false;
        }

        // Check if the file is located in typical test directories
        String filePath = file.getPath();
        if (filePath.contains("/src/test/java/") || filePath.contains("/src/test/resources/")) {
            return true;
        }

        // Check for test annotations or test-related naming conventions
        if (psiFile.getName().endsWith("Test.java") || psiFile.getName().endsWith("Spec.groovy")) {
            return true;
        }

        // Analyze the PSI tree for test-related annotations
        PsiClass[] classes = PsiTreeUtil.getChildrenOfType(psiFile, PsiClass.class);
        if (classes != null) {
            for (PsiClass psiClass : classes) {
                for (PsiMethod method : psiClass.getMethods()) {
                    if (hasTestAnnotation(method)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean hasTestAnnotation(PsiMethod method) {
        for (PsiAnnotation annotation : method.getAnnotations()) {
            String qualifiedName = annotation.getQualifiedName();
            if (qualifiedName != null && (qualifiedName.equals("org.junit.Test") ||
                    qualifiedName.equals("org.junit.jupiter.api.Test") ||
                    qualifiedName.equals("spock.lang.Specification"))) {
                return true;
            }
        }
        return false;
    }
}
