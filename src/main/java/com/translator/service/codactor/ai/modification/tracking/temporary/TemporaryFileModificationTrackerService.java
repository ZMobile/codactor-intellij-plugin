package com.translator.service.codactor.ai.modification.tracking.temporary;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.ModificationType;

import java.util.List;

public interface TemporaryFileModificationTrackerService {
    String addTemporaryFileModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void removeTemporaryFileModification(String modificationId);
}
