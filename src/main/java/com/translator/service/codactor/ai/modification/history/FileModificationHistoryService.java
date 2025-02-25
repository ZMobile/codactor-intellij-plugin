package com.translator.service.codactor.ai.modification.history;

import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;

import java.util.List;

public interface FileModificationHistoryService {
    List<FileModificationDataHolder> getRecentHistoricalFileModifications();

    FileModificationDataHolder getModification(String id);
}
