package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.function.GptFunctionCall;

public interface TestDirectiveFunctionProcessorService {
    String processFunctionCall(GptFunctionCall functionCall, String inquiryId);
}
