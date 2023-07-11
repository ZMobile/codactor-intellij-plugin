package com.translator.service.codactor.inquiry.functions;

import com.translator.model.codactor.inquiry.function.ChatGptFunctionCall;

public interface InquiryFunctionCallProcessorService {
    String processFunctionCall(ChatGptFunctionCall functionCall);
}
