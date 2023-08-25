package com.translator.service.codactor.editor.diff;

import com.intellij.openapi.editor.Editor;

public interface DiffEditorGeneratorService {
    Editor createDiffEditor(String beforeCode, String afterCode);

    void updateDiffEditor(Editor editor, String beforeCode, String afterCode);

    Editor createDiffEditorWithMimickedIndentation(String beforeCode, String afterCode);
}
