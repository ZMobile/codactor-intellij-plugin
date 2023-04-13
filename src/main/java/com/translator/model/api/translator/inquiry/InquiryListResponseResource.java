package com.translator.model.api.translator.inquiry;

import com.translator.model.inquiry.Inquiry;

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
