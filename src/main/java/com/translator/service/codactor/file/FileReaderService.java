package com.translator.service.codactor.file;

import com.intellij.openapi.project.Project;

public interface FileReaderService {
    String readFileContent(Project project, String filePath);
}
