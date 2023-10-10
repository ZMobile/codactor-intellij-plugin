package com.translator.service.codactor.editor.psi;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.psi.PsiElement;

public interface FindUsagesHandlerService {
    FindUsagesHandler getFindUsagesHandlerForElement(PsiElement element);
}
