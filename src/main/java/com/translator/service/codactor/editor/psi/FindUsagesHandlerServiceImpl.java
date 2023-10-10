package com.translator.service.codactor.editor.psi;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;

public class FindUsagesHandlerServiceImpl implements FindUsagesHandlerService {
    public FindUsagesHandler getFindUsagesHandlerForElement(PsiElement element) {
        for (FindUsagesHandlerFactory factory : FindUsagesHandlerFactory.EP_NAME.getExtensionList()) {
            if (factory.canFindUsages(element)) {
                return factory.createFindUsagesHandler(element, FindUsagesHandlerFactory.OperationMode.DEFAULT);
            }
        }
        return null;
    }
}
