package com.translator.model.codactor.history;

import com.translator.model.codactor.history.data.HistoricalObjectDataHolder;

public class HistoricalContextObjectHolder {
    private HistoricalContextInquiryHolder historicalContextInquiryHolder;
    private HistoricalContextModificationHolder historicalContextModificationHolder;
    private HistoricalObjectType historicalObjectType;

    public HistoricalContextObjectHolder(HistoricalContextInquiryHolder historicalContextInquiryHolder) {
        this.historicalContextInquiryHolder = historicalContextInquiryHolder;
        this.historicalContextModificationHolder = null;
        this.historicalObjectType = HistoricalObjectType.INQUIRY;
    }

    public HistoricalContextObjectHolder(HistoricalContextModificationHolder historicalContextModificationHolder) {
        this.historicalContextModificationHolder = historicalContextModificationHolder;
        this.historicalContextInquiryHolder = null;
        this.historicalObjectType = HistoricalObjectType.FILE_MODIFICATION;
    }

    public HistoricalContextObjectHolder() {
        this.historicalContextInquiryHolder = null;
        this.historicalContextModificationHolder = null;
        this.historicalObjectType = null;
    }

    public HistoricalContextObjectHolder(HistoricalObjectDataHolder historicalObjectDataHolder) {
        if (historicalObjectDataHolder.getHistoricalContextObjectType() == HistoricalObjectType.FILE_MODIFICATION) {
            this.historicalContextModificationHolder = new HistoricalContextModificationHolder(historicalObjectDataHolder.getHistoricalModificationDataHolder());
            this.historicalContextInquiryHolder = null;
        } else {
            this.historicalContextInquiryHolder = new HistoricalContextInquiryHolder(historicalObjectDataHolder.getHistoricalContextInquiryDataHolder());
            this.historicalContextModificationHolder = null;
        }
        this.historicalObjectType = historicalObjectDataHolder.getHistoricalContextObjectType();
    }

    public HistoricalContextInquiryHolder getHistoricalContextInquiryHolder() {
        return historicalContextInquiryHolder;
    }

    public void setHistoricalContextInquiryHolder(HistoricalContextInquiryHolder historicalContextInquiryHolder) {
        this.historicalContextInquiryHolder = historicalContextInquiryHolder;
    }

    public HistoricalContextModificationHolder getHistoricalContextModificationHolder() {
        return historicalContextModificationHolder;
    }

    public void setHistoricalContextModificationHolder(HistoricalContextModificationHolder historicalContextModificationHolder) {
        this.historicalContextModificationHolder = historicalContextModificationHolder;
    }

    public HistoricalObjectType getHistoricalContextObjectType() {
        return historicalObjectType;
    }

    public void setHistoricalContextObjectType(HistoricalObjectType historicalObjectType) {
        this.historicalObjectType = historicalObjectType;
    }
}
