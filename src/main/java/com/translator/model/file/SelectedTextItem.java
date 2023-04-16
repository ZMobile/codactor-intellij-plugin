package com.translator.model.file;

public class SelectedTextItem {
    private String filePath;
    private int startIndex;
    private int endIndex;
    private String selectedText;

    public SelectedTextItem(String filePath, int startIndex, int endIndex, String selectedText) {
        this.filePath = filePath;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.selectedText = selectedText;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String getSelectedText() {
        return selectedText;
    }
}
