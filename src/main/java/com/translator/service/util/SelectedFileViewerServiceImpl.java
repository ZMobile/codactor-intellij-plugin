package com.translator.service.util;

import com.google.inject.Inject;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;

public class SelectedFileViewerServiceImpl implements SelectedFileViewerService {
    private Project project;

    @Inject
    public SelectedFileViewerServiceImpl(Project project) {
        this.project = project;
    }

    public VirtualFile getSelectedFileInEditor() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        final VirtualFile[] selectedFiles = fileEditorManager.getSelectedFiles();
        if (selectedFiles.length > 0) {
            return fileEditorManager.getSelectedFiles()[0];
        }

        return null;
    }

    public VirtualFile getSelectedFileInTreeView() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        FileEditor[] fileEditors = fileEditorManager.getSelectedEditors();
        FileEditor fileEditor = fileEditors[0];
        return fileEditor.getFile();
    }
}
