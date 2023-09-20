package com.translator.service.codactor.modification;

import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.codactor.modification.ModificationType;

import java.util.List;

public interface CodeRecorderService {
    void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    void getModifiedCode(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    void getFixedCode(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    void getCreatedCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    void getCreatedCodeFile(String filePath, String description, String overrideCode);

    void getTranslatedCode(String filePath, String newLanguage, String newFileType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);
}
