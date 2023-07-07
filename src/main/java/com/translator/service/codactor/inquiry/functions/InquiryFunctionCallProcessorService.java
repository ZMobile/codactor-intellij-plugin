package com.translator.service.codactor.inquiry.functions;

import com.translator.model.codactor.api.translator.inquiry.function.ChatGptFunctionCall;

public interface InquiryFunctionCallProcessorService {
    String processFunctionCall(ChatGptFunctionCall functionCall);

    void test();
}
