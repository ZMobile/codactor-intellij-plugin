package com.translator.service.codactor.modification.multi;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;

import java.util.List;

public interface MultiFileModificationService {
    void modifyCodeFiles(List<String> filePaths, String modification, List<HistoricalContextObjectHolder> priorContext) throws InterruptedException;

    void fixCodeFiles(List<String> filePaths, String error, List<HistoricalContextObjectHolder> priorContextData) throws InterruptedException;
}
