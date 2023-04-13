package com.translator.model.modification;


import com.translator.service.modification.tracking.listener.DocumentListenerService;
import com.translator.service.modification.tracking.listener.UneditableSegmentListenerService;
import com.intellij.ui.components.JBTextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileModificationTracker {
    private Map<String, JBTextArea> displayMap;
    private final Map<String, String> extensionToSyntaxMap;
    private final Map<String, String> languageToSyntaxMap;
    private final DocumentListenerService documentListenerService;
    private final UneditableSegmentListenerService uneditableSegmentListenerService;
    private final String filePath;
    private final List<FileModification> modifications;
    private final List<FileModificationUpdate> fileModificationUpdateQueue;

    public FileModificationTracker(Map<String, JBTextArea> displayMap, Map<String, String> extensionToSyntaxMap, Map<String, String> languageToSyntaxMap, DocumentListenerService documentListenerService, UneditableSegmentListenerService uneditableSegmentListenerService, String filePath, JBTextArea display) {
        this.displayMap = displayMap;
        this.extensionToSyntaxMap = extensionToSyntaxMap;
        this.languageToSyntaxMap = languageToSyntaxMap;
        this.documentListenerService = documentListenerService;
        this.uneditableSegmentListenerService = uneditableSegmentListenerService;
        this.filePath = filePath;
        this.modifications = new ArrayList<>();
        this.fileModificationUpdateQueue = new ArrayList<>();
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


    public String addModification(int startIndex, int endIndex, ModificationType modificationType) {
        JBTextArea display = displayMap.get(filePath);
        String beforeText = display.getText().substring(startIndex, endIndex);
        for (FileModification m : modifications) {
            // Check if the proposed modification would overlap with any existing modifications in this tracker
            if ((startIndex <= m.getStartIndex() && endIndex >= m.getStartIndex()) || (startIndex <= m.getEndIndex() && endIndex >= m.getEndIndex())) {
                return null;
            }
        }
        FileModification fileModification = new FileModification(filePath, startIndex, endIndex, beforeText, modificationType);
        modifications.add(fileModification);
        uneditableSegmentListenerService.addUneditableFileModificationSegmentListener(fileModification.getId());
        return fileModification.getId();
    }



   public void removeModification(String modificationId) {
        FileModification m = modifications.stream()
                .filter(modification -> modification.getId().equals(modificationId))
                .findFirst()
                .orElse(null);
        if (m == null) {
            return;
        }
        uneditableSegmentListenerService.removeUneditableFileModificationSegmentListener(modificationId);
        modifications.remove(m);
    }

    public boolean hasModification(String modificationId) {
        return modifications.stream()
                .anyMatch(m -> m.getId().equals(modificationId));
    }

    public List<FileModification> getModifications() {
        return modifications;
    }


    public void updateModifications(int formerStartIndex, int formerEndIndex, String textInserted) {
        int lengthDifference = textInserted.length() - (formerEndIndex - formerStartIndex);
        for (FileModification m : modifications) {
            if (m.getStartIndex() >= formerStartIndex) {
                m.setStartIndex(m.getStartIndex() + lengthDifference);
                m.setEndIndex(m.getEndIndex() + lengthDifference);
            } else if (formerStartIndex >= m.getStartIndex() && formerEndIndex < m.getEndIndex()) {
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
        JBTextArea display = displayMap.get(filePath);
        synchronized (modifications) {
            for (FileModification m : modifications) {
                if (m.getId().equals(modificationId)) {
                    documentListenerService.removeDocumentListener(m.getFilePath());
                    uneditableSegmentListenerService.removeUneditableFileModificationSegmentListener(modificationId);
                    int formerStartIndex = m.getStartIndex();
                    int formerEndIndex = m.getEndIndex();
                    modifications.remove(m);
                    updateModifications(formerStartIndex, formerEndIndex, modification);
                    display.replaceRange(modification, formerStartIndex, formerEndIndex);
                    documentListenerService.insertDocumentListener(m.getFilePath());
                    break;
                }
            }
        }
    }

    public void readyFileModificationUpdate(String modificationId, List<FileModificationSuggestionRecord> modificationOptions) {
        FileModification fileModification = getModification(modificationId);
        if (fileModification != null) {
            fileModification.setModificationRecordId(modificationOptions.get(0).getModificationId());
            List<FileModificationSuggestion> suggestions = new ArrayList<>();
            for (FileModificationSuggestionRecord modificationOption : modificationOptions) {
                String syntax;
                //if (modificationOption.getLanguage() == null) {
                    syntax = extensionToSyntaxMap.get(getFileExtension(filePath).toLowerCase());
                /*} else {
                    syntax = languageToSyntaxMap.get(modificationOption.getLanguage().toLowerCase());
                }*/
                suggestions.add(new FileModificationSuggestion(modificationOption.getId(), filePath, modificationId, modificationOption.getSuggestedCode(), syntax));
            }
            fileModification.setModificationOptions(suggestions);
            fileModification.setDone(true);
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public FileModification getModification(String modificationId) {
        for (FileModification m : modifications) {
            if (m.getId().equals(modificationId)) {
                return m;
            }
        }
        return null;
    }

    public JBTextArea getDisplay() {
        return displayMap.get(filePath);
    }

    public String getFileExtension(String filePath) {
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            return filePath.substring(i + 1);
        }
        return "";
    }

    public Map<String, JBTextArea> getDisplayMap() {
        return displayMap;
    }

    public void setDisplayMap(Map<String, JBTextArea> displayMap) {
        this.displayMap = displayMap;
    }
}
