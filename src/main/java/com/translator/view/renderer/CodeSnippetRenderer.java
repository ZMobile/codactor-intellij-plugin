/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.translator.view.renderer;

import com.translator.view.viewer.CodeSnippetViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author zantehays
 */
public class CodeSnippetRenderer extends JPanel implements ListCellRenderer<CodeSnippetViewer> {

    public CodeSnippetRenderer() {

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
    public Component getListCellRendererComponent(JList<? extends CodeSnippetViewer> list, CodeSnippetViewer value, int index, boolean isSelected, boolean cellHasFocus) {
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
        return this;
    }
}
