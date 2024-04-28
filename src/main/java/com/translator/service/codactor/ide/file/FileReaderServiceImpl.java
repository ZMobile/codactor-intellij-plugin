package com.translator.service.codactor.ide.file;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.io.IOException;

public class FileReaderServiceImpl implements FileReaderService {

    public String readFileContent(Project project, String filePath) {
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        VirtualFile virtualFile = virtualFileManager.findFileByUrl("file://" + filePath);

        if (virtualFile != null) {
            try {
                return new String(virtualFile.contentsToByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
