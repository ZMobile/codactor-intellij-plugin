package com.translator.service.file;

import com.intellij.openapi.project.Project;

public interface FileOpenerService {
    void openFileInEditor(Project project, String filePath);

    void openFileInEditor(Project project, String filePath, int startIndex);
}
