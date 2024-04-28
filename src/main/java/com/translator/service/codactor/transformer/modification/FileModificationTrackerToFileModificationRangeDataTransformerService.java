package com.translator.service.codactor.transformer.modification;

import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.model.codactor.ai.modification.data.FileModificationRangeData;

import java.util.List;

public interface FileModificationTrackerToFileModificationRangeDataTransformerService {
    List<FileModificationRangeData> convert(FileModificationTracker fileModificationTracker);
}
