package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;

public interface RunTestAndGetOutputService {
    String runTestAndGetOutput(CreateAndRunUnitTestDirectiveSession createAndRunUnitTestDirectiveSession) throws Exception;
}
