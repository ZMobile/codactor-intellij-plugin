package com.translator.service.codactor.code;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;

public interface CodeSnippetExtractorService {
    String getSnippet(String filePath, int startIndex, int endIndex);

    String getAllText(String filePath);

    SelectionModel getSelectedText(String filePath);

    String getSnippet(Editor editor, int startIndex, int endIndex);

    String getAllText(Editor editor);

    SelectionModel getSelectedText(Editor editor);
}
