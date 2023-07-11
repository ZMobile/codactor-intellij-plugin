package com.translator.dao.history;

import com.translator.model.codactor.api.translator.history.DesktopCompletedCodeModificationHistoryResponseResource;

public interface CodeModificationHistoryDao {
    DesktopCompletedCodeModificationHistoryResponseResource getRecentModifications();
}
