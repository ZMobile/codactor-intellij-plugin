package com.translator.service.codactor.ide.editor.psi;

import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleFindUsagesProvider implements FindUsagesProvider {
    public SimpleFindUsagesProvider() {
        super();
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        // Replace this placeholder with the type of the PSI Element
        return "PSI Element type";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        // Replace this placeholder with the descriptive name of the PSI Element
        return "Descriptive name of the PSI Element";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        // Replace this placeholder with the node text of the PSI Element
        return "Node text of the PSI element";
    }

    // The rest of the implementation goes here...
}
