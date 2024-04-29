package com.translator.view.codactor.listener;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;

public class UneditableSegmentListener implements UndoableEditListener {
    private String fileModificationId;
    private int previousStartIndex;
    private int previousEndIndex;
    private FileModificationTrackerService fileModificationTrackerService;
    private FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService;
    private boolean modificationSuggestionModification;

    public UneditableSegmentListener(String fileModificationId, FileModificationTrackerService fileModificationTrackerService, FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService, boolean modificationSuggestionModification) {
        this.fileModificationId = fileModificationId;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationSuggestionModificationTrackerService = fileModificationSuggestionModificationTrackerService;
        this.modificationSuggestionModification = modificationSuggestionModification;
        if (modificationSuggestionModification) {
            FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationSuggestionModificationTrackerService.getModificationSuggestionModification(fileModificationId);
            previousStartIndex = fileModificationSuggestionModification.getRangeMarker().getStartOffset();
            previousEndIndex = fileModificationSuggestionModification.getRangeMarker().getEndOffset();
        } else {
            FileModification fileModification = fileModificationTrackerService.getModification(fileModificationId);
            previousStartIndex = fileModification.getRangeMarker().getStartOffset();
            previousEndIndex = fileModification.getRangeMarker().getEndOffset();
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) e.getEdit();
        int offset = event.getOffset();
        int length = event.getLength();
        boolean textAdded = event.getType().equals(DocumentEvent.EventType.INSERT);
        //Check if the offset and length of the edit is within the uneditable segment
        int startIndex;
        int endIndex;
        if (modificationSuggestionModification) {
            FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationSuggestionModificationTrackerService.getModificationSuggestionModification(fileModificationId);
            startIndex = fileModificationSuggestionModification.getRangeMarker().getStartOffset();
            endIndex = fileModificationSuggestionModification.getRangeMarker().getEndOffset();
            } else {
            FileModification fileModification = fileModificationTrackerService.getModification(fileModificationId);
            startIndex = fileModification.getRangeMarker().getStartOffset();
            endIndex = fileModification.getRangeMarker().getEndOffset();
        }
        int startIndexOfReference = textAdded ? startIndex : previousStartIndex;
        int endIndexOfReference = textAdded ? endIndex : previousEndIndex;
        if (((offset > startIndexOfReference && offset + length <= endIndexOfReference)
                || (offset <= startIndexOfReference && offset + length > startIndexOfReference)
                || (offset <= endIndexOfReference && offset + length > endIndexOfReference))) {
            JOptionPane.showMessageDialog(null, "This segment is uneditable", "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.getEdit().undo();
        }
        previousStartIndex = startIndex;
        previousEndIndex = endIndex;
    }
}
