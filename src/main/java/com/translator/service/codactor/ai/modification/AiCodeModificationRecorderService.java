package com.translator.service.codactor.ai.modification;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.ModificationType;

import java.util.List;

public interface AiCodeModificationRecorderService {
    String getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    String getModifiedCode(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    String getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    String getFixedCode(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    String getCreatedCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext, String overrideCode);

    String getCreatedCodeFile(String filePath, String description, String overrideCode);

    String getTranslatedCode(String filePath, String newLanguage, String newFileType, List<HistoricalContextObjectHolder> priorContext, String overrideCode);
}
