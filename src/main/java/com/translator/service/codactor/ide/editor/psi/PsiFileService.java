package com.translator.service.codactor.ide.editor.psi;

import com.intellij.psi.PsiFile;

public interface PsiFileService {
    PsiFile getPsiFileFromPath(String filePath);
}
