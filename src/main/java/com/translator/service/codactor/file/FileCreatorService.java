package com.translator.service.codactor.file;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileCreatorService {
    List<File> createFilesFromInput(String directoryPath, String input);

    File createFile(String directoryPath, String fileName) throws IOException;

    PsiFile createAndReturnPsiFile(String filePath);
}
