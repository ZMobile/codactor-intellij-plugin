package com.translator.service.codactor.editor.psi;

import com.intellij.find.FindManager;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.translator.model.codactor.editor.psi.UsageResult;
import com.translator.service.codactor.editor.CodeSnippetIndexGetterService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FindUsagesServiceImpl implements FindUsagesService {
    private final Project project;
    private final PsiElementCollectorService psiElementCollectorService;
    private final FindUsagesHandlerService findUsagesHandlerService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;

    @Inject
    private FindUsagesServiceImpl(Project project,
                                  PsiElementCollectorService psiElementCollectorService,
                                  FindUsagesHandlerService findUsagesHandlerService,
                                  CodeSnippetIndexGetterService codeSnippetIndexGetterService) {
        this.project = project;
        this.psiElementCollectorService = psiElementCollectorService;
        this.findUsagesHandlerService = findUsagesHandlerService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
    }

    public List<UsageResult> findUsagesWithinRange(String filePath, int startOffset, int endOffset) {
        List<UsageResult> results = new ArrayList<>();
        List<PsiElement> elementsInRange = psiElementCollectorService.getAllPsiElementsWithinRange(filePath, startOffset, endOffset);

        for (PsiElement element : elementsInRange) {
            FindUsagesHandler handler = findUsagesHandlerService.getFindUsagesHandlerForElement(element);
            if (handler != null) {
                for (PsiReference ref : handler.findReferencesToHighlight(element, GlobalSearchScope.projectScope(project))) {
                    PsiElement usageElement = ref.getElement();
                    results.add(new UsageResult(
                            usageElement.getContainingFile().getVirtualFile().getPath(),
                            usageElement.getText()
                    ));
                }
            }
        }

        return results;
    }

    @Override
    public List<UsageResult> findUsagesWithinRange(String filePath, String codeSnippet) {
        int startOffset = codeSnippetIndexGetterService.getStartIndexInFilePath(filePath, codeSnippet);
        int endOffset = codeSnippetIndexGetterService.getEndIndexInFilePath(filePath, codeSnippet);
        return findUsagesWithinRange(filePath, startOffset, endOffset);
    }
}
