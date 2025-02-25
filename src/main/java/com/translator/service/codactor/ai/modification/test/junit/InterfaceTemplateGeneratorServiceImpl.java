package com.translator.service.codactor.ai.modification.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.service.codactor.ai.chat.inquiry.InquiryService;

import javax.inject.Inject;

public class InterfaceTemplateGeneratorServiceImpl implements InterfaceTemplateGeneratorService {
    private final InquiryService inquiryService;

    @Inject
    public InterfaceTemplateGeneratorServiceImpl(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @Override
    public Inquiry generateInterfaceTemplate(String interfaceName, String filePath, String description) {
        String packageName = filePath.substring(filePath.indexOf("java/") + 5/*, filePath.lastIndexOf("/")*/).replace("/", ".");
        String question = "Please complete just the interface for this code. The following is the description:"
                + description + " interface: \"package " + packageName + "; public interface " + interfaceName + " { }\"";
        return inquiryService.createHeadlessInquiry(question, "gpt-4o", false);
    }
}
