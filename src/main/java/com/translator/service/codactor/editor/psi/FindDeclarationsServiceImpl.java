package com.translator.service.codactor.editor.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.translator.model.codactor.editor.psi.DeclarationResult;
import com.translator.service.codactor.editor.CodeSnippetIndexGetterService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FindDeclarationsServiceImpl implements FindDeclarationsService {
    private final PsiElementCollectorService psiElementCollectorService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;

    @Inject
    public FindDeclarationsServiceImpl(PsiElementCollectorService psiElementCollectorService,
                                       CodeSnippetIndexGetterService codeSnippetIndexGetterService) {
        this.psiElementCollectorService = psiElementCollectorService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
    }

    public List<DeclarationResult> findDeclarationsWithinRange(String filePath, int startOffset, int endOffset) {
        List<DeclarationResult> results = new ArrayList<>();
        List<PsiElement> elementsInRange = psiElementCollectorService.getAllPsiElementsWithinRange(filePath, startOffset, endOffset);

        for (PsiElement element : elementsInRange) {
            PsiReference ref = element.getReference();
            if (ref != null) {
                PsiElement declarationElement = ref.resolve();
                if (declarationElement != null) {
                    results.add(new DeclarationResult(
                            declarationElement.getContainingFile().getVirtualFile().getPath(),
                            declarationElement.getText()
                    ));
                }
            }
        }

        return results;
    }

    @Override
    public List<DeclarationResult> findDeclarationsWithinRange(String filePath, String codeSnippet) {
        int startOffset = codeSnippetIndexGetterService.getStartIndexInFilePath(filePath, codeSnippet);
        int endOffset = codeSnippetIndexGetterService.getEndIndexInFilePath(filePath, codeSnippet);
        return findDeclarationsWithinRange(filePath, startOffset, endOffset);
    }
}