package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.function.directive.test.ReplacedClassInfoResource;
import com.translator.model.codactor.ai.chat.function.directive.test.ResultsResource;
import org.junit.runner.Result;

import java.util.List;
import java.util.Map;

public interface ImplementationFixerService {
    ReplacedClassInfoResource startFixing(String implementationFilePath, String interfaceFilePath, List<ResultsResource> resultsResources);
}
