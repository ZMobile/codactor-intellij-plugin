package com.translator.view.renderer;

import com.intellij.openapi.application.ApplicationManager;
import com.translator.view.viewer.InquiryChatViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InquiryChatRenderer extends JPanel implements ListCellRenderer<InquiryChatViewer> {

    public InquiryChatRenderer() {

        setLayout(new BorderLayout());
        // Add mouse listeners to the CodeSnippetViewer component
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Handle mouse click event
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                // Handle mouse enter event
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // Handle mouse exit event
            }
        });
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends InquiryChatViewer> list, InquiryChatViewer value, int index, boolean isSelected, boolean cellHasFocus) {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            removeAll();
            add(value, BorderLayout.CENTER);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
        });
        return this;
    }
}
