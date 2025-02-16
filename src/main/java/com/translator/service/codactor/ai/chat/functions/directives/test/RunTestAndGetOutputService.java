package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;

import com.translator.model.codactor.ai.chat.function.directive.test.ResultsResource;
import org.junit.runner.Result;

import java.util.List;
import java.util.Map;

public interface RunTestAndGetOutputService {
    String runTestAndGetOutput(CreateAndRunUnitTestDirectiveSession createAndRunUnitTestDirectiveSession) throws Exception;

    List<ResultsResource> runTestsAndGetOutputs(String implementationFilePath, List<String> unitTestFilePaths) throws Exception;
}