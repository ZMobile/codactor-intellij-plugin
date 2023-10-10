package com.translator.service.codactor.editor.psi;

import com.intellij.psi.PsiFile;

public interface PsiFileService {
    PsiFile getPsiFileFromPath(String filePath);
}
