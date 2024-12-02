package com.translator.service.codactor.ai.modification.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.modification.test.UnitTestData;

import java.util.List;

public interface UnitTestListGeneratorService {
    List<UnitTestData> generateUnitTestList(Inquiry inquiry, InquiryChat interfaceInquiryChat);
}
