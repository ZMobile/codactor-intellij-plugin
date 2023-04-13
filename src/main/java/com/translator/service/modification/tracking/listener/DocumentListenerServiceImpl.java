package com.translator.service.modification.tracking.listener;

import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestion;
import com.translator.model.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.modification.FileModificationTracker;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.intellij.ui.components.JBTextArea;

import javax.inject.Inject;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.util.HashMap;
import java.util.Map;

public class DocumentListenerServiceImpl implements DocumentListenerService {
    private final FileModificationTrackerService fileModificationTrackerService;
    private final Map<String, JBTextArea> displayMap;
    private final Map<String, DocumentListener> documentListenerMap;
    private final Map<String, DocumentListener> modificationSuggestionDocumentListenerMap;

    @Inject
    public DocumentListenerServiceImpl(FileModificationTrackerService fileModificationTrackerService,
                                       Map<String, JBTextArea> displayMap) {
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.displayMap = displayMap;
        this.documentListenerMap = new HashMap<>();
        this.modificationSuggestionDocumentListenerMap = new HashMap<>();
    }

    @Override
    public void insertDocumentListener(String filePath) {
        if (documentListenerMap.containsKey(filePath)) {
            return;
        }
        JBTextArea display = displayMap.get(filePath);
        DocumentListener documentListener = new DocumentListener() {
            private DocumentEvent mostRecentEvent;
            @Override
            public void insertUpdate(DocumentEvent e) {
                boolean undoableEditTriggered = false;
                int startIndex = -1;
                int endIndex = -1;
                String textInserted = null;
                try {
                    if (e != mostRecentEvent) {
                        startIndex = e.getOffset();
                        endIndex = startIndex + e.getLength();
                        textInserted = e.getDocument().getText(startIndex, endIndex - startIndex);
                        FileModificationTracker currentFileModificationTracker = fileModificationTrackerService.getActiveModificationFiles().get(filePath);
                        if (currentFileModificationTracker != null) {
                            for (FileModification fileModification : currentFileModificationTracker.getModifications()) {
                                if ((startIndex > fileModification.getStartIndex() && endIndex <= fileModification.getEndIndex())
                                        || (startIndex <= fileModification.getStartIndex() && endIndex > fileModification.getStartIndex())
                                        || (startIndex <= fileModification.getEndIndex() && endIndex > fileModification.getEndIndex())) {
                                    undoableEditTriggered = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!undoableEditTriggered) {
                        int documentLength = e.getDocument().getLength();
                        if (endIndex > documentLength) {
                            endIndex = documentLength;
                        }
                        fileModificationTrackerService.updateModifications(filePath, startIndex, startIndex, textInserted);
                        mostRecentEvent = e;
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                boolean undoableEditTriggered = false;
                int startIndex = -1;
                int endIndex = -1;
                if (e != mostRecentEvent) {
                    startIndex = e.getOffset();
                    endIndex = startIndex + e.getLength();
                    FileModificationTracker currentFileModificationTracker = fileModificationTrackerService.getActiveModificationFiles().get(filePath);
                    if (currentFileModificationTracker != null) {
                        for (FileModification fileModification : currentFileModificationTracker.getModifications()) {
                            if ((startIndex > fileModification.getStartIndex() && endIndex <= fileModification.getEndIndex())
                                    || (startIndex <= fileModification.getStartIndex() && endIndex > fileModification.getStartIndex())
                                    || (startIndex <= fileModification.getEndIndex() && endIndex > fileModification.getEndIndex())) {
                                //This shoudln't be getting called when the first edit happens
                                undoableEditTriggered = true;
                                break;
                            }
                        }
                    }
                }
                if (!undoableEditTriggered) {
                    int documentLength = e.getDocument().getLength();
                    if (endIndex > documentLength) {
                        endIndex = documentLength;
                    }
                    fileModificationTrackerService.updateModifications(filePath, startIndex, endIndex, "");
                    mostRecentEvent = e;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        };
        display.getDocument().addDocumentListener(documentListener);
        documentListenerMap.put(filePath, documentListener);
    }

    @Override
    public void removeDocumentListener(String filePath) {
        if (!documentListenerMap.containsKey(filePath)) {
            return;
        }
        JBTextArea display = displayMap.get(filePath);
        DocumentListener documentListener = documentListenerMap.get(filePath);
        display.getDocument().removeDocumentListener(documentListener);
        documentListenerMap.remove(filePath);
    }

    @Override
    public void insertModificationSuggestionDocumentListener(String suggestionId) {
        if (modificationSuggestionDocumentListenerMap.containsKey(suggestionId)) {
            return;
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        //JBTextArea display = fileModificationSuggestion.getDisplay();
        DocumentListener documentListener = new DocumentListener() {
            private DocumentEvent mostRecentEvent;
            @Override
            public void insertUpdate(DocumentEvent e) {
                boolean undoableEditTriggered = false;
                int startIndex = -1;
                int endIndex = -1;
                String textInserted = null;
                try {
                    if (e != mostRecentEvent) {
                        startIndex = e.getOffset();
                        endIndex = startIndex + e.getLength();
                        textInserted = e.getDocument().getText(startIndex, endIndex - startIndex);
                        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = fileModificationTrackerService.getActiveModificationSuggestionModifications().get(suggestionId);
                        if (fileModificationSuggestionModificationTracker != null) {
                            for (FileModification fileModification : fileModificationSuggestionModificationTracker.getModifications()) {
                                if ((startIndex > fileModification.getStartIndex() && endIndex <= fileModification.getEndIndex())
                                        || (startIndex <= fileModification.getStartIndex() && endIndex > fileModification.getStartIndex())
                                        || (startIndex <= fileModification.getEndIndex() && endIndex > fileModification.getEndIndex())) {
                                    undoableEditTriggered = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!undoableEditTriggered) {
                        int documentLength = e.getDocument().getLength();
                        if (endIndex > documentLength) {
                            endIndex = documentLength;
                        }
                        fileModificationTrackerService.updateModificationSuggestionModifications(suggestionId, startIndex, endIndex, "");
                        mostRecentEvent = e;
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                boolean undoableEditTriggered = false;
                int startIndex = -1;
                int endIndex = -1;
                if (e != mostRecentEvent) {
                    startIndex = e.getOffset();
                    endIndex = startIndex + e.getLength();
                    FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = fileModificationTrackerService.getActiveModificationSuggestionModifications().get(suggestionId);
                    if (fileModificationSuggestionModificationTracker != null) {
                        for (FileModification fileModification : fileModificationSuggestionModificationTracker.getModifications()) {
                            if ((startIndex > fileModification.getStartIndex() && endIndex <= fileModification.getEndIndex())
                                    || (startIndex <= fileModification.getStartIndex() && endIndex > fileModification.getStartIndex())
                                    || (startIndex <= fileModification.getEndIndex() && endIndex > fileModification.getEndIndex())) {
                                undoableEditTriggered = true;
                                break;
                            }
                        }
                    }
                }
                if (!undoableEditTriggered) {
                    int documentLength = e.getDocument().getLength();
                    if (endIndex > documentLength) {
                        endIndex = documentLength;
                    }
                    fileModificationTrackerService.updateModifications(suggestionId, startIndex, endIndex, "");
                    mostRecentEvent = e;
               }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        };
        //display.getDocument().addDocumentListener(documentListener);
        modificationSuggestionDocumentListenerMap.put(suggestionId, documentListener);
    }

    @Override
    public void removeModificationSuggestionDocumentListener(String suggestionId) {
        if (!modificationSuggestionDocumentListenerMap.containsKey(suggestionId)) {
            return;
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        //JBTextArea display = fileModificationSuggestion.getDisplay();
        DocumentListener documentListener = modificationSuggestionDocumentListenerMap.get(suggestionId);
        //display.getDocument().removeDocumentListener(documentListener);
        modificationSuggestionDocumentListenerMap.remove(suggestionId);
    }
}
