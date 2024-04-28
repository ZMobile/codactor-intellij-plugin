package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.editor.Editor;

import java.util.List;

public interface EditorService {
    Editor getEditor(String filePath);

    List<Editor> getAllEditors();
}
