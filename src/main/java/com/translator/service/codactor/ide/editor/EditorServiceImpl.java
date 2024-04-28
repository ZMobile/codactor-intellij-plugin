package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class EditorServiceImpl implements EditorService {
    private Project project;

    @Inject
    public EditorServiceImpl(Project project) {
        this.project = project;
    }

    public Editor getEditor(String filePath) {
        FileEditor[] allEditors = FileEditorManager.getInstance(project).getAllEditors();

        for (FileEditor fileEditor : allEditors) {
            VirtualFile file = fileEditor.getFile();

            if (file != null && filePath.equals(file.getPath())) {
                if (fileEditor instanceof TextEditor) {
                    return ((TextEditor) fileEditor).getEditor();
                }
            }
        }

        return null;
    }

    @Override
    public List<Editor> getAllEditors() {
        List<Editor> editorList = new ArrayList<>();
        FileEditor[] allEditors = FileEditorManager.getInstance(project).getAllEditors();

        for (FileEditor fileEditor : allEditors) {
            VirtualFile file = fileEditor.getFile();
            if (file != null) {
                System.out.println("File: " + file.getPath());
            }
            if (fileEditor instanceof TextEditor) {
                editorList.add(((TextEditor) fileEditor).getEditor());
            }
        }
        return editorList;
    }
}