package com.translator.service.codactor.ide.file;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SelectedFileFetcherServiceImpl implements SelectedFileFetcherService {
    private final Project project;

    @Inject
    public SelectedFileFetcherServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public VirtualFile[] getCurrentlySelectedFiles() {
        if (project != null) {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            return fileEditorManager.getSelectedFiles();
        }
        return null;
    }

    @Override
    public VirtualFile[] getOpenFiles() {
        if (project != null) {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            return fileEditorManager.getOpenFiles();
        }
        return null;
    }

    public VirtualFile getSelectedFileInTreeView() {
        if (project != null) {
            PsiDirectory[] psiDirectories = ProjectView.getInstance(project).getCurrentProjectViewPane().getSelectedDirectories();
            for (PsiDirectory psiDirectory : psiDirectories) {
                PsiDirectoryNode psiDirectoryNode = new PsiDirectoryNode(project, psiDirectory, new ViewSettings() {
                    @Override
                    public boolean isFoldersAlwaysOnTop() {
                        return ViewSettings.super.isFoldersAlwaysOnTop();
                    }
                });
                return psiDirectoryNode.getVirtualFile();
            }
        }
        return null;
    }

    @Override
    public VirtualFile[] getSelectedFilesInTreeView() {
        if (project != null) {
            AbstractProjectViewPane pane = ProjectView.getInstance(project).getCurrentProjectViewPane();
            PsiElement[] psiElements = pane.getSelectedPSIElements();
            List<VirtualFile> selectedFiles = new ArrayList<>();
            for (PsiElement psiElement : psiElements) {
                System.out.println(psiElement);
                if (psiElement instanceof PsiFile) {
                    selectedFiles.add(((PsiFile) psiElement).getVirtualFile());
                } else if (psiElement instanceof PsiClass) {
                    selectedFiles.add(psiElement.getContainingFile().getVirtualFile());
                } else if (psiElement instanceof PsiDirectory) {
                    selectedFiles.add(((PsiDirectory) psiElement).getVirtualFile());
                }
            }
            return selectedFiles.toArray(new VirtualFile[0]);
        }
        return null;
    }
}