package com.translator.model.codactor.inquiry.data;

import com.translator.model.codactor.inquiry.Inquiry;

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
