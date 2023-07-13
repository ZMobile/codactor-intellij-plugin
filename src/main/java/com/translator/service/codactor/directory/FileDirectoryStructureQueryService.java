package com.translator.service.codactor.directory;

import java.io.File;

public interface FileDirectoryStructureQueryService {
    String getDirectoryStructureAsJson(String filePath, int depth);

    String searchForChildDirectory(String filePath, String childName, int depth);
}
