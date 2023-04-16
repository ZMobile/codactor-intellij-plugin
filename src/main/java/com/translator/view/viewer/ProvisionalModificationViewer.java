/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.translator.view.viewer;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.ProvisionalModificationCustomizer;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestion;
import com.translator.view.factory.ProvisionalModificationCustomizerFactory;
import com.translator.view.renderer.CodeSnippetRenderer;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.intellij.ui.components.JBTextArea;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author zantehays
 */
public class ProvisionalModificationViewer extends JBPanel<ProvisionalModificationViewer> {

    public interface ModificationActionSelectedListener {
        void onModificationAccepted(String fileModificationId, String modification);

        void onModificationsRejected(String fileModificationId);
    }

    private FileModification fileModification;
    private JList<CodeSnippetViewer> jList1;
    private JToolBar jToolBar2;
    private JButton acceptButton;
    private JButton rejectAllButton;
    private JButton customizeButton;
    private String fileModificationId;
    private CodactorToolWindowService codactorToolWindowService;
    private ModificationActionSelectedListener listener;
    private ProvisionalModificationCustomizerFactory provisionalModificationCustomizerFactory;

    @Inject
    public ProvisionalModificationViewer(CodactorToolWindowService codactorToolWindowService,
                                         ProvisionalModificationCustomizerFactory provisionalModificationCustomizerFactory) {
        this.codactorToolWindowService = codactorToolWindowService;
        this.provisionalModificationCustomizerFactory = provisionalModificationCustomizerFactory;
        this.listener = null;
        initComponents();
    }

    private void initComponents() {
        jList1 = new JList<>();
        jList1.setModel(new DefaultListModel<>());
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList1.setCellRenderer(new CodeSnippetRenderer());
        jList1.setBackground(Color.LIGHT_GRAY); // Set the background of the JList to grey
        jList1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = jList1.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        updateSelectedModification(selectedIndex);
                    }
                }
            }
        });
        JBScrollPane jBScrollPane1 = new JBScrollPane(jList1);

        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        acceptButton = new JButton("Accept Solution");
        acceptButton.setFocusable(false);
        acceptButton.setHorizontalTextPosition(SwingConstants.CENTER);
        acceptButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        acceptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(acceptButton);

        rejectAllButton = new JButton("Reject All Changes");
        rejectAllButton.setFocusable(false);
        rejectAllButton.setHorizontalTextPosition(SwingConstants.CENTER);
        rejectAllButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        rejectAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(rejectAllButton);

        customizeButton = new JButton("Customize...");
        customizeButton.setFocusable(false);
        customizeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        customizeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        customizeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(customizeButton);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.onModificationAccepted(fileModificationId, jList1.getModel().getElementAt(0).getText());
                    codactorToolWindowService.closeModificationQueueViewerToolWindow();
                    updateModificationList(null);
                }
            }
        });

        rejectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.onModificationsRejected(fileModificationId);
                    codactorToolWindowService.closeModificationQueueViewerToolWindow();
                    updateModificationList(null);
                }
            }
        });

        ProvisionalModificationViewer provisionalModificationViewer = this;
        customizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProvisionalModificationCustomizer provisionalModificationCustomizer = provisionalModificationCustomizerFactory.create(fileModification);
                provisionalModificationCustomizer.setVisible(true);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
    }

    public void updateModificationList(FileModification fileModification) {
        this.fileModification = fileModification;
        if (fileModification == null) {
            jList1.setModel(new DefaultListModel<>());
            fileModificationId = null;
            return;
        }
        DefaultListModel<CodeSnippetViewer> model = new DefaultListModel<>();
        //for (int i = 0; i < numFileModifications; i++) {
            FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().get(0);
            CodeSnippetViewer viewer = new CodeSnippetViewer(fileModificationSuggestion.getSuggestedCode());
            //if (i == 0) {
                viewer.setBackground(JBColor.GREEN);
            //} else {
                //viewer.setBackground(Color.LIGHT_GRAY);
            //}
            model.addElement(viewer);
        //}
        jList1.setModel(model);
        fileModificationId = fileModification.getId();
    }

    public void updateSelectedModification(int selectedIndex) {
        DefaultListModel<CodeSnippetViewer> model = new DefaultListModel<>();
        for (int i = 0; i < jList1.getModel().getSize(); i++) {
            CodeSnippetViewer viewer = jList1.getModel().getElementAt(i);
            /*if (i != selectedIndex) {
                //viewer.setBackground(JBColor.LIGHT_GRAY);
            } else {
                viewer.setBackground(JBColor.GREEN);
            }*/
            model.addElement(viewer);
        }
        jList1.setModel(model);
    }

    public void setModificationActionSelectedListener(ModificationActionSelectedListener listener) {
        this.listener = listener;
    }
}
