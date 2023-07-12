package com.translator.service.util;

import com.google.inject.Inject;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class SelectedFileViewerServiceImpl implements SelectedFileViewerService {
    private Project project;

    @Inject
    public SelectedFileViewerServiceImpl(Project project) {
        this.project = project;
    }

    public VirtualFile getSelectedFileInEditor() {
        final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        return fileEditorManager.getSelectedFiles()[0];
    }

    public VirtualFile getSelectedFileInTreeView() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        FileEditor[] fileEditors = fileEditorManager.getSelectedEditors();
        FileEditor fileEditor = fileEditors[0];
        return fileEditor.getFile();
    }
}
