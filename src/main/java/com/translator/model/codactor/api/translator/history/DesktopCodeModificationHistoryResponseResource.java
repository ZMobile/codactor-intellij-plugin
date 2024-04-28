package com.translator.model.codactor.api.translator.history;

import com.translator.model.codactor.ai.history.data.HistoricalFileModificationDataHolder;

import java.util.List;

public class DesktopCodeModificationHistoryResponseResource {
    private List<HistoricalFileModificationDataHolder> modificationHistory;

    public DesktopCodeModificationHistoryResponseResource(List<HistoricalFileModificationDataHolder> modificationHistory) {
        this.modificationHistory = modificationHistory;
    }

    public List<HistoricalFileModificationDataHolder> getModificationHistory() {
        return modificationHistory;
    }
}
