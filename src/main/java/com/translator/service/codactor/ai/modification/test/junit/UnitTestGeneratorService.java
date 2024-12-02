package com.translator.service.codactor.ai.modification.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;

public interface UnitTestGeneratorService {
    String generateUnitTestCode(Inquiry inquiry, InquiryChat testDescriptionsInquiryChat, String fileName, String packageName, String unitTestName, String unitTestDescription);
}
