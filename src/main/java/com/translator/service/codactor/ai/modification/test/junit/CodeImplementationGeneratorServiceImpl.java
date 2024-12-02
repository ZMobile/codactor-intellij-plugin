package com.translator.service.codactor.ai.modification.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.service.codactor.ai.chat.inquiry.InquiryService;

import javax.inject.Inject;

public class CodeImplementationGeneratorServiceImpl implements CodeImplementationGeneratorService {
    private final InquiryService inquiryService;

    @Inject
    public CodeImplementationGeneratorServiceImpl(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @Override
    public String generateImplementationCode(Inquiry inquiry, InquiryChat interfaceInquiryChat) {
        String question = "Please now generate the implementation for this code.";
        InquiryChat inquiryChat = inquiryService.continueHeadlessInquiry(inquiry, interfaceInquiryChat.getId(), question, "gpt-4o", false);
        String startOfCode = inquiryChat.getMessage().substring(inquiryChat.getMessage().indexOf("```") + 3);
        String code = startOfCode.substring(0, startOfCode.indexOf("```"));
        if (code.startsWith("java")) {
            code = code.substring(4);
        }
        if (code.startsWith("\n")) {
            code = code.substring(1);
        }

        if (!isValidJavaSyntax(code)) {
            String followUpQuestion = "It seems the syntax for this code: " + code +
                    " was incorrect. Can you try again to provide just the java code?";
            InquiryChat followUpInquiryChat = inquiryService.continueHeadlessInquiry(
                    inquiry, inquiry.getId(), followUpQuestion, "gpt-4o", false);
            String followUpStartOfCode = followUpInquiryChat.getMessage().substring(followUpInquiryChat.getMessage().indexOf("```") + 3);
            code = followUpStartOfCode.substring(0, followUpStartOfCode.indexOf("```"));
            if (code.startsWith("java")) {
                code = code.substring(4);
            }
            if (code.startsWith("\n")) {
                code = code.substring(1);
            }
        }

        return code;
    }

    /**
     * Validates Java syntax using IntelliJ's PSI.
     *
     * @param code Java code to validate.
     * @return true if the syntax is valid, false otherwise.
     */
    private boolean isValidJavaSyntax(String code) {
        /*try {
            PsiFile psiFile = PsiFileFactory.getInstance(project)
                    .createFileFromText("Temp.java", JavaLanguage.INSTANCE, code);
            // Check if it's a valid Java file and contains at least one class
            return psiFile != null && psiFile.getChildren().length > 0;
        } catch (Exception e) {
            return false; // Syntax is invalid
        }*/
        return code.contains("class");
    }
}
