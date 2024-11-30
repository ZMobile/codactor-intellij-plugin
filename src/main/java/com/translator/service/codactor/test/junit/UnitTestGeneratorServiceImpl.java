package com.translator.service.codactor.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.test.UnitTestData;
import com.translator.service.codactor.ai.chat.inquiry.InquiryService;

import javax.inject.Inject;

public class UnitTestGeneratorServiceImpl implements UnitTestGeneratorService {
    private final InquiryService inquiryService;

    @Inject
    public UnitTestGeneratorServiceImpl(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    public String generateUnitTestCode(Inquiry inquiry, InquiryChat testDescriptionsInquiryChat, String fileName, String unitTestName, String unitTestDescription) {
        String fileNameStripped = fileName;
        if (fileNameStripped.endsWith(".java")) {
            fileNameStripped = fileNameStripped.substring(0, fileNameStripped.length() - 5);
        }
        String question = "Can you please provide the unit test code for this test: file name: " + unitTestName + " description: " + unitTestDescription + ". The implementation subject of these tests (" + fileNameStripped + "Impl) has not been generated yet, so please just leave out the import for that and act like its there.";
        InquiryChat inquiryChat = inquiryService.continueHeadlessInquiry(inquiry, testDescriptionsInquiryChat.getId(), question, "gpt-4o", false);
        String startOfCode = inquiryChat.getMessage().substring(inquiryChat.getMessage().indexOf("```") + 3);
        String code = startOfCode.substring(0, startOfCode.indexOf("```"));
        if (code.startsWith("java")) {
            code = code.substring(4);
        }
        if (code.startsWith("\n")) {
            code = code.substring(1);
        }
        return code;
    }
}
