package com.translator.model.codactor.ai;

public class FileContentResponse {
    private String filePath;
    private String filePackage;
    private String content;

    public FileContentResponse(String filePath, String filePackage, String content) {
        this.filePath = filePath;
        this.filePackage = filePackage;
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePackage() {
        return filePackage;
    }

    public void setFilePackage(String filePackage) {
        this.filePackage = filePackage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
