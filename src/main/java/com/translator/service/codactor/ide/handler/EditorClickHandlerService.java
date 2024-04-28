package com.translator.service.codactor.ide.handler;

import com.intellij.openapi.editor.Editor;
import com.translator.model.codactor.ai.modification.FileModificationTracker;

public interface EditorClickHandlerService {
    void addEditorClickHandler(FileModificationTracker fileModificationTracker);

    void addEditorClickHandler(FileModificationTracker fileModificationTracker, Editor editor);

    void removeEditorClickHandler(String filePath);
}
