package com.translator.service.codactor.ai.chat.inquiry;

public interface InquirySystemMessageGeneratorService {
    String generateDefaultSystemMessage();

    String generateFunctionsSystemMessage();

    String generateSystemMessage(String model);
}
