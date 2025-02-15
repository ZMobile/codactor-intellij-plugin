package com.translator.service.codactor.ai.chat.functions.directives.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FindTestsInDirectoryServiceImpl implements FindTestsInDirectoryService {
    @Override
    public List<String> findTestsInDirectory(String directoryPath) {
        //Get all child files in directory and return ones that end with Test.java
        List<String> testFilePaths = new ArrayList<>();
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("Test.java")) {
                testFilePaths.add(file.getAbsolutePath());
            }
        }
        return testFilePaths;
    }
}
