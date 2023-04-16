package com.translator.service.modification.tracking.listener;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class UneditableSegmentFilter extends DocumentFilter {
    private final int startOffset;
    private final int endOffset;

    public UneditableSegmentFilter(int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (offset >= startOffset && offset + string.length() <= endOffset) {
            JOptionPane.showMessageDialog(null, "This segment is uneditable", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.insertString(fb, offset, string, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        if (offset >= startOffset && offset + length <= endOffset) {
            JOptionPane.showMessageDialog(null, "This segment is uneditable", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.remove(fb, offset, length);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (offset >= startOffset && offset + length <= endOffset) {
            JOptionPane.showMessageDialog(null, "This segment is uneditable", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.replace(fb, offset, length, text, attrs);
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }
}