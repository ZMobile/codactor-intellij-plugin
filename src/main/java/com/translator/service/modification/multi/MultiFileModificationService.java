package com.translator.service.modification.multi;

import com.translator.model.history.data.HistoricalContextObjectDataHolder;

import java.util.List;

public interface MultiFileModificationService {
    void modifyCodeFiles(List<String> filePaths, String modification, List<HistoricalContextObjectDataHolder> priorContext);
}
