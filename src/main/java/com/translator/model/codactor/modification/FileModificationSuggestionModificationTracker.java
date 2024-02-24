package com.translator.model.codactor.modification;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.translator.service.codactor.editor.RangeReplaceService;

import java.util.ArrayList;
import java.util.List;

public class FileModificationSuggestionModificationTracker {
    private final FileModificationSuggestion fileModificationSuggestion;
    private final List<FileModificationSuggestionModification> modifications;
    private final List<FileModificationUpdate> fileModificationUpdateQueue;
    private final RangeReplaceService rangeReplaceService;

    public FileModificationSuggestionModificationTracker(FileModificationSuggestion fileModificationSuggestion,
                                                         RangeReplaceService rangeReplaceService) {
        this.fileModificationSuggestion = fileModificationSuggestion;
        this.modifications = new ArrayList<>();
        this.fileModificationUpdateQueue = new ArrayList<>();
        this.rangeReplaceService = rangeReplaceService;
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

    public void processModificationUpdates() {
        for (FileModificationUpdate update : fileModificationUpdateQueue) {
            implementModification(update.getModificationId(), update.getModification());
        }
        fileModificationUpdateQueue.clear();
    }


    public String addModificationSuggestionModification(Editor editor, String filePath, int startIndex, int endIndex, ModificationType modificationType) {
        for (FileModificationSuggestionModification m : modifications) {
            // Check if the proposed modification would overlap with any existing modifications in this tracker
            if (m.getEditor().equals(editor) && (startIndex <= m.getRangeMarker().getStartOffset() && endIndex >= m.getRangeMarker().getStartOffset()) || (startIndex <= m.getRangeMarker().getEndOffset() && endIndex >= m.getRangeMarker().getEndOffset())) {
                return "Error: Can't modify code that is already being modified. Your modification boundaries clash with modification id: " + m.getId() + " with code snippet: \n" + m.getBeforeText();
            }
        }
        Document document = fileModificationSuggestion.getSuggestedCodeEditor().getDocument();
        RangeMarker rangeMarker = document.createRangeMarker(startIndex, endIndex);
        FileModificationSuggestionModification fileModificationSuggestionModification = new FileModificationSuggestionModification(editor, filePath, fileModificationSuggestion.getModificationId(), fileModificationSuggestion.getId(), rangeMarker, fileModificationSuggestion.getBeforeCode(), modificationType);
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


    public void implementModification(String modificationId, String modification) {
        synchronized (modifications) {
            for (FileModificationSuggestionModification m : modifications) {
                if (m.getId().equals(modificationId)) {
                    RangeMarker rangeMarker = m.getRangeMarker();
                    if (rangeMarker != null && rangeMarker.isValid()) {
                        int formerStartIndex = m.getRangeMarker().getStartOffset();
                        int formerEndIndex = m.getRangeMarker().getEndOffset();
                        modifications.remove(m);
                        rangeReplaceService.replaceRange(m.getEditor(), formerStartIndex, formerEndIndex, modification);
                        break;
                    }
                }
            }
        }
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
