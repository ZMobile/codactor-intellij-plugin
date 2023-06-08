package com.translator.model.codactor.modification;

public class UndoAction {
    private String formerContents;
    private int formerStartIndex;
    private int formerEndIndex;
    private String textInserted;

    public UndoAction(String formerContents, int formerStartIndex, int formerEndIndex, String textInserted) {
        this.formerContents = formerContents;
        this.formerStartIndex = formerStartIndex;
        this.formerEndIndex = formerEndIndex;
        this.textInserted = textInserted;
    }

    public String getFormerContents() {
        return formerContents;
    }

    public int getFormerStartIndex() {
        return formerStartIndex;
    }

    public int getFormerEndIndex() {
        return formerEndIndex;
    }

    public String getTextInserted() {
        return textInserted;
    }
}