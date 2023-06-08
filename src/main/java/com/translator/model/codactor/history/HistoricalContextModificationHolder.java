package com.translator.model.codactor.history;


import com.translator.model.codactor.history.data.HistoricalContextModificationDataHolder;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.modification.RecordType;

import java.util.ArrayList;
import java.util.List;

public class HistoricalContextModificationHolder {
    private String subjectRecordId;
    private RecordType recordType;
    private boolean includePreviousContext;
    private List<InquiryChat> requestedChats;

    public HistoricalContextModificationHolder(String subjectRecordId, RecordType recordType, boolean includePreviousContext, List<InquiryChat> requestedChats) {
        this.subjectRecordId = subjectRecordId;
        this.recordType = recordType;
        this.includePreviousContext = includePreviousContext;
        this.requestedChats = requestedChats;
    }

    public HistoricalContextModificationHolder(HistoricalContextModificationDataHolder historicalContextModificationDataHolder) {
        if (historicalContextModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
            this.subjectRecordId = historicalContextModificationDataHolder.getFileModificationSuggestionRecord().getId();
        } else if (historicalContextModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
            this.subjectRecordId = historicalContextModificationDataHolder.getFileModificationSuggestionModificationRecord().getId();
        }
        this.recordType = historicalContextModificationDataHolder.getRecordType();
        this.includePreviousContext = historicalContextModificationDataHolder.includesPreviousContext();
        this.requestedChats = new ArrayList<>();
    }

    public String getSubjectRecordId() {
        return subjectRecordId;
    }

    public boolean includesPreviousContext() {
        return includePreviousContext;
    }

    public void setIncludePreviousContext(boolean includePreviousContext) {
        this.includePreviousContext = includePreviousContext;
    }

    public void setSubjectRecordId(String subjectRecordId) {
        this.subjectRecordId = subjectRecordId;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public List<InquiryChat> getRequestedChats() {
        return requestedChats;
    }

    public void setRequestedChats(List<InquiryChat> requestedChats) {
        this.requestedChats = requestedChats;
    }
}
