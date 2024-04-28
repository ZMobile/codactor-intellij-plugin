package com.translator.service.codactor.ide.editor.psi;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import com.translator.model.codactor.ide.psi.implementation.ImplementationResult;
import com.translator.model.codactor.ide.psi.implementation.ImplementationResultsResource;
import com.translator.model.codactor.ide.psi.implementation.MethodImplementationResultsResource;
import com.translator.service.codactor.ide.editor.CodeSnippetIndexGetterService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FindImplementationsServiceImpl implements FindImplementationsService {
    private final PsiElementCollectorService psiElementCollectorService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;

    @Inject
    public FindImplementationsServiceImpl(PsiElementCollectorService psiElementCollectorService,
                                          CodeSnippetIndexGetterService codeSnippetExtractorService) {
        this.psiElementCollectorService = psiElementCollectorService;
        this.codeSnippetIndexGetterService = codeSnippetExtractorService;
    }


    public ImplementationResultsResource findImplementationsWithinRange(String filePath, int startOffset, int endOffset) {
        ImplementationResultsResource results = new ImplementationResultsResource(filePath);
        ApplicationManager.getApplication().invokeAndWait(() -> {
            List<PsiElement> elementsInRange = psiElementCollectorService.collectMethodsAndVariables(filePath, startOffset, endOffset);
            for (PsiElement element : elementsInRange) {
                if (element instanceof PsiMethod) {
                    PsiMethod psiMethod = (PsiMethod) element;
                    MethodImplementationResultsResource methodImplementationResultsResource = new MethodImplementationResultsResource(psiMethod.getName());
                    Collection<PsiMethod> implementations = psiElementCollectorService.findImplementations(psiMethod);
                    for (PsiMethod implementation : implementations) {
                        PsiFile psiFile = implementation.getContainingFile();
                        String psiReferenceFilePath = psiFile.getVirtualFile().getPath();
                        ImplementationResult implementationResult = new ImplementationResult(psiReferenceFilePath, implementation.getText());
                        methodImplementationResultsResource.getImplementations().add(implementationResult);
                    }
                    results.getMethods().add(methodImplementationResultsResource);
                }
            }
        });

        return results;
    }

    @Override
    public ImplementationResultsResource findImplementationsWithinRange(String filePath, String codeSnippet) {
        AtomicInteger startOffset = new AtomicInteger();
        AtomicInteger endOffset = new AtomicInteger();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            startOffset.set(codeSnippetIndexGetterService.getStartIndexInFilePath(filePath, codeSnippet));
            endOffset.set(codeSnippetIndexGetterService.getEndIndexInFilePath(filePath, codeSnippet));
        });
        System.out.println("Start offset: " + startOffset.get() + ", End offset: " + endOffset.get() + ", Snippet: " + codeSnippet); // Added log

        return findImplementationsWithinRange(filePath, startOffset.get(), endOffset.get());
    }
}
