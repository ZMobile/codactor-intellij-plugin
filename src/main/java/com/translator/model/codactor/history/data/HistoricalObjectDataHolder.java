package com.translator.model.codactor.history.data;

import com.translator.model.codactor.history.HistoricalContextObjectType;

public class HistoricalObjectDataHolder {
    private HistoricalInquiryDataHolder historicalInquiryDataHolder;
    private HistoricalFileModificationDataHolder historicalFileModificationDataHolder;
    private HistoricalContextObjectType historicalContextObjectType;

    public HistoricalObjectDataHolder(HistoricalInquiryDataHolder historicalInquiryDataHolder) {
        this.historicalInquiryDataHolder = historicalInquiryDataHolder;
        this.historicalFileModificationDataHolder = null;
        this.historicalContextObjectType = HistoricalContextObjectType.INQUIRY;
    }

    public HistoricalObjectDataHolder(HistoricalFileModificationDataHolder historicalFileModificationDataHolder) {
        this.historicalFileModificationDataHolder = historicalFileModificationDataHolder;
        this.historicalInquiryDataHolder = null;
        this.historicalContextObjectType = HistoricalContextObjectType.FILE_MODIFICATION;
    }

    public HistoricalObjectDataHolder() {
        this.historicalInquiryDataHolder = null;
        this.historicalFileModificationDataHolder = null;
        this.historicalContextObjectType = null;
    }

    public HistoricalInquiryDataHolder getHistoricalContextInquiryDataHolder() {
        return historicalInquiryDataHolder;
    }

    public void setHistoricalContextInquiryDataHolder(HistoricalInquiryDataHolder historicalInquiryDataHolder) {
        this.historicalInquiryDataHolder = historicalInquiryDataHolder;
    }

    public HistoricalFileModificationDataHolder getHistoricalModificationDataHolder() {
        return historicalFileModificationDataHolder;
    }

    public void setHistoricalModificationDataHolder(HistoricalFileModificationDataHolder historicalFileModificationDataHolder) {
        this.historicalFileModificationDataHolder = historicalFileModificationDataHolder;
    }

    public HistoricalContextObjectType getHistoricalContextObjectType() {
        return historicalContextObjectType;
    }

    public void setHistoricalContextObjectType(HistoricalContextObjectType historicalContextObjectType) {
        this.historicalContextObjectType = historicalContextObjectType;
    }
}
