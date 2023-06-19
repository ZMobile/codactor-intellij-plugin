package com.translator.service.codactor.file;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

public interface CodeFileGeneratorService {
    PsiElement createCodeFile(String fileName, String description, PsiDirectory directory);
}
