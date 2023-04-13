package com.translator.view.listener;

import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestionModification;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;

public class UneditableSegmentListener implements UndoableEditListener {
    private JBTextArea textArea;
    private String fileModificationId;
    private int previousStartIndex;
    private int previousEndIndex;
    private FileModificationTrackerService fileModificationTrackerService;
    private boolean modificationSuggestionModification;

    public UneditableSegmentListener(String fileModificationId, FileModificationTrackerService fileModificationTrackerService, JBTextArea textArea, boolean modificationSuggestionModification) {
        this.textArea = textArea;
        this.fileModificationId = fileModificationId;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.modificationSuggestionModification = modificationSuggestionModification;
        if (modificationSuggestionModification) {
            FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationTrackerService.getModificationSuggestionModification(fileModificationId);
            previousStartIndex = fileModificationSuggestionModification.getStartIndex();
            previousEndIndex = fileModificationSuggestionModification.getEndIndex();
        } else {
            FileModification fileModification = fileModificationTrackerService.getModification(fileModificationId);
            previousStartIndex = fileModification.getStartIndex();
            previousEndIndex = fileModification.getEndIndex();
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
            FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationTrackerService.getModificationSuggestionModification(fileModificationId);
            startIndex = fileModificationSuggestionModification.getStartIndex();
            endIndex = fileModificationSuggestionModification.getEndIndex();
            } else {
            FileModification fileModification = fileModificationTrackerService.getModification(fileModificationId);
            startIndex = fileModification.getStartIndex();
            endIndex = fileModification.getEndIndex();
        }
        int startIndexOfReference = textAdded ? startIndex : previousStartIndex;
        int endIndexOfReference = textAdded ? endIndex : previousEndIndex;
        if (((offset > startIndexOfReference && offset + length <= endIndexOfReference)
                || (offset <= startIndexOfReference && offset + length > startIndexOfReference)
                || (offset <= endIndexOfReference && offset + length > endIndexOfReference))) {
            JOptionPane.showMessageDialog(textArea, "This segment is uneditable", "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.getEdit().undo();
        }
        previousStartIndex = startIndex;
        previousEndIndex = endIndex;
    }
}
