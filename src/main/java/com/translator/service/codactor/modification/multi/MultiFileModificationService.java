package com.translator.service.codactor.modification.multi;

import com.translator.model.codactor.history.data.HistoricalContextObjectDataHolder;

import java.util.List;

public interface MultiFileModificationService {
    void modifyCodeFiles(List<String> filePaths, String modification, List<HistoricalContextObjectDataHolder> priorContext) throws InterruptedException;

    void fixCodeFiles(List<String> filePaths, String error, List<HistoricalContextObjectDataHolder> priorContextData) throws InterruptedException;
}
