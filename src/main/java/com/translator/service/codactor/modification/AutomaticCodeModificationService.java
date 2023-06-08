package com.translator.service.codactor.modification;

import com.translator.model.codactor.modification.ModificationType;

public interface AutomaticCodeModificationService {
    void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType);

    void getModifiedCode(String filePath, String modification, ModificationType modificationType);

    void getModifiedCodeModification(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType);

    void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType);

    void getFixedCode(String filePath, String error, ModificationType modificationType);

    void getModifiedCodeFix(String suggestionId, String code, int startIndex, int endIndex, String error, ModificationType modificationType);

    void getCreatedCode(String filePath, String description);

    void createAndImplementCode(String filePath, String description);

    void getModifiedCodeCreation(String suggestionId, int startIndex, int endIndex, String description);

    void getTranslatedCode(String filePath, String newLanguage, String newFileType);
}
