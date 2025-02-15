package com.translator.service.codactor.ai.chat.functions.directives.test;

import java.io.File;

public class PackageFromFilePathRetrievalServiceImpl implements PackageFromFilePathRetrievalService {
    public String getPackageFromFilePath(String filePath) {
        // Find index of "src/main/java/" or "src/test/java/"
        String normalizedPath = filePath.replace(File.separator, "/");
        String[] possibleRoots = {"/src/main/java/", "/src/test/java/"};

        for (String root : possibleRoots) {
            int index = normalizedPath.indexOf(root);
            if (index != -1) {
                // Extract the part after the src directory
                String packagePath = normalizedPath.substring(index + root.length());
                // Remove file extension and convert to package format
                return packagePath.replace('/', '.').replace('\\', '.').replace(".java", "");
            }
        }

        // Return empty if not found
        return "";
    }
}
