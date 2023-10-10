package com.translator.model.codactor.functions.search;

public class SearchResult {
    private final String filePath;
    private final String text;

    public SearchResult(String filePath, String text) {
        this.filePath = filePath;
        this.text = text;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getText() {
        return text;
    }
}