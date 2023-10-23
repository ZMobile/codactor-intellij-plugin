package com.translator.service.codactor.editor.psi;

import com.intellij.psi.*;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;

import java.util.Collection;
import java.util.List;

public interface PsiElementCollectorService {
    //List<PsiElement> getAllPsiElementsWithinRange(String filePath, int startOffset, int endOffset);

    List<PsiElement> collectMethodsAndVariables(String filePath, int startIndex, int endIndex);

    PsiMethod findMethodDeclaration(PsiMethodCallExpression methodCallExpression);

    Collection<PsiMethod> findImplementations(PsiMethod method);

    Collection<PsiReference> findMethodReferences(PsiMethod psiMethod);

    Collection<PsiReference> findClassReferences(PsiClass psiClass);

    PsiClass findReferencedClassOfField(PsiField psiField);

    PsiClass findClassOfField(PsiField psiField);

    Collection<PsiReference> findFieldReferences(PsiField psiField);
}
