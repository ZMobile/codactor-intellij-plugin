package com.translator.service.codactor.ide.editor.psi;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import com.translator.model.codactor.ide.psi.usage.FieldUsageResultsResource;
import com.translator.model.codactor.ide.psi.usage.MethodUsageResultsResource;
import com.translator.model.codactor.ide.psi.usage.UsageResult;
import com.translator.model.codactor.ide.psi.usage.UsageResultsResource;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.CodeSnippetIndexGetterService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FindUsagesServiceImpl implements FindUsagesService {
    private final PsiElementCollectorService psiElementCollectorService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;
    private final CodeSnippetExtractorService codeSnippetExtractorService;

    @Inject
    public FindUsagesServiceImpl(PsiElementCollectorService psiElementCollectorService,
                                 CodeSnippetIndexGetterService codeSnippetIndexGetterService,
                                 CodeSnippetExtractorService codeSnippetExtractorService) {
        this.psiElementCollectorService = psiElementCollectorService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;

    }


    public UsageResultsResource findUsagesWithinRange(String filePath, int startOffset, int endOffset) {
        UsageResultsResource results = new UsageResultsResource(filePath);
        ApplicationManager.getApplication().invokeAndWait(() -> {
            List<PsiElement> elementsInRange = psiElementCollectorService.collectMethodsAndVariables(filePath, startOffset, endOffset);
            for (PsiElement element : elementsInRange) {
                if (element instanceof PsiMethod) {
                    PsiMethod psiMethod = (PsiMethod) element;
                    MethodUsageResultsResource methodUsageResultsResource = new MethodUsageResultsResource(psiMethod.getName());
                    Collection<PsiReference> references = psiElementCollectorService.findMethodReferences(psiMethod);
                    for (PsiReference psiReference : references) {
                        PsiFile psiFile = psiReference.getElement().getContainingFile();
                        String psiReferenceFilePath = psiFile.getVirtualFile().getPath();
                        String snippet = codeSnippetExtractorService.getCurrentLineCodeAtIndex(psiReferenceFilePath, psiReference.getElement().getTextOffset());
                        UsageResult usageResult = new UsageResult(psiReferenceFilePath, snippet);
                        methodUsageResultsResource.getUsages().add(usageResult);
                    }
                    results.getMethods().add(methodUsageResultsResource);
                } else if (element instanceof PsiField) {
                    PsiField psiField = (PsiField) element;
                    FieldUsageResultsResource fieldUsageResultsResource = new FieldUsageResultsResource(psiField.getName());
                    Collection<PsiReference> internalReferences = psiElementCollectorService.findFieldReferences(psiField);
                    for (PsiReference internalReference : internalReferences) {
                        PsiFile psiFile = internalReference.getElement().getContainingFile();
                        String psiReferenceFilePath = psiFile.getVirtualFile().getPath();
                        String snippet = codeSnippetExtractorService.getCurrentLineCodeAtIndex(psiReferenceFilePath, internalReference.getElement().getTextOffset());
                        UsageResult usageResult = new UsageResult(psiReferenceFilePath, snippet);
                        fieldUsageResultsResource.getInternalUsages().add(usageResult);
                    }
                    PsiClass psiClass = psiElementCollectorService.findReferencedClassOfField((PsiField) element);
                    Collection<PsiReference> externalReferences = psiElementCollectorService.findClassReferences(psiClass);
                    for (PsiReference psiReference : externalReferences) {
                        PsiFile psiFile = psiReference.getElement().getContainingFile();
                        String psiReferenceFilePath = psiFile.getVirtualFile().getPath();
                        if (psiReferenceFilePath.equals(filePath)) {
                            continue;
                        }
                        String snippet = codeSnippetExtractorService.getCurrentLineCodeAtIndex(psiReferenceFilePath, psiReference.getElement().getTextOffset());
                        UsageResult usageResult = new UsageResult(psiReferenceFilePath, snippet);
                        fieldUsageResultsResource.getExternalUsages().add(usageResult);
                    }
                    results.getFields().add(fieldUsageResultsResource);
                }
            }

        });
        return results;
    }

    /*public UsageResultsResource findUsagesOfClass(String filePath) {
        UsageResultsResource results = new UsageResultsResource(filePath);
        ApplicationManager.getApplication().invokeAndWait(() -> {
            List<PsiMethod> methods = psiElementCollectorService.collectMethods(filePath);

            // If the class is an implementation, add methods from interfaces it implements
            List<PsiMethod> interfaceMethods = psiElementCollectorService.collectInterfaceMethods(filePath);
            methods.addAll(interfaceMethods);

            for (PsiMethod method : methods) {
                MethodUsageResultsResource methodUsageResultsResource = new MethodUsageResultsResource(method.getName());
                Collection<PsiReference> references = psiElementCollectorService.findMethodReferences(method);
                for (PsiReference psiReference : references) {
                    PsiFile psiFile = psiReference.getElement().getContainingFile();
                    String psiReferenceFilePath = psiFile.getVirtualFile().getPath();
                    String snippet = codeSnippetExtractorService.getCurrentLineCodeAtIndex(psiReferenceFilePath, psiReference.getElement().getTextOffset());
                    UsageResult usageResult = new UsageResult(psiReferenceFilePath, snippet);
                    methodUsageResultsResource.getUsages().add(usageResult);
                }
                results.getMethods().add(methodUsageResultsResource);
            }
        });
        return results;
    }*/

    @Override
    public UsageResultsResource findUsagesWithinRange(String filePath, String codeSnippet) {
        AtomicInteger startOffset = new AtomicInteger();
        AtomicInteger endOffset = new AtomicInteger();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            startOffset.set(codeSnippetIndexGetterService.getStartIndexInFilePath(filePath, codeSnippet));
            endOffset.set(codeSnippetIndexGetterService.getEndIndexInFilePath(filePath, codeSnippet));
        });

        return findUsagesWithinRange(filePath, startOffset.get(), endOffset.get());
    }
}
