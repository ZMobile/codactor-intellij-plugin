package com.translator.model.codactor.history.data;

import com.translator.model.codactor.inquiry.Inquiry;

public class HistoricalInquiryDataHolder {
    private Inquiry inquiry;
    private String previousChatId;
    private String startingChatId;
    private boolean includePreviousContext;

    public HistoricalInquiryDataHolder(Inquiry inquiry,
                                       String previousChatId,
                                       String startingChatId,
                                       boolean includePreviousContext) {
        this.inquiry = inquiry;
        this.previousChatId = previousChatId;
        this.startingChatId = startingChatId;
        this.includePreviousContext = includePreviousContext;
    }

    public HistoricalInquiryDataHolder(Inquiry inquiry) {
        this.inquiry = inquiry;
        this.includePreviousContext = false;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public String getPreviousChatId() {
        return previousChatId;
    }

    public void setPreviousChatId(String previousChatId) {
        this.previousChatId = previousChatId;
    }

    public String getStartingChatId() {
        return startingChatId;
    }

    public void setStartingChatId(String startingChatId) {
        this.startingChatId = startingChatId;
    }

    public boolean includesPreviousContext() {
        return includePreviousContext;
    }

    public void setIncludePreviousContext(boolean includePreviousContext) {
        this.includePreviousContext = includePreviousContext;
    }
}
