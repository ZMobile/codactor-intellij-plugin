package com.translator.service.modification;

import com.translator.model.api.translator.modification.*;
import com.translator.model.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.modification.ModificationType;

public interface AutomaticCodeModificationService {
    void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType);

    void getModifiedCodeModification(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource);

    void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType);

    void getModifiedCodeFix(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource);

    void getCreatedCode(String filePath, int startIndex, int endIndex, String description);

    void getModifiedCodeCreation(DesktopCodeCreationRequestResource desktopCodeCreationRequestResource);

    void getTranslatedCode(String filePath, String newLanguage, String newFileType);
}
