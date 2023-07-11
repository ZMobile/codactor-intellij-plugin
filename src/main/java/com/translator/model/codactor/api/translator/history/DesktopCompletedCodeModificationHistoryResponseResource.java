package com.translator.model.codactor.api.translator.history;

import com.translator.model.codactor.history.data.HistoricalFileModificationDataHolder;

import java.util.List;

public class DesktopCompletedCodeModificationHistoryResponseResource {
    private List<HistoricalFileModificationDataHolder> modificationHistory;

    public DesktopCompletedCodeModificationHistoryResponseResource(List<HistoricalFileModificationDataHolder> modificationHistory) {
        this.modificationHistory = modificationHistory;
    }

    public List<HistoricalFileModificationDataHolder> getModificationHistory() {
        return modificationHistory;
    }
}
