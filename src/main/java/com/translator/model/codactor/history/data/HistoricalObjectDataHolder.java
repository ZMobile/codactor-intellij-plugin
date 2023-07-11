package com.translator.model.codactor.history.data;

import com.translator.model.codactor.history.HistoricalObjectType;

public class HistoricalObjectDataHolder {
    private HistoricalInquiryDataHolder historicalInquiryDataHolder;
    private HistoricalFileModificationDataHolder historicalFileModificationDataHolder;
    private HistoricalObjectType historicalObjectType;

    public HistoricalObjectDataHolder(HistoricalInquiryDataHolder historicalInquiryDataHolder) {
        this.historicalInquiryDataHolder = historicalInquiryDataHolder;
        this.historicalFileModificationDataHolder = null;
        this.historicalObjectType = HistoricalObjectType.INQUIRY;
    }

    public HistoricalObjectDataHolder(HistoricalFileModificationDataHolder historicalFileModificationDataHolder) {
        this.historicalFileModificationDataHolder = historicalFileModificationDataHolder;
        this.historicalInquiryDataHolder = null;
        this.historicalObjectType = HistoricalObjectType.FILE_MODIFICATION;
    }

    public HistoricalObjectDataHolder() {
        this.historicalInquiryDataHolder = null;
        this.historicalFileModificationDataHolder = null;
        this.historicalObjectType = null;
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

    public HistoricalObjectType getHistoricalContextObjectType() {
        return historicalObjectType;
    }

    public void setHistoricalContextObjectType(HistoricalObjectType historicalObjectType) {
        this.historicalObjectType = historicalObjectType;
    }
}
