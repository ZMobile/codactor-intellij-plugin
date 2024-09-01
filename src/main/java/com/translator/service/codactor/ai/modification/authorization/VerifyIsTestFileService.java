package com.translator.service.codactor.ai.modification.authorization;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public interface VerifyIsTestFileService {
    boolean isTestFile(Project project, VirtualFile file);

    boolean isTestFile(Project project, String filePath);
}
