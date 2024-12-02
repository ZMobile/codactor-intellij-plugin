package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;

import java.util.List;

public interface CompileAndRunTestsService {
    List<String> compileAndRunUnitTests(String implementationFilePath, List<String> unitTestFilePaths);
}
