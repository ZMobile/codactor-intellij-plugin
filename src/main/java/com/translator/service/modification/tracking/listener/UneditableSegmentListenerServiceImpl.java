package com.translator.service.modification.tracking.listener;

import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestionModification;
import com.translator.model.modification.FileModificationSuggestionModificationTracker;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.view.listener.UneditableSegmentListener;
import com.intellij.ui.components.JBTextArea;

import javax.inject.Inject;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import java.util.HashMap;
import java.util.Map;

public class UneditableSegmentListenerServiceImpl implements UneditableSegmentListenerService {
    private FileModificationTrackerService fileModificationTrackerService;
    private Map<String, JBTextArea> displayMap;
    private Map<String, UndoableEditListener> uneditableFileModificationSegmentListenerMap;
    private Map<String, UndoableEditListener> uneditableFileModificationSuggestionSegmentListenerMap;

    @Inject
    public UneditableSegmentListenerServiceImpl(FileModificationTrackerService fileModificationTrackerService,
                                                Map<String, JBTextArea> displayMap) {
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.displayMap = displayMap;
        this.uneditableFileModificationSegmentListenerMap = new HashMap<>();
        this.uneditableFileModificationSuggestionSegmentListenerMap = new HashMap<>();
    }

    @Override
    public void addUneditableFileModificationSegmentListener(String modificationId) {
        if (uneditableFileModificationSegmentListenerMap.containsKey(modificationId)) {
            return;
        }
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        JBTextArea display = displayMap.get(fileModification.getFilePath());
        Document document = display.getDocument();
        UndoableEditListener undoableEditListener = new UneditableSegmentListener(modificationId, fileModificationTrackerService, display, false);
        document.addUndoableEditListener(undoableEditListener);
        uneditableFileModificationSegmentListenerMap.put(modificationId, undoableEditListener);
    }

    @Override
    public void removeUneditableFileModificationSegmentListener(String modificationId) {
        /*FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        JBTextArea display = displayMap.get(fileModification.getFilePath());
        Document document = display.getDocument();
        UndoableEditListener undoableEditListener = uneditableFileModificationSegmentListenerMap.get(modificationId);
        document.removeUndoableEditListener(undoableEditListener);
        uneditableFileModificationSegmentListenerMap.remove(modificationId);*/
    }

    @Override
    public void addUneditableFileModificationSuggestionModificationSegmentListener(String modificationSuggestionModificationId) {
        /*if (uneditableFileModificationSuggestionSegmentListenerMap.containsKey(modificationSuggestionModificationId)) {
            return;
        }
        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationTrackerService.getModificationSuggestionModification(modificationSuggestionModificationId);
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = fileModificationTrackerService.getModificationSuggestionModificationTracker(fileModificationSuggestionModification.getSuggestionId());
        //JBTextArea display = fileModificationSuggestionModificationTracker.getDisplay();
        //Document document = display.getDocument();
        UndoableEditListener undoableEditListener = new UneditableSegmentListener(modificationSuggestionModificationId, fileModificationTrackerService, display, true);
        document.addUndoableEditListener(undoableEditListener);
        uneditableFileModificationSuggestionSegmentListenerMap.put(modificationSuggestionModificationId, undoableEditListener);*/
    }

    @Override
    public void removeUneditableFileModificationSuggestionModificationSegmentListener(String modificationSuggestionModificationId) {
        /*FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationTrackerService.getModificationSuggestionModification(modificationSuggestionModificationId);
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = fileModificationTrackerService.getModificationSuggestionModificationTracker(fileModificationSuggestionModification.getSuggestionId());
        //JBTextArea display = fileModificationSuggestionModificationTracker.getDisplay();
        //Document document = display.getDocument();
        UndoableEditListener undoableEditListener = uneditableFileModificationSuggestionSegmentListenerMap.get(modificationSuggestionModificationId);
        document.removeUndoableEditListener(undoableEditListener);
        uneditableFileModificationSuggestionSegmentListenerMap.remove(modificationSuggestionModificationId);*/
    }
}
