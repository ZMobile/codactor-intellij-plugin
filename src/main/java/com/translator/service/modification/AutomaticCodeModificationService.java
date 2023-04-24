package com.translator.service.modification;

import com.translator.model.api.translator.modification.*;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.modification.ModificationType;

import java.util.List;

public interface AutomaticCodeModificationService {
    void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType);

    void getModifiedCodeModification(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType);

    void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType);

    void getModifiedCodeFix(String suggestionId, String code, int startIndex, int endIndex, String error, ModificationType modificationType);

    void getCreatedCode(String filePath, String description);

    void createAndImplementCode(String filePath, String description);

    void getModifiedCodeCreation(String suggestionId, int startIndex, int endIndex, String description);

    void getTranslatedCode(String filePath, String newLanguage, String newFileType);
}
