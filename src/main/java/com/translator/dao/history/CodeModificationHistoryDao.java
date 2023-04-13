package com.translator.dao.history;

import com.translator.model.api.translator.history.DesktopCodeModificationHistoryResponseResource;

public interface CodeModificationHistoryDao {
    DesktopCodeModificationHistoryResponseResource getRecentModifications();
}
