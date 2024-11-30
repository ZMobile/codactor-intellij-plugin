package com.translator.service.codactor.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.test.UnitTestData;

public interface UnitTestGeneratorService {
    String generateUnitTestCode(Inquiry inquiry, InquiryChat testDescriptionsInquiryChat, String fileName, String unitTestName, String unitTestDescription);
}
