package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    public Editor getEditorHeadless(String filePath) {
        AtomicReference<Editor> editor = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            // Try to fetch the file directly by path
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile != null) {
                Editor editor1 = null;
                // Check if it's already open
                FileEditor[] allEditors = FileEditorManager.getInstance(project).getEditors(virtualFile);
                for (FileEditor fileEditor : allEditors) {
                    if (fileEditor instanceof TextEditor) {
                        editor1 = ((TextEditor) fileEditor).getEditor();
                    }
                }

                // If not open, create a new editor
                Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                if (editor1 == null && document != null) {
                    editor1 = EditorFactory.getInstance().createEditor(document, project);
                }
                editor.set(editor1);
            }
        });

        return editor.get(); // File not found or unable to create an editor
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