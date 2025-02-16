package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.function.directive.test.ReplacedClassInfoResource;

public interface TestRunnerLoopService {
    void runUnitTestsAndGetFeedback(String directoryPath, String implementationFilePath, String interfaceFilePath, ReplacedClassInfoResource replacedClassInfoResource);
}
