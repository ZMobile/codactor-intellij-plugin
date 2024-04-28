package com.translator.service.codactor.ai.chat.functions;

import com.translator.model.codactor.ai.chat.function.GptFunctionCall;

public interface InquiryFunctionCallProcessorService {
    String processFunctionCall(GptFunctionCall functionCall);

    String testMethod();
}
