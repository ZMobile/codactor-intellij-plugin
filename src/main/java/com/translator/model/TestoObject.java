package com.translator.model;

import com.translator.model.codactor.inquiry.InquiryChat;

import java.util.List;

public class TestoObject {
    private InquiryChat nonFunctionChat;
    private List<InquiryChat> functionChats;

    public TestoObject(InquiryChat nonFunctionRelated, List<InquiryChat> functionRelated) {
        this.nonFunctionChat = nonFunctionRelated;
        this.functionChats = functionRelated;
    }
}
