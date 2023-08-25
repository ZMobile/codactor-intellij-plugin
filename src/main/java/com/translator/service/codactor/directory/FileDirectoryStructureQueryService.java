package com.translator.service.codactor.directory;

public interface FileDirectoryStructureQueryService {
    String getDirectoryStructureAsJson(String filePath, int depth);

    String searchForChildDirectory(String filePath, String childName, int depth);
}
