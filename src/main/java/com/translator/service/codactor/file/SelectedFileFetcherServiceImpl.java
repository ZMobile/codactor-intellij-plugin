package com.translator.service.codactor.file;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;
import java.util.Arrays;

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
}
