package com.translator.service.code;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public interface RangeReplaceService {
    void replaceRange(String filePath, int startOffset, int endOffset, String replacementString);

    void replaceRange(Editor editor, int startOffset, int endOffset, String replacementString);
}
