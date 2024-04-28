package com.translator.model.codactor.ide.file;

public class FileItem {
    private String filePath;

    public FileItem(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
