package com.translator.model.codactor.ai.modification;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.translator.service.codactor.ide.editor.RangeReplaceService;

import java.util.ArrayList;
import java.util.List;

public class FileModificationSuggestionModificationTracker {
    private final FileModificationSuggestion fileModificationSuggestion;
    private final List<FileModificationSuggestionModification> modifications;
    private final List<FileModificationUpdate> fileModificationUpdateQueue;

    public FileModificationSuggestionModificationTracker(FileModificationSuggestion fileModificationSuggestion,
                                                         RangeReplaceService rangeReplaceService) {
        this.fileModificationSuggestion = fileModificationSuggestion;
        this.modifications = new ArrayList<>();
        this.fileModificationUpdateQueue = new ArrayList<>();
    }

    public FileModificationSuggestionModificationTracker(FileModificationSuggestion fileModificationSuggestion) {
        this.fileModificationSuggestion = fileModificationSuggestion;
        this.modifications = new ArrayList<>();
        this.fileModificationUpdateQueue = new ArrayList<>();
    }

    public FileModificationSuggestion getFileModificationSuggestion() {
        return fileModificationSuggestion;
    }

    public void addModificationUpdate(FileModificationUpdate update) {
        fileModificationUpdateQueue.add(update);
    }

    public List<FileModificationUpdate> getFileModificationUpdateQueue() {
        return fileModificationUpdateQueue;
    }

    public String addModificationSuggestionModification(FileModificationSuggestionModification fileModificationSuggestionModification) {
         modifications.add(fileModificationSuggestionModification);
        return fileModificationSuggestionModification.getId();
    }

    public void removeModificationSuggestionModification(String modificationSuggestionModificationId) {
        modifications.stream()
                .filter(m -> m.getId().equals(modificationSuggestionModificationId))
                .findFirst()
                .ifPresent(modifications::remove);
    }

    public boolean hasModificationSuggestionModification(String modificationSuggestionModificationId) {
        return modifications.stream()
                .anyMatch(m -> m.getId().equals(modificationSuggestionModificationId));
    }

    public List<FileModificationSuggestionModification> getModifications() {
        return modifications;
    }

    public FileModificationSuggestionModification getModificationSuggestionModification(String modificationId) {
        for (FileModificationSuggestionModification m : modifications) {
            if (m.getId().equals(modificationId)) {
                return m;
            }
        }
        return null;
    }
}
