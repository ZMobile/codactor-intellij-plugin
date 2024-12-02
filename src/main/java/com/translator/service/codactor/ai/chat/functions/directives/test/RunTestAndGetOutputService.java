package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;

import java.util.List;

public interface RunTestAndGetOutputService {
    String runTestAndGetOutput(CreateAndRunUnitTestDirectiveSession createAndRunUnitTestDirectiveSession) throws Exception;

    List<String> runTestsAndGetOutputs(String implementationFilePath, List<String> unitTestFilePaths) throws Exception;
}