package com.translator.service.codactor.inquiry.functions;

import com.translator.model.codactor.inquiry.InquiryChat;

public interface CodactorFunctionToLabelMapperService {
    String getLabel(InquiryChat inquiryChat);
}
