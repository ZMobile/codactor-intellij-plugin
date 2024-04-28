package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.editor.Editor;

public interface RangeReplaceService {
    void replaceRange(String filePath, int startOffset, int endOffset, String replacementString, boolean silent);

    void replaceRange(Editor editor, int startOffset, int endOffset, String replacementString);
}
