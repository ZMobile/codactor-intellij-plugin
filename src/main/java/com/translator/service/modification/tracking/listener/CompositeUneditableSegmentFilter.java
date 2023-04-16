package com.translator.service.modification.tracking.listener;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.ArrayList;
import java.util.List;

public class CompositeUneditableSegmentFilter implements DocumentListener {
    private final List<UneditableSegmentFilter> filters;
    private boolean blockedLastChange;

    public CompositeUneditableSegmentFilter() {
        filters = new ArrayList<>();
        blockedLastChange = false;
    }

    public void addUneditableSegmentFilter(UneditableSegmentFilter filter) {
        filters.add(filter);
    }

    public void removeUneditableSegmentFilter(UneditableSegmentFilter filter) {
        filters.remove(filter);
    }

    @Override
    public void beforeDocumentChange(DocumentEvent event) {
        if (blockedLastChange) {
            blockedLastChange = false;
            return;
        }

        int offset = event.getOffset();
        int oldLength = event.getOldLength();
        int newLength = event.getNewLength();

        for (UneditableSegmentFilter filter : filters) {
            if (isUneditableChange(filter, offset, oldLength) || isUneditableChange(filter, offset, newLength)) {
                blockedLastChange = true;
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "This segment is uneditable", "Error",
                        JOptionPane.ERROR_MESSAGE));
                event.getDocument().removeDocumentListener(this);
                event.getDocument().replaceString(offset, offset + oldLength, event.getOldFragment().toString());
                event.getDocument().addDocumentListener(this);
                break;
            }
        }
    }

    private boolean isUneditableChange(UneditableSegmentFilter filter, int offset, int length) {
        return offset >= filter.getStartOffset() && offset + length <= filter.getEndOffset();
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        // This method is called after the document change occurs
    }
}