package com.translator.service.file;

import com.intellij.openapi.project.Project;

public interface FileOpenerService {
    void openFileInEditor(String filePath);

    void openFileInEditor(String filePath, int startIndex);
}
