package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;
import com.translator.model.codactor.ai.chat.function.directive.test.ResultsResource;
import org.junit.runner.Result;

import java.util.List;
import java.util.Map;

public interface CompileAndRunTestsService {
    //List<String> compileAndRunUnitTests(String implementationFilePath, List<String> unitTestFilePaths);

    //List<Result> compileAndRunUnitTests(String implementationFilePath, String directoryPath);

    List<ResultsResource> compileAndRunUnitTests(String interfaceFilePath, String implementationFilePath, String directoryPath);
}
