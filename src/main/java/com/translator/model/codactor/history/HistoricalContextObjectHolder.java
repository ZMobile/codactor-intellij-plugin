package com.translator.model.codactor.history;

import com.translator.model.codactor.history.data.HistoricalContextObjectDataHolder;

public class HistoricalContextObjectHolder {
    private HistoricalContextInquiryHolder historicalContextInquiryHolder;
    private HistoricalContextModificationHolder historicalContextModificationHolder;
    private HistoricalContextObjectType historicalContextObjectType;

    public HistoricalContextObjectHolder(HistoricalContextInquiryHolder historicalContextInquiryHolder) {
        this.historicalContextInquiryHolder = historicalContextInquiryHolder;
        this.historicalContextModificationHolder = null;
        this.historicalContextObjectType = HistoricalContextObjectType.INQUIRY;
    }

    public HistoricalContextObjectHolder(HistoricalContextModificationHolder historicalContextModificationHolder) {
        this.historicalContextModificationHolder = historicalContextModificationHolder;
        this.historicalContextInquiryHolder = null;
        this.historicalContextObjectType = HistoricalContextObjectType.FILE_MODIFICATION;
    }

    public HistoricalContextObjectHolder() {
        this.historicalContextInquiryHolder = null;
        this.historicalContextModificationHolder = null;
        this.historicalContextObjectType = null;
    }

    public HistoricalContextObjectHolder(HistoricalContextObjectDataHolder historicalContextObjectDataHolder) {
        if (historicalContextObjectDataHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
            this.historicalContextModificationHolder = new HistoricalContextModificationHolder(historicalContextObjectDataHolder.getHistoricalCompletedModificationDataHolder());
            this.historicalContextInquiryHolder = null;
        } else {
            this.historicalContextInquiryHolder = new HistoricalContextInquiryHolder(historicalContextObjectDataHolder.getHistoricalContextInquiryDataHolder());
            this.historicalContextModificationHolder = null;
        }
        this.historicalContextObjectType = historicalContextObjectDataHolder.getHistoricalContextObjectType();
    }

    public HistoricalContextInquiryHolder getHistoricalContextInquiryHolder() {
        return historicalContextInquiryHolder;
    }

    public void setHistoricalContextInquiryHolder(HistoricalContextInquiryHolder historicalContextInquiryHolder) {
        this.historicalContextInquiryHolder = historicalContextInquiryHolder;
    }

    public HistoricalContextModificationHolder getHistoricalCompletedModificationHolder() {
        return historicalContextModificationHolder;
    }

    public void setHistoricalCompletedModificationHolder(HistoricalContextModificationHolder historicalContextModificationHolder) {
        this.historicalContextModificationHolder = historicalContextModificationHolder;
    }

    public HistoricalContextObjectType getHistoricalContextObjectType() {
        return historicalContextObjectType;
    }

    public void setHistoricalContextObjectType(HistoricalContextObjectType historicalContextObjectType) {
        this.historicalContextObjectType = historicalContextObjectType;
    }
}
