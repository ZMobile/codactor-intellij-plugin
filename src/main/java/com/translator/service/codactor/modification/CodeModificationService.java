package com.translator.service.codactor.modification;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.ModificationType;

import java.util.List;

public interface CodeModificationService {
    void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeAndWait(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCode(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeAndWait(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeModification(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCodeAndWait(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCode(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCodeAndWait(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeFix(String suggestionId, String code, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getCreatedCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext);

    void getCreatedCodeAndWait(String filePath, String description, List<HistoricalContextObjectHolder> priorContext);

    void getCreatedCodeFile(String filePath, String description);

    void getCreatedCodeFileAndWait(String filePath, String description);

    void getDeletedCodeFile(String filePath);

    void createAndImplementCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeCreation(String suggestionId, int startIndex, int endIndex, String description, List<HistoricalContextObjectHolder> priorContext);

    void getTranslatedCode(String filePath, String newLanguage, String newFileType, List<HistoricalContextObjectHolder> priorContext);
}
