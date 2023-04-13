package com.translator.model.modification;

import com.translator.service.modification.tracking.listener.DocumentListenerService;
import com.translator.service.modification.tracking.listener.UneditableSegmentListenerService;
import com.intellij.ui.components.JBTextArea;

import java.util.ArrayList;
import java.util.List;

public class FileModificationSuggestionModificationTracker {
    private final DocumentListenerService documentListenerService;
    private final UneditableSegmentListenerService uneditableSegmentListenerService;
    private final String modificationId;
    private final String suggestionId;
    private final List<FileModificationSuggestionModification> modifications;
    private final List<FileModificationUpdate> fileModificationUpdateQueue;

    public FileModificationSuggestionModificationTracker(DocumentListenerService documentListenerService,
                                                         UneditableSegmentListenerService uneditableSegmentListenerService,
                                                         String modificationId,
                                                         String suggestionId) {
        this.documentListenerService = documentListenerService;
        this.uneditableSegmentListenerService = uneditableSegmentListenerService;
        this.modificationId = modificationId;
        this.suggestionId = suggestionId;
        this.modifications = new ArrayList<>();
        this.fileModificationUpdateQueue = new ArrayList<>();
    }

    public String getModificationId() {
        return modificationId;
    }

    public String getSuggestionId() {
        return suggestionId;
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


    public String addModificationSuggestionModification(String filePath, int startIndex, int endIndex, ModificationType modificationType) {
        //String beforeText = display.getText().substring(startIndex, endIndex);
        for (FileModificationSuggestionModification m : modifications) {
            // Check if the proposed modification would overlap with any existing modifications in this tracker
            if ((startIndex <= m.getStartIndex() && endIndex >= m.getStartIndex()) || (startIndex <= m.getEndIndex() && endIndex >= m.getEndIndex())) {
                return null;
            }
        }
        FileModificationSuggestionModification fileModificationSuggestionModification = new FileModificationSuggestionModification(filePath, modificationId, suggestionId, startIndex, endIndex, "Testo", modificationType);
        modifications.add(fileModificationSuggestionModification);
        uneditableSegmentListenerService.addUneditableFileModificationSuggestionModificationSegmentListener(fileModificationSuggestionModification.getId());
        return fileModificationSuggestionModification.getId();
    }



    public void removeModificationSuggestionModification(String modificationSuggestionModificationId) {
        uneditableSegmentListenerService.removeUneditableFileModificationSuggestionModificationSegmentListener(modificationSuggestionModificationId);
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


    public void updateModifications(int formerStartIndex, int formerEndIndex, String textInserted) {
        int lengthDifference = textInserted.length() - (formerEndIndex - formerStartIndex);
        for (FileModificationSuggestionModification m : modifications) {
            if (m.getStartIndex() >= formerStartIndex) {
                m.setStartIndex(m.getStartIndex() + lengthDifference);
                m.setEndIndex(m.getEndIndex() + lengthDifference);
            }
        }
    }

    /*public void undoUpdateModifications() {
        if (undoStack.empty()) {
            return;
        }
        // Pop the most recent undo action from the stack
        UndoAction undoAction = undoStack.pop();
        // Get the former contents of the file
        String formerContents = undoAction.getFormerContents();
        // Get the former start index of the modification
        int formerStartIndex = undoAction.getFormerStartIndex();

        // Get the text inserted into the file
        String textInserted = undoAction.getTextInserted();
        // Update the file contents
        fileContents = formerContents;

        // Update the modifications
        int lengthDifference = textInserted.length() - (undoAction.getFormerEndIndex() - undoAction.getFormerStartIndex());
        for (FileModification m : modifications) {
            if (m.getStartIndex() >= formerStartIndex) {
                m.setStartIndex(m.getStartIndex() - lengthDifference);
                m.setEndIndex(m.getEndIndex() - lengthDifference);
            }
        }
    }*/

    public void implementModification(String modificationId, String modification) {
        synchronized (modifications) {
            for (FileModificationSuggestionModification m : modifications) {
                if (m.getId().equals(modificationId)) {
                    documentListenerService.removeModificationSuggestionDocumentListener(m.getSuggestionId());
                    uneditableSegmentListenerService.removeUneditableFileModificationSuggestionModificationSegmentListener(m.getId());
                    int formerStartIndex = m.getStartIndex();
                    int formerEndIndex = m.getEndIndex();
                    //int lengthDifference = modification.length() - (formerEndIndex - formerStartIndex + 1);
                    modifications.remove(m);
                    updateModifications(formerStartIndex, formerEndIndex, modification);
                    //display.replaceRange(modification, formerStartIndex, formerEndIndex);
                    documentListenerService.insertModificationSuggestionDocumentListener(m.getSuggestionId());
                    m.setEditedCode(modification);
                    break;
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
