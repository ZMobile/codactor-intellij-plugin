package com.translator.service.codactor.file;

import java.io.File;
import java.util.List;

public interface FileCreatorService {
    List<File> createFilesFromInput(String directoryPath, String input);
}
