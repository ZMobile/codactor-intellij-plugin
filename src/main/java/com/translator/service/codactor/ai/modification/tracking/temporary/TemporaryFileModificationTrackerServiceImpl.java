package com.translator.service.codactor.ai.modification.tracking.temporary;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.ModificationType;

import java.util.List;

public class TemporaryFileModificationTrackerServiceImpl implements TemporaryFileModificationTrackerService {
    @Override
    public String addTemporaryFileModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        return null;
    }

    @Override
    public void removeTemporaryFileModification(String modificationId) {
    }
}
