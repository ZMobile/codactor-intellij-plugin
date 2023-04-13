/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.translator.view.viewer;

/**
 *
 * @author zantehays
 */

import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextArea;


import javax.swing.*;
import java.awt.*;

public class CodeSnippetViewer extends JBPanel {
    private JToolBar jToolBar1;
    private JLabel jLabel1;
    private JBTextArea jBTextArea1;

    public CodeSnippetViewer(JBTextArea jBTextArea1) {
        this.jBTextArea1 = jBTextArea1;
        initComponents();
    }

    public CodeSnippetViewer(JBTextArea jBTextArea1, String header) {
        this.jBTextArea1 = jBTextArea1;
        this.jBTextArea1.setText(jBTextArea1.getText().trim());
        initComponents(header);
    }

    private void initComponents() {
        jToolBar1 = new JToolBar();
        jToolBar1.setFloatable(false);
        jToolBar1.setBorderPainted(false);
        jLabel1 = new JLabel();

        jToolBar1.setRollover(true);

        jLabel1.setText("Solution");
        jToolBar1.add(jLabel1);

        // Use BorderLayout to add the JBScrollPane to the center of the panel
        setLayout(new BorderLayout());
        add(jToolBar1, BorderLayout.NORTH);
        add(jBTextArea1, BorderLayout.CENTER);
    }

    private void initComponents(String header) {
        jToolBar1 = new JToolBar();
        jToolBar1.setFloatable(false);
        jToolBar1.setBorderPainted(false);
        jLabel1 = new JLabel();

        jToolBar1.setRollover(true);

        jLabel1.setText(header);
        jToolBar1.add(jLabel1);

        // Use BorderLayout to add the JBScrollPane to the center of the panel
        setLayout(new BorderLayout());
        add(jToolBar1, BorderLayout.NORTH);
        add(jBTextArea1, BorderLayout.CENTER);
    }

    public void setTextArea(JBTextArea jBTextArea1) {
        this.jBTextArea1 = jBTextArea1;
    }

    public JToolBar getToolBar() {
        return jToolBar1;
    }

    public String getText() {
        return jBTextArea1.getText();
    }

    public void setToolbarBackground(Color color) {
        jToolBar1.setBackground(color);
    }
}