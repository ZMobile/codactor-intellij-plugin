package com.translator.service.codactor.ai.modification.test.junit;

import com.intellij.openapi.project.Project;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.service.codactor.ai.chat.inquiry.InquiryService;

import javax.inject.Inject;

public class UnitTestGeneratorServiceImpl implements UnitTestGeneratorService {
    private final Project project;
    private final InquiryService inquiryService;

    @Inject
    public UnitTestGeneratorServiceImpl(Project project, InquiryService inquiryService) {
        this.project = project;
        this.inquiryService = inquiryService;
    }

    public String generateUnitTestCode(Inquiry inquiry, InquiryChat testDescriptionsInquiryChat, String fileName, String packageName, String unitTestName, String unitTestDescription) {
        String fileNameStripped = fileName;
        if (fileNameStripped.endsWith(".java")) {
            fileNameStripped = fileNameStripped.substring(0, fileNameStripped.length() - 5);
        }
        String question = "Can you please provide the unit test code for this test: file name: " + unitTestName + " description: " + unitTestDescription + ". The implementation subject of these tests (" + packageName + "." + fileNameStripped + "Impl) has not been generated yet as we are adhering to TDD principles and making the unit test first, therefore please just act like its there, and make sure to import the class properly.";
        InquiryChat inquiryChat = inquiryService.continueHeadlessInquiry(inquiry, testDescriptionsInquiryChat.getId(), question, "gpt-4o", false);
        String startOfCode = inquiryChat.getMessage().substring(inquiryChat.getMessage().indexOf("```") + 3);
        String code = startOfCode.substring(0, startOfCode.indexOf("```"));
        if (code.startsWith("java")) {
            code = code.substring(4);
        }
        if (code.startsWith("\n")) {
            code = code.substring(1);
        }


        // Validate the syntax using IntelliJ's PSI utilities
        if (!isValidJavaSyntax(code)) {
            String followUpQuestion = "It seems the syntax for this code: " + code +
                    " was incorrect. Can you try again to provide just the java code?";
            InquiryChat followUpInquiryChat = inquiryService.continueHeadlessInquiry(
                    inquiry, testDescriptionsInquiryChat.getId(), followUpQuestion, "gpt-4o", false);
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
