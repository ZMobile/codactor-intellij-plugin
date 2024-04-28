package com.translator.model.codactor.ide.file.search;

import com.intellij.openapi.util.TextRange;

public class SearchResult {
    private final String filePath;
    private final String text;
    private final TextRange textRange;

    public SearchResult(String filePath, String text, TextRange textRange) {
        this.filePath = filePath;
        this.text = text;
        this.textRange = textRange;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getText() {
        return text;
    }

    public TextRange getTextRange() {
        return textRange;
    }
}