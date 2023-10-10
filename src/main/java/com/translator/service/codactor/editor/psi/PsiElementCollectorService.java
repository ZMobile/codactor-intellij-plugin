package com.translator.service.codactor.editor.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

public interface PsiElementCollectorService {
    List<PsiElement> getAllPsiElementsWithinRange(String filePath, int startOffset, int endOffset);
}
