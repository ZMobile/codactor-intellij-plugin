package com.translator.service.codactor.file;

import com.intellij.openapi.project.Project;

import javax.inject.Inject;
import java.io.File;

public class FileRemoverServiceImpl implements FileRemoverService {
    private final Project project;

    @Inject
    public FileRemoverServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public void deleteCodeFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist or is not a regular file: " + filePath);
        }

        if (!file.delete()) {
            throw new RuntimeException("Failed to delete the file: " + filePath);
        }
    }
}