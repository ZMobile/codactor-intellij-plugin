package com.translator.service.codactor.ide.editor.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import javax.inject.Inject;

public class PsiFileServiceImpl implements PsiFileService {
    private final Project project;

    @Inject
    public PsiFileServiceImpl(Project project) {
        this.project = project;
    }

    public PsiFile getPsiFileFromPath(String filePath) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (virtualFile == null) {
            return null;
        }

        return PsiManager.getInstance(project).findFile(virtualFile);
    }
}