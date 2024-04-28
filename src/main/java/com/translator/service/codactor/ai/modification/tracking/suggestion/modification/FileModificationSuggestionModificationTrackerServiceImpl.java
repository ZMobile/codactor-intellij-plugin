package com.translator.service.codactor.ai.modification.tracking.suggestion.modification;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.codactor.ai.modification.ModificationType;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileModificationSuggestionModificationTrackerServiceImpl implements FileModificationSuggestionModificationTrackerService {
    private Map<String, FileModificationSuggestionModificationTracker> activeModificationSuggestionModifications;
    private final FileModificationSuggestionModificationService fileModificationSuggestionModificationService;

    @Inject
    public FileModificationSuggestionModificationTrackerServiceImpl(FileModificationSuggestionModificationService fileModificationSuggestionModificationService) {
        this.activeModificationSuggestionModifications = new HashMap<>();
        this.fileModificationSuggestionModificationService = fileModificationSuggestionModificationService;
    }

    @Override
    public String addModificationSuggestionModification(FileModificationSuggestion fileModificationSuggestion, Editor editor, int startIndex, int endIndex, ModificationType modificationType) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker;
        if (activeModificationSuggestionModifications.containsKey(fileModificationSuggestion.getId())) {
            fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.get(fileModificationSuggestion.getId());
        } else {
            fileModificationSuggestionModificationTracker = new FileModificationSuggestionModificationTracker(fileModificationSuggestion);
            activeModificationSuggestionModifications.put(fileModificationSuggestion.getId(), fileModificationSuggestionModificationTracker);
        }
        for (FileModificationSuggestionModification m : fileModificationSuggestionModificationTracker.getModifications()) {
            // Check if the proposed modification would overlap with any existing modifications in this tracker
            if (m.getEditor().equals(editor) && (startIndex <= m.getRangeMarker().getStartOffset() && endIndex >= m.getRangeMarker().getStartOffset()) || (startIndex <= m.getRangeMarker().getEndOffset() && endIndex >= m.getRangeMarker().getEndOffset())) {
                return "Error: Can't modify code that is already being modified. Your modification boundaries clash with modification id: " + m.getId() + " with code snippet: \n" + m.getBeforeText();
            }
        }
        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationSuggestionModificationService.addModificationSuggestionModification(fileModificationSuggestion, editor, startIndex, endIndex, modificationType);
        fileModificationSuggestionModificationTracker.getModifications().add(fileModificationSuggestionModification);
        return fileModificationSuggestionModification.getId();
    }

    @Override
    public void removeModificationSuggestionModification(String fileModificationSuggestionModificationId) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.values().stream()
                .filter(m -> m.hasModificationSuggestionModification(fileModificationSuggestionModificationId))
                .findFirst()
                .orElseThrow();
        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationSuggestionModificationTracker.getModificationSuggestionModification(fileModificationSuggestionModificationId);
        fileModificationSuggestionModificationTracker.removeModificationSuggestionModification(fileModificationSuggestionModificationId);
        if (fileModificationSuggestionModificationTracker.getModifications().isEmpty()) {
            activeModificationSuggestionModifications.values().remove(fileModificationSuggestionModificationTracker);
        }
        fileModificationSuggestionModificationService.removeModificationSuggestionModification(fileModificationSuggestionModification);
    }

    @Override
    public FileModificationSuggestionModificationTracker getModificationSuggestionModificationTracker(String suggestionId) {
        return activeModificationSuggestionModifications.get(suggestionId);
    }

    @Override
    public Map<String, FileModificationSuggestionModificationTracker> getActiveModificationSuggestionModifications() {
        return activeModificationSuggestionModifications;
    }

    public void implementModification(String modificationSuggestionModificationId, String modification) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = getTrackerWithModificationSuggestionModification(modificationSuggestionModificationId);
        if (fileModificationSuggestionModificationTracker == null) {
            return;
        }
        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationSuggestionModificationTracker.getModificationSuggestionModification(modificationSuggestionModificationId);
        fileModificationSuggestionModificationService.implementModification(fileModificationSuggestionModificationTracker.getFileModificationSuggestion(), fileModificationSuggestionModification, modification);
        removeModificationSuggestionModification(modificationSuggestionModificationId);
    }

    public FileModificationSuggestionModificationTracker getTrackerWithModificationSuggestionModification(String modificationSuggestionModificationId) {
        return getActiveModificationSuggestionModifications().values().stream()
                .filter(m -> m.hasModificationSuggestionModification(modificationSuggestionModificationId))
                .findFirst()
                .orElse(null);
    }

    public FileModificationSuggestionModificationTracker getWithModificationSuggestionModificationId(String modificationSuggestionModificationId) {
        return activeModificationSuggestionModifications.values().stream()
                .filter(m -> m.hasModificationSuggestionModification(modificationSuggestionModificationId))
                .findFirst()
                .orElse(null);
    }

    public boolean hasModificationSuggestionModification(String suggestionId, String modificationSuggestionModificationId) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = getActiveModificationSuggestionModifications().get(suggestionId);
        return fileModificationSuggestionModificationTracker.getModifications().stream()
                .anyMatch(m -> m.getId().equals(modificationSuggestionModificationId));
    }
}
