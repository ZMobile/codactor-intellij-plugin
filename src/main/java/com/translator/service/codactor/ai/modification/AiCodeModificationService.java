package com.translator.service.codactor.ai.modification;

import com.intellij.openapi.editor.Editor;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.ModificationType;

import java.util.List;

public interface AiCodeModificationService {
    void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeAndWait(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCode(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeAndWait(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeModification(Editor editor, FileModificationSuggestion fileModificationSuggestion, String code, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCodeAndWait(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCode(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getFixedCodeAndWait(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeFix(Editor editor, FileModificationSuggestion fileModificationSuggestion, String code, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void getCreatedCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext);

    void getCreatedCodeAndWait(String filePath, String description, List<HistoricalContextObjectHolder> priorContext);

    void getCreatedCodeFile(String filePath, String description);

    void getCreatedCodeFileAndWait(String filePath, String description);

    void getDeletedCodeFile(String filePath);

    void createAndImplementCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext);

    void getModifiedCodeCreation(Editor editor, FileModificationSuggestion fileModificationSuggestion, int startIndex, int endIndex, String description, List<HistoricalContextObjectHolder> priorContext);

    void getTranslatedCode(String filePath, String newLanguage, String newFileType, List<HistoricalContextObjectHolder> priorContext);
}
