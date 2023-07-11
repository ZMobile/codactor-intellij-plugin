package com.translator.service.codactor.modification.history;

import com.translator.model.codactor.modification.data.FileModificationDataHolder;

import java.util.List;

public interface FileModificationHistoryService {
    List<FileModificationDataHolder> getRecentHistoricalFileModifications();

    FileModificationDataHolder getModification(String id);
}
