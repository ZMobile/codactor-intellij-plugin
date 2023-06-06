package com.translator.service.uml;

import com.google.inject.Inject;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;
import com.translator.model.uml.node.PromptNode;
import com.translator.model.uml.prompt.Prompt;
import com.translator.service.inquiry.InquiryService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;

import java.util.Comparator;

public class PromptNodeDialogRunnerServiceImpl implements PromptNodeDialogRunnerService {
    private final InquiryDao inquiryDao;
    private OpenAiApiKeyService openAiApiKeyService;

    @Inject
    public PromptNodeDialogRunnerServiceImpl(InquiryDao inquiryDao,
                                             OpenAiApiKeyService openAiApiKeyService) {
        this.inquiryDao = inquiryDao;
        this.openAiApiKeyService = openAiApiKeyService;
    }

    @Override
    public void run(PromptNode promptNode, String model) {
        if (promptNode.isRunning()) {
            return;
        }
        promptNode.getActiveInquiryList().clear();
        promptNode.setRunning(true);
        Inquiry inquiry;
        InquiryChat previousInquiryChat = null;
        for (int i = 0; i < promptNode.getPromptList().size(); i++) {
            String prompt = promptNode.getPromptList().get(i).getPrompt();
            if (i == 0) {
                inquiry = inquiryDao.createGeneralInquiry(prompt, openAiApiKeyService.getOpenAiApiKey(), model);
            } else {
                inquiry = inquiryDao.continueInquiry(previousInquiryChat.getId(), prompt, openAiApiKeyService.getOpenAiApiKey(), model);
            }
            previousInquiryChat = inquiry.getChats().stream()
                    .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                    .orElseThrow();
            int inquiryIndex = promptNode.getActiveInquiryList().indexOf(inquiry);
            if (inquiryIndex == -1) {
                promptNode.getActiveInquiryList().add(inquiry);
            } else {
                promptNode.getActiveInquiryList().set(inquiryIndex, inquiry);
            }
        }
    }
}
