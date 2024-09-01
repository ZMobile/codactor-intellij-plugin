/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.translator.view.codactor.viewer;

/**
 *
 * @author zantehays
 */

import com.intellij.openapi.editor.Editor;
import com.intellij.ui.components.JBPanel;
import com.translator.view.codactor.panel.FixedHeightPanel;

import javax.swing.*;
import java.awt.*;

public class CodeSnippetViewer extends JBPanel<CodeSnippetViewer> {
    private JToolBar jToolBar1;
    private JLabel jLabel1;
    private Editor editor;

    public CodeSnippetViewer(Editor editor) {
        this.editor = editor;
        initComponents();
    }


    private void initComponents() {
        initComponents("Solution");
    }

    private void initComponents(String header) {
        jToolBar1 = new JToolBar();
        jToolBar1.setBackground(Color.darkGray);
        jToolBar1.setFloatable(false);
        jToolBar1.setBorderPainted(false);
        jLabel1 = new JLabel();

        jToolBar1.setRollover(true);

        jLabel1.setText(header);
        jToolBar1.add(jLabel1);

        // Use BorderLayout to add the JBScrollPane to the center of the panel
        setLayout(new BorderLayout());
        add(jToolBar1, BorderLayout.NORTH);
        editor.getComponent().setPreferredSize(new Dimension(Integer.MAX_VALUE, editor.getComponent().getPreferredSize().height));
        FixedHeightPanel fixedHeightPanel = new FixedHeightPanel(editor);
        fixedHeightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fixedHeightPanel.add(editor.getComponent());
        add(fixedHeightPanel, BorderLayout.CENTER);

    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public JToolBar getToolBar() {
        return jToolBar1;
    }

    public String getText() {
        return editor.getDocument().getText();
    }

    public void setToolbarBackground(Color color) {
        jToolBar1.setBackground(color);
    }
}