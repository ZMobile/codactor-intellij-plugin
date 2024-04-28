package com.translator.service.codactor.ai.chat.functions;

import com.translator.model.codactor.ai.chat.InquiryChat;

public interface CodactorFunctionToLabelMapperService {
    String getLabel(InquiryChat inquiryChat);
}
