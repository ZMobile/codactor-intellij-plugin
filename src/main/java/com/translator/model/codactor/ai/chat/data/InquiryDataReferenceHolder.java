package com.translator.model.codactor.ai.chat.data;

import com.translator.model.codactor.ai.chat.Inquiry;

public class InquiryDataReferenceHolder {
    private String id;
    private String subjectLine;

    public InquiryDataReferenceHolder(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.subjectLine = inquiry.getSubjectLine();
    }

    public String getId() {
        return id;
    }

    public String getSubjectLine() {
        return subjectLine;
    }
}
