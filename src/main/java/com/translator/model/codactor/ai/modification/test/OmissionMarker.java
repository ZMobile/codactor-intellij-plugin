package com.translator.model.codactor.ai.modification.test;

public class OmissionMarker {
    private int stringIndex;
    private int lineNumber;
    private int diffIndex;

    public OmissionMarker(int lineNumber,
                          int stringIndex,
                          int diffIndex) {
        this.lineNumber = lineNumber;
        this.stringIndex = stringIndex;
        this.diffIndex = diffIndex;
    }

    public int getStringIndex() {
        return stringIndex;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setStringIndex(int index) {
        this.stringIndex = index;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getDiffIndex() {
        return diffIndex;
    }

    public void setDiffIndex(int diffIndex) {
        this.diffIndex = diffIndex;
    }
}
