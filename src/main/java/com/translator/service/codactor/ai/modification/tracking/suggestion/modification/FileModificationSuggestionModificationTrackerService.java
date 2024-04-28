package com.translator.service.codactor.ai.modification.tracking.suggestion.modification;

import com.intellij.openapi.editor.Editor;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.codactor.ai.modification.ModificationType;

import java.util.Map;

public interface FileModificationSuggestionModificationTrackerService {
    String addModificationSuggestionModification(FileModificationSuggestion fileModificationSuggestion, Editor editor, int startIndex, int endIndex, ModificationType modificationType);

    void removeModificationSuggestionModification(String fileModificationSuggestionModificationId);

    void implementModification(String modificationSuggestionModificationId, String modification);

    FileModificationSuggestionModificationTracker getModificationSuggestionModificationTracker(String suggestionId);

    Map<String, FileModificationSuggestionModificationTracker> getActiveModificationSuggestionModifications();
}
