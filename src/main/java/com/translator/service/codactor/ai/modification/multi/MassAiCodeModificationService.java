package com.translator.service.codactor.ai.modification.multi;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;

import java.util.List;

public interface MassAiCodeModificationService {
    void getModifiedCode(List<String> filePaths, String modification, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCode(List<String> filePaths, String error, List<HistoricalContextObjectHolder> priorContext);

    void getTranslatedCode(List<String> filePaths, String newLanguage, String newFileType, List<HistoricalContextObjectHolder> priorContext);
}
