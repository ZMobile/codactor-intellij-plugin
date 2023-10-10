package com.translator.service.codactor.editor.psi;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class PsiElementCollectorServiceImpl implements PsiElementCollectorService {
    private final PsiFileService psiFileService;

    @Inject
    public PsiElementCollectorServiceImpl(PsiFileService psiFileService) {
        this.psiFileService = psiFileService;
    }

    public List<PsiElement> getAllPsiElementsWithinRange(String filePath, int startOffset, int endOffset) {
        PsiFile psiFile = psiFileService.getPsiFileFromPath(filePath);
        List<PsiElement> elements = new ArrayList<>();

        PsiElement startElement = psiFile.findElementAt(startOffset);
        PsiElement endElement = psiFile.findElementAt(endOffset);

        if (startElement != null && endElement != null) {
            PsiElement commonParent = PsiTreeUtil.findCommonParent(startElement, endElement);

            if (commonParent != null) {
                collectElementsWithinRange(commonParent, startOffset, endOffset, elements);
            }
        }

        return elements;
    }

    private void collectElementsWithinRange(PsiElement element, int startOffset, int endOffset, List<PsiElement> elements) {
        if (element.getTextRange().getStartOffset() >= startOffset && element.getTextRange().getEndOffset() <= endOffset) {
            elements.add(element);
        }

        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            collectElementsWithinRange(child, startOffset, endOffset, elements);
        }
    }
}
