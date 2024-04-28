package com.translator.service.codactor.ai.modification.multi;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;

import java.util.List;

public interface MultiFileAiModificationService {
    void modifyCodeFiles(List<String> filePaths, String modification, List<HistoricalContextObjectHolder> priorContext) throws InterruptedException;

    void fixCodeFiles(List<String> filePaths, String error, List<HistoricalContextObjectHolder> priorContextData) throws InterruptedException;
}
