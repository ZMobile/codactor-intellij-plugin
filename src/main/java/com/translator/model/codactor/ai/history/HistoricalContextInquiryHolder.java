package com.translator.model.codactor.ai.history;

import com.translator.model.codactor.ai.history.data.HistoricalInquiryDataHolder;
import com.translator.model.codactor.ai.chat.InquiryChat;

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

    public HistoricalContextInquiryHolder(String inquiryId) {
        this.inquiryId = inquiryId;
    }

    public HistoricalContextInquiryHolder(HistoricalInquiryDataHolder historicalInquiryDataHolder) {
        this.inquiryId = historicalInquiryDataHolder.getInquiry().getId();
        this.previousChatId = historicalInquiryDataHolder.getPreviousChatId();
        this.startingChatId = historicalInquiryDataHolder.getStartingChatId();
        this.includePreviousContext = historicalInquiryDataHolder.includesPreviousContext();
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
