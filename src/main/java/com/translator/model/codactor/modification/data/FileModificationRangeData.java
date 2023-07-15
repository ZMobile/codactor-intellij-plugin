package com.translator.model.codactor.modification.data;

public class FileModificationRangeData {
    private String modificationId;
    private String subjectLine;
    private int startLine;
    private String startRangeCode;
    private int endLine;
    private String endRangeCode;

    public FileModificationRangeData(String modificationId, String subjectLine, int startLine, String startRangeCode, int endLine, String endRangeCode) {
        this.modificationId = modificationId;
        this.subjectLine = subjectLine;
        this.startLine = startLine;
        this.startRangeCode = startRangeCode;
        this.endLine = endLine;
        this.endRangeCode = endRangeCode;
    }

    public String getModificationId() {
        return modificationId;
    }

    public void setModificationId(String modificationId) {
        this.modificationId = modificationId;
    }

    public String getSubjectLine() {
        return subjectLine;
    }

    public void setSubjectLine(String subjectLine) {
        this.subjectLine = subjectLine;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public String getStartRangeCode() {
        return startRangeCode;
    }

    public void setStartRangeCode(String startRangeCode) {
        this.startRangeCode = startRangeCode;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String getEndRangeCode() {
        return endRangeCode;
    }

    public void setEndRangeCode(String endRangeCode) {
        this.endRangeCode = endRangeCode;
    }
}
