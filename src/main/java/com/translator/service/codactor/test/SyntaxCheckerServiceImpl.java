package com.translator.service.codactor.test;

import com.google.inject.Inject;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SyntaxCheckerServiceImpl implements SyntaxCheckerService {
    private final Project project;

    @Inject
    public SyntaxCheckerServiceImpl(Project project) {
        this.project = project;
    }

    public boolean checkSyntax(String code) {
        // Create a PsiFile from the code string
        PsiFile psiFile = createPsiFileFromString(code);

        if (!(psiFile instanceof PsiJavaFile)) {
            System.out.println("Error: The provided code is not a valid Java file.");
            return false;
        }

        // Find syntax errors in the PsiFile
        PsiErrorElement[] errors = findSyntaxErrors(psiFile);
        if (errors.length > 0) {
            for (PsiErrorElement error : errors) {
                System.out.println("Syntax error: " + error.getErrorDescription() +
                        " at " + error.getTextRange());
            }
            return false;
        }

        System.out.println("No syntax errors found.");
        return true;
    }

    private PsiFile createPsiFileFromString(String code) {
        // Use the PsiFileFactory to create a PsiFile from the code string
        AtomicReference<PsiFile> psiFile = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            PsiFileFactory factory = PsiFileFactory.getInstance(project);
            String fileName = "Dummy.java";
            psiFile.set(factory.createFileFromText(fileName, JavaLanguage.INSTANCE, code));
        });
        return psiFile.get();
    }

    private PsiErrorElement[] findSyntaxErrors(PsiFile psiFile) {
        List<PsiErrorElement> errors = new ArrayList<>();
        psiFile.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitErrorElement(PsiErrorElement element) {
                errors.add(element);
                super.visitErrorElement(element);
            }
        });
        return errors.toArray(new PsiErrorElement[0]);
    }
}
