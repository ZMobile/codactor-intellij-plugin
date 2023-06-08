package com.translator.view.codactor.renderer;

import javax.swing.*;
import java.awt.*;

public class SeparatorListCellRenderer<T> implements ListCellRenderer<T> {
    private final ListCellRenderer<T> delegate;

    public SeparatorListCellRenderer(ListCellRenderer<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        }
        return c;
    }
}
