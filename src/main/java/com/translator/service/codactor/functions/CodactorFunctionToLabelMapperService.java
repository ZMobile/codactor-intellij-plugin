package com.translator.service.codactor.functions;

import com.translator.model.codactor.inquiry.InquiryChat;

public interface CodactorFunctionToLabelMapperService {
    String getLabel(InquiryChat inquiryChat);
}
