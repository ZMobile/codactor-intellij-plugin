package com.translator.model.history.data;

import com.translator.model.history.HistoricalContextObjectType;

public class HistoricalContextObjectDataHolder {
    private HistoricalContextInquiryDataHolder historicalContextInquiryDataHolder;
    private HistoricalContextModificationDataHolder historicalContextModificationDataHolder;
    private HistoricalContextObjectType historicalContextObjectType;

    public HistoricalContextObjectDataHolder(HistoricalContextInquiryDataHolder historicalContextInquiryDataHolder) {
        this.historicalContextInquiryDataHolder = historicalContextInquiryDataHolder;
        this.historicalContextModificationDataHolder = null;
        this.historicalContextObjectType = HistoricalContextObjectType.INQUIRY;
    }

    public HistoricalContextObjectDataHolder(HistoricalContextModificationDataHolder historicalContextModificationDataHolder) {
        this.historicalContextModificationDataHolder = historicalContextModificationDataHolder;
        this.historicalContextInquiryDataHolder = null;
        this.historicalContextObjectType = HistoricalContextObjectType.FILE_MODIFICATION;
    }

    public HistoricalContextObjectDataHolder() {
        this.historicalContextInquiryDataHolder = null;
        this.historicalContextModificationDataHolder = null;
        this.historicalContextObjectType = null;
    }

    public HistoricalContextInquiryDataHolder getHistoricalContextInquiryDataHolder() {
        return historicalContextInquiryDataHolder;
    }

    public void setHistoricalContextInquiryDataHolder(HistoricalContextInquiryDataHolder historicalContextInquiryDataHolder) {
        this.historicalContextInquiryDataHolder = historicalContextInquiryDataHolder;
    }

    public HistoricalContextModificationDataHolder getHistoricalCompletedModificationDataHolder() {
        return historicalContextModificationDataHolder;
    }

    public void setHistoricalCompletedModificationDataHolder(HistoricalContextModificationDataHolder historicalContextModificationDataHolder) {
        this.historicalContextModificationDataHolder = historicalContextModificationDataHolder;
    }

    public HistoricalContextObjectType getHistoricalContextObjectType() {
        return historicalContextObjectType;
    }

    public void setHistoricalContextObjectType(HistoricalContextObjectType historicalContextObjectType) {
        this.historicalContextObjectType = historicalContextObjectType;
    }
}
