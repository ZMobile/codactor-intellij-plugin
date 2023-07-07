/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.translator.view.codactor.viewer.modification;

import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestion;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.service.codactor.file.FileOpenerService;
import com.translator.service.codactor.modification.FileModificationSuggestionDiffViewerService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.ProvisionalModificationCustomizerDialog;
import com.translator.view.codactor.factory.dialog.ProvisionalModificationCustomizerDialogFactory;
import com.translator.view.codactor.renderer.CodeSnippetRenderer;
import com.translator.view.codactor.viewer.CodeSnippetViewer;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author zantehays
 */
public class ProvisionalModificationViewer extends JBPanel<ProvisionalModificationViewer> {
    private FileModification fileModification;
    private JList<CodeSnippetViewer> codeSnippetList;
    private JToolBar jToolBar2;
    private JButton acceptButton;
    private JButton rejectAllButton;
    private JButton customizeButton;
    private JButton diffViewerButton;
    private String fileModificationId;
    private CodactorToolWindowService codactorToolWindowService;
    private FileModificationTrackerService fileModificationTrackerService;
    private FileOpenerService fileOpenerService;
    private FileModificationSuggestionDiffViewerService fileModificationSuggestionDiffViewerService;
    private ProvisionalModificationCustomizerDialogFactory provisionalModificationCustomizerDialogFactory;

    @Inject
    public ProvisionalModificationViewer(CodactorToolWindowService codactorToolWindowService,
                                         FileModificationTrackerService fileModificationTrackerService,
                                         FileOpenerService fileOpenerService,
                                         FileModificationSuggestionDiffViewerService fileModificationSuggestionDiffViewerService,
                                         ProvisionalModificationCustomizerDialogFactory provisionalModificationCustomizerDialogFactory) {
        this.codactorToolWindowService = codactorToolWindowService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileOpenerService = fileOpenerService;
        this.fileModificationSuggestionDiffViewerService = fileModificationSuggestionDiffViewerService;
        this.provisionalModificationCustomizerDialogFactory = provisionalModificationCustomizerDialogFactory;
        initComponents();
    }

    private void initComponents() {
        codeSnippetList = new JList<>();
        codeSnippetList.setModel(new DefaultListModel<>());
        codeSnippetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        codeSnippetList.setCellRenderer(new CodeSnippetRenderer());
        codeSnippetList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = codeSnippetList.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        updateSelectedModification(selectedIndex);
                    }
                }
            }
        });
        JBScrollPane jBScrollPane1 = new JBScrollPane(codeSnippetList);

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

        customizeButton = new JButton("Customize");
        customizeButton.setFocusable(false);
        customizeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        customizeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        customizeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(customizeButton);

        diffViewerButton = new JButton("Diff Viewer");
        diffViewerButton.setFocusable(false);
        diffViewerButton.setHorizontalTextPosition(SwingConstants.CENTER);
        diffViewerButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        diffViewerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(diffViewerButton);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().get(0);
                fileModificationTrackerService.implementModificationUpdate(fileModificationId, fileModificationSuggestion.getSuggestedCodeEditor().getDocument().getText(), false);
                List<FileModification> modifications = fileModificationTrackerService.getAllFileModifications();
                boolean anyDone = false;
                FileModification nextModification = null;
                for (FileModification modification : modifications) {
                    if (modification.isDone()) {
                        anyDone = true;
                        nextModification = modification;
                        break;
                    }
                }
                if (anyDone) {
                    updateModificationList(nextModification);
                    FileModification finalNextModification = nextModification;
                    ApplicationManager.getApplication().invokeLater(() -> {
                        fileOpenerService.openFileInEditor(finalNextModification.getFilePath(), finalNextModification.getRangeMarker().getStartOffset());
                    });
                } else {
                    codactorToolWindowService.closeModificationQueueViewerToolWindow();
                    updateModificationList(null);
                }
            }
        });
        acceptButton.setEnabled(true);

        rejectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileModificationTrackerService.removeModification(fileModificationId);
                List<FileModification> modifications = fileModificationTrackerService.getAllFileModifications();
                boolean anyDone = false;
                FileModification nextModification = null;
                for (FileModification modification : modifications) {
                    if (modification.isDone()) {
                        anyDone = true;
                        nextModification = modification;
                        break;
                    }
                }
                if (anyDone) {
                    updateModificationList(nextModification);
                } else {
                    codactorToolWindowService.closeModificationQueueViewerToolWindow();
                    updateModificationList(null);
                }
            }
        });
        rejectAllButton.setEnabled(true);

        customizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProvisionalModificationCustomizerDialog provisionalModificationCustomizerDialog = provisionalModificationCustomizerDialogFactory.create(fileModification.getModificationOptions().get(0));
                provisionalModificationCustomizerDialog.setVisible(true);
                fileModificationTrackerService.addProvisionalModificationCustomizer(provisionalModificationCustomizerDialog);
            }
        });

        diffViewerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().get(0);
                String beforeCode = fileModificationSuggestion.getBeforeCode();
                String afterCode = fileModificationSuggestion.getSuggestedCodeEditor().getDocument().getText();
                fileModificationSuggestionDiffViewerService.showDiffViewer(fileModification.getFilePath(), beforeCode, afterCode);
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
            codeSnippetList.setModel(new DefaultListModel<>());
            fileModificationId = null;
            return;
        }
        DefaultListModel<CodeSnippetViewer> model = new DefaultListModel<>();
        //for (int i = 0; i < numFileModifications; i++) {
            FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().get(0);
        CodeSnippetViewer viewer;
        if (fileModification.getModificationType() == ModificationType.MODIFY
            || fileModification.getModificationType() == ModificationType.MODIFY_SELECTION
            || fileModification.getModificationType() == ModificationType.FIX
            || fileModification.getModificationType() == ModificationType.FIX_SELECTION) {
            viewer = new CodeSnippetViewer(fileModificationSuggestion.getDiffEditor());
        } else {
            viewer = new CodeSnippetViewer(fileModificationSuggestion.getSuggestedCodeEditor());
        }
        //if (i == 0) {
        viewer.setBackground(Color.decode("#228B22"));
            //} else {
                //viewer.setBackground(Color.LIGHT_GRAY);
            //}
        model.addElement(viewer);
        //}
        codeSnippetList.setModel(model);
        fileModificationId = fileModification.getId();
    }

    public void updateSelectedModification(int selectedIndex) {
        DefaultListModel<CodeSnippetViewer> model = new DefaultListModel<>();
        for (int i = 0; i < codeSnippetList.getModel().getSize(); i++) {
            CodeSnippetViewer viewer = codeSnippetList.getModel().getElementAt(i);
            /*if (i != selectedIndex) {
                //viewer.setBackground(JBColor.LIGHT_GRAY);
            } else {
                viewer.setBackground(Color.decode("#228B22"));
            }*/
            model.addElement(viewer);
        }
        codeSnippetList.setModel(model);
    }
}
