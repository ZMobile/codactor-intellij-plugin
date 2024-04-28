package com.translator.service.codactor.ide.file;

import javax.inject.Inject;
import java.io.File;

public class FileTranslatorServiceImpl implements FileTranslatorService {
    private final RenameFileService renameFileService;

    @Inject
    public FileTranslatorServiceImpl(RenameFileService renameFileService) {
        this.renameFileService = renameFileService;
    }

    @Override
    public void translateFile(String filePath, String newFileType) {
            File file = new File(filePath);
        String fileNameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.'));
        String adjustedFileType;
        if (newFileType.startsWith(".")){
            adjustedFileType = newFileType;
        } else {
                adjustedFileType = "." + newFileType;
        }
        String newFileName = fileNameWithoutExtension + adjustedFileType;
        renameFileService.renameFile(filePath, newFileName);
    }
}
