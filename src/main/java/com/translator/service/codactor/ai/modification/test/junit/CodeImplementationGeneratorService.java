package com.translator.service.codactor.ai.modification.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;

public interface CodeImplementationGeneratorService {
    String generateImplementationCode(Inquiry inquiry, InquiryChat interfaceInquiryChat);
}
