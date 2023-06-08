package com.translator.service.codactor.file;

public interface FileOpenerService {
    void openFileInEditor(String filePath);

    void openFileInEditor(String filePath, int startIndex);
}
