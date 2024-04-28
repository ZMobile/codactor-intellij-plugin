package com.translator.service.codactor.ai.modification.tracking.suggestion.modification;

import com.intellij.openapi.editor.Editor;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.ModificationType;

public interface FileModificationSuggestionModificationService {
    FileModificationSuggestionModification addModificationSuggestionModification(FileModificationSuggestion fileModificationSuggestion, Editor editor, int startIndex, int endIndex, ModificationType modificationType);

    void implementModification(FileModificationSuggestion fileModificationSuggestion, FileModificationSuggestionModification fileModificationSuggestionModification, String modification);

    void removeModificationSuggestionModification(FileModificationSuggestionModification fileModificationSuggestionModification);
}
