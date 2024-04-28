package com.translator.dao.history;

import com.translator.model.codactor.api.translator.history.DesktopCodeModificationHistoryResponseResource;
import com.translator.model.codactor.ai.history.data.HistoricalFileModificationDataHolder;

public interface CodeModificationHistoryDao {
    DesktopCodeModificationHistoryResponseResource getRecentModifications();

    HistoricalFileModificationDataHolder getModification(String id);
}
