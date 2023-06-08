package com.translator.service.codactor.file;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import javax.inject.Inject;

public class FileOpenerServiceImpl implements FileOpenerService {
    private Project project;

    @Inject
    public FileOpenerServiceImpl(Project project) {
        this.project = project;
    }

    public void openFileInEditor(String filePath) {
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        VirtualFile virtualFile = virtualFileManager.findFileByUrl("file://" + filePath);

        if (virtualFile != null) {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(virtualFile, true);
        }
    }

    public void openFileInEditor(String filePath, int startIndex) {
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        VirtualFile virtualFile = virtualFileManager.findFileByUrl("file://" + filePath);

        if (virtualFile != null) {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(virtualFile, true);
            FileEditor fileEditor = fileEditorManager.getSelectedEditor(virtualFile);

            if (fileEditor instanceof TextEditor) {
                Editor editor = ((TextEditor) fileEditor).getEditor();
                CaretModel caretModel = editor.getCaretModel();
                Document document = editor.getDocument();
                int lineNumber = document.getLineNumber(startIndex);
                int lineStartOffset = document.getLineStartOffset(lineNumber);

                caretModel.moveToOffset(lineStartOffset);
            }
        }
    }
}
