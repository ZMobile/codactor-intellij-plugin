package com.translator.service.codactor.ai.chat.functions;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.function.GptFunctionCall;

public interface InquiryFunctionCallProcessorService {
    String processFunctionCall(Inquiry inquiry, GptFunctionCall functionCall);

    String testMethod();
}
