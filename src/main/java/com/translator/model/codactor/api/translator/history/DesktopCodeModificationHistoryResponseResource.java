package com.translator.model.codactor.api.translator.history;

import com.translator.model.codactor.history.data.HistoricalContextModificationDataHolder;

import java.util.List;

public class DesktopCodeModificationHistoryResponseResource {
    private List<HistoricalContextModificationDataHolder> modificationHistory;

    public DesktopCodeModificationHistoryResponseResource(List<HistoricalContextModificationDataHolder> modificationHistory) {
        this.modificationHistory = modificationHistory;
    }

    public List<HistoricalContextModificationDataHolder> getModificationHistory() {
        return modificationHistory;
    }
}
