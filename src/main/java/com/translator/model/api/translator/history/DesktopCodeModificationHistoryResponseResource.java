package com.translator.model.api.translator.history;

import com.translator.model.history.data.HistoricalContextModificationDataHolder;

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
