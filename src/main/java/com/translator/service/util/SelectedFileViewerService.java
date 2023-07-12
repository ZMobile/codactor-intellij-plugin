package com.translator.service.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;

public interface SelectedFileViewerService {
    VirtualFile getSelectedFileInEditor();

    VirtualFile getSelectedFileInTreeView();
}
