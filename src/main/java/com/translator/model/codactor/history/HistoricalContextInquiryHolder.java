package com.translator.model.codactor.history;

import com.translator.model.codactor.history.data.HistoricalContextInquiryDataHolder;
import com.translator.model.codactor.inquiry.InquiryChat;

import java.util.ArrayList;
import java.util.List;

public class HistoricalContextInquiryHolder {
    private String inquiryId;
    private String previousChatId;
    private String startingChatId;
    private boolean includePreviousContext;
    private List<InquiryChat> requestedChats;

    public HistoricalContextInquiryHolder(String inquiryId,
                                               String previousChatId,
                                               String startingChatId,
                                               boolean includePreviousContext,
                                               List<InquiryChat> requestedChats) {
        this.inquiryId = inquiryId;
        this.previousChatId = previousChatId;
        this.startingChatId = startingChatId;
        this.includePreviousContext = includePreviousContext;
        this.requestedChats = requestedChats;
    }

    public HistoricalContextInquiryHolder(HistoricalContextInquiryDataHolder historicalContextInquiryDataHolder) {
        this.inquiryId = historicalContextInquiryDataHolder.getInquiry().getId();
        this.previousChatId = historicalContextInquiryDataHolder.getPreviousChatId();
        this.startingChatId = historicalContextInquiryDataHolder.getStartingChatId();
        this.includePreviousContext = historicalContextInquiryDataHolder.includesPreviousContext();
        this.requestedChats = new ArrayList<>();
    }

    public String getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(String inquiryId) {
        this.inquiryId = inquiryId;
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

    public boolean includePreviousContext() {
        return includePreviousContext;
    }

    public void setIncludePreviousContext(boolean includePreviousContext) {
        this.includePreviousContext = includePreviousContext;
    }

    public List<InquiryChat> getRequestedChats() {
        return requestedChats;
    }

    public void setRequestedChats(List<InquiryChat> requestedChats) {
        this.requestedChats = requestedChats;
    }
}
