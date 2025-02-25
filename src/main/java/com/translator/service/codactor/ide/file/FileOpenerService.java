package com.translator.service.codactor.ide.file;

public interface FileOpenerService {
    void openFileInEditor(String filePath);

    void openFileInEditor(String filePath, int startIndex);

    void closeFileInEditor(String filePath);
}
