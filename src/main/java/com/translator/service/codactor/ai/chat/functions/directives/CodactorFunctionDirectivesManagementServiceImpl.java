package com.translator.service.codactor.ai.chat.functions.directives;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.chat.function.GptFunction;
import com.translator.service.codactor.ai.chat.functions.CodactorFunctionGeneratorService;

import java.util.Comparator;
import java.util.List;

public class CodactorFunctionDirectivesManagementServiceImpl implements CodactorFunctionDirectivesManagementService {
    private final CodactorFunctionGeneratorService codactorFunctionGeneratorService;

    public CodactorFunctionDirectivesManagementServiceImpl(CodactorFunctionGeneratorService codactorFunctionGeneratorService) {
        this.codactorFunctionGeneratorService = codactorFunctionGeneratorService;
    }

    List<GptFunction> getDirectiveForMostRecentChat(Inquiry inquiry) {
        InquiryChat previousInquiryChat = inquiry.getChats().stream()
                .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                .orElseThrow();

        if (!previousInquiryChat.getFrom().equalsIgnoreCase("assistant")) {
            String previousInquiryChatId = previousInquiryChat.getPreviousInquiryChatId();
            previousInquiryChat = inquiry.getChats().stream()
                    .filter(chat -> chat.getId().equals(previousInquiryChatId))
                    .findFirst()
                    .orElseThrow();
        }
        return null;
    }


}
