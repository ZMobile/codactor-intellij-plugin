package com.translator.service.modification.tracking.listener;

public interface EditorClickHandlerService {
    void addEditorClickHandler(String filePath);

    void removeEditorClickHandler(String filePath);
}
