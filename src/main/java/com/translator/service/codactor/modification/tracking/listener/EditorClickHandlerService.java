package com.translator.service.codactor.modification.tracking.listener;

import com.intellij.openapi.editor.Editor;

public interface EditorClickHandlerService {
    void addEditorClickHandler(String filePath);

    void addEditorClickHandler(String filePath, Editor editor);

    void removeEditorClickHandler(String filePath);
}
