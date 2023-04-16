package com.translator.service.code;

import com.intellij.openapi.editor.SelectionModel;

public interface CodeSnippetExtractorService {
    String getSnippet(String filePath, int startIndex, int endIndex);

    String getAllText(String filePath);

    SelectionModel getSelectedText(String filePath);
}
