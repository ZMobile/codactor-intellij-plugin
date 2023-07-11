package com.translator.service.codactor.inquiry;

public interface InquirySystemMessageGeneratorService {
    String generateDefaultSystemMessage();

    String generateFunctionsSystemMessage();

    String generateSystemMessage(String model);
}
