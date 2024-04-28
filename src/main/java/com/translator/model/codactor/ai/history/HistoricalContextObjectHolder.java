package com.translator.model.codactor.ai.history;

import com.translator.model.codactor.ai.history.data.HistoricalObjectDataHolder;

public class HistoricalContextObjectHolder {
    private HistoricalContextInquiryHolder historicalContextInquiryHolder;
    private HistoricalContextFileModificationHolder historicalContextFileModificationHolder;
    private HistoricalContextObjectType historicalContextObjectType;

    public HistoricalContextObjectHolder(HistoricalContextInquiryHolder historicalContextInquiryHolder) {
        this.historicalContextInquiryHolder = historicalContextInquiryHolder;
        this.historicalContextFileModificationHolder = null;
        this.historicalContextObjectType = HistoricalContextObjectType.INQUIRY;
    }

    public HistoricalContextObjectHolder(HistoricalContextFileModificationHolder historicalContextFileModificationHolder) {
        this.historicalContextFileModificationHolder = historicalContextFileModificationHolder;
        this.historicalContextInquiryHolder = null;
        this.historicalContextObjectType = HistoricalContextObjectType.FILE_MODIFICATION;
    }

    public HistoricalContextObjectHolder() {
        this.historicalContextInquiryHolder = null;
        this.historicalContextFileModificationHolder = null;
        this.historicalContextObjectType = null;
    }

    public HistoricalContextObjectHolder(HistoricalObjectDataHolder historicalObjectDataHolder) {
        if (historicalObjectDataHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
            this.historicalContextFileModificationHolder = new HistoricalContextFileModificationHolder(historicalObjectDataHolder.getHistoricalModificationDataHolder());
            this.historicalContextInquiryHolder = null;
        } else {
            this.historicalContextInquiryHolder = new HistoricalContextInquiryHolder(historicalObjectDataHolder.getHistoricalContextInquiryDataHolder());
            this.historicalContextFileModificationHolder = null;
        }
        this.historicalContextObjectType = historicalObjectDataHolder.getHistoricalContextObjectType();
    }

    public HistoricalContextInquiryHolder getHistoricalContextInquiryHolder() {
        return historicalContextInquiryHolder;
    }

    public void setHistoricalContextInquiryHolder(HistoricalContextInquiryHolder historicalContextInquiryHolder) {
        this.historicalContextInquiryHolder = historicalContextInquiryHolder;
    }

    public HistoricalContextFileModificationHolder getHistoricalModificationHolder() {
        return historicalContextFileModificationHolder;
    }

    public void setHistoricalModificationHolder(HistoricalContextFileModificationHolder historicalContextFileModificationHolder) {
        this.historicalContextFileModificationHolder = historicalContextFileModificationHolder;
    }

    public HistoricalContextObjectType getHistoricalContextObjectType() {
        return historicalContextObjectType;
    }

    public void setHistoricalContextObjectType(HistoricalContextObjectType historicalContextObjectType) {
        this.historicalContextObjectType = historicalContextObjectType;
    }
}
