package com.translator.model.codactor.api.translator.inquiry;

import com.translator.model.codactor.ai.chat.Inquiry;

import java.util.List;

public class InquiryListResponseResource {
    List<Inquiry> inquiries;

    public InquiryListResponseResource(List<Inquiry> inquiries) {
        this.inquiries = inquiries;
    }

    public List<Inquiry> getInquiries() {
        return inquiries;
    }
}
