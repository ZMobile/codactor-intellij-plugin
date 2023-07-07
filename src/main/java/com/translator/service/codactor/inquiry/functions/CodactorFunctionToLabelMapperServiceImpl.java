package com.translator.service.codactor.inquiry.functions;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.translator.model.codactor.api.translator.inquiry.function.ChatGptFunctionCall;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.service.codactor.json.JsonExtractorService;

public class CodactorFunctionToLabelMapperServiceImpl implements CodactorFunctionToLabelMapperService {
    private Gson gson;

    @Inject
    public CodactorFunctionToLabelMapperServiceImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String getLabel(InquiryChat inquiryChat) {
        if (inquiryChat.getFrom().equalsIgnoreCase("function")) {
            return "Done";
        }
        ChatGptFunctionCall functionCall = inquiryChat.getFunctionCall();
        if (functionCall.getName().equals("read_file_at_package")) {
            String packageName = JsonExtractorService.extractField(functionCall.getArguments(), "package");
            return "Reading file at package " + packageName + "...";
        }
        return null;
    }
}
