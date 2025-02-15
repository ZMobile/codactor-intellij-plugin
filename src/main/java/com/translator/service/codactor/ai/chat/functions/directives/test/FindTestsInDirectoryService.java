package com.translator.service.codactor.ai.chat.functions.directives.test;

import java.util.List;

public interface FindTestsInDirectoryService {
    List<String> findTestsInDirectory(String directoryPath);
}
