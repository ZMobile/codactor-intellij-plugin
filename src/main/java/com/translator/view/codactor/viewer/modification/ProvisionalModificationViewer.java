/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.translator.view.codactor.viewer.modification;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.CheckBox;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.ide.file.FileOpenerService;
import com.translator.service.codactor.ai.modification.diff.AiFileModificationSuggestionDiffViewerService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.modification.ProvisionalModificationCustomizerDialogManager;
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
    private JToolBar toolbar;
    private JButton backButton;
    private JButton acceptButton;
    private JButton rejectAllButton;
    private JButton customizeButton;
    private JButton diffViewerButton;
    private JButton queueButton;
    private String fileModificationId;
    private CodactorToolWindowService codactorToolWindowService;
    private FileModificationTrackerService fileModificationTrackerService;
    private ProvisionalModificationCustomizerDialogManager provisionalModificationCustomizerDialogManager;
    private FileOpenerService fileOpenerService;
    private AiFileModificationSuggestionDiffViewerService aiFileModificationSuggestionDiffViewerService;
    private DiffEditorGeneratorService diffEditorGeneratorService;

    @Inject
    public ProvisionalModificationViewer(CodactorToolWindowService codactorToolWindowService,
                                         FileModificationTrackerService fileModificationTrackerService,
                                         ProvisionalModificationCustomizerDialogManager provisionalModificationCustomizerDialogManager,
                                         FileOpenerService fileOpenerService,
                                         AiFileModificationSuggestionDiffViewerService aiFileModificationSuggestionDiffViewerService,
                                         DiffEditorGeneratorService diffEditorGeneratorService) {
        this.codactorToolWindowService = codactorToolWindowService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.provisionalModificationCustomizerDialogManager = provisionalModificationCustomizerDialogManager;
        this.fileOpenerService = fileOpenerService;
        this.aiFileModificationSuggestionDiffViewerService = aiFileModificationSuggestionDiffViewerService;
        this.diffEditorGeneratorService = diffEditorGeneratorService;
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

        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorderPainted(false);

        backButton = new JButton("\u2190"); // "\u2190" is a unicode character for a left arrow
        backButton.setFocusable(false);
        backButton.setHorizontalTextPosition(SwingConstants.LEADING);
        backButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbar.add(backButton);

        toolbar.addSeparator();

        acceptButton = new JButton("Accept Solution");
        acceptButton.setFocusable(false);
        acceptButton.setHorizontalTextPosition(SwingConstants.CENTER);
        acceptButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        acceptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolbar.add(acceptButton);

        rejectAllButton = new JButton("Reject All Changes");
        rejectAllButton.setFocusable(false);
        rejectAllButton.setHorizontalTextPosition(SwingConstants.CENTER);
        rejectAllButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        rejectAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolbar.add(rejectAllButton);

        toolbar.addSeparator();

        customizeButton = new JButton("Customize");
        customizeButton.setFocusable(false);
        customizeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        customizeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        customizeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolbar.add(customizeButton);

        diffViewerButton = new JButton("Diff Viewer");
        diffViewerButton.setFocusable(false);
        diffViewerButton.setHorizontalTextPosition(SwingConstants.CENTER);
        diffViewerButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        diffViewerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolbar.add(diffViewerButton);

        toolbar.addSeparator();

        // Add a check box to the right of the diff viewer button
        JCheckBox restoredCheckBox = new JCheckBox("Code Restored");
        restoredCheckBox.setFocusable(false);
        restoredCheckBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
        toolbar.add(restoredCheckBox);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().get(0);
                fileModificationTrackerService.implementModification(fileModificationId, fileModificationSuggestion.getSuggestedCodeEditor().getDocument().getText(), false);
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
                provisionalModificationCustomizerDialogManager.addProvisionalModificationCustomizerDialog(fileModification.getModificationOptions().get(0));
            }
        });

        diffViewerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().get(0);
                String beforeCode = fileModificationSuggestion.getBeforeCode();
                String afterCode = fileModificationSuggestion.getSuggestedCodeEditor().getDocument().getText();
                aiFileModificationSuggestionDiffViewerService.showDiffViewer(fileModification.getFilePath(), beforeCode, afterCode);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codactorToolWindowService.openModificationQueueViewerToolWindow();
            }
        });

        restoredCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Differentiate between whether it was checked or unchecked:
                updateCodeRestoration(restoredCheckBox.isSelected());
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(toolbar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(toolbar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
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

    public void updateCodeRestoration(boolean codeRestored) {
        FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().get(0);
        if (codeRestored) {
            diffEditorGeneratorService.updateDiffEditor(fileModificationSuggestion.getDiffEditor(), fileModificationSuggestion.getBeforeCode(), fileModificationSuggestion.getSuggestedCode());
        } else {
            diffEditorGeneratorService.updateDiffEditor(fileModificationSuggestion.getDiffEditor(), fileModificationSuggestion.getBeforeCode(), fileModificationSuggestion.getSuggestedCodeBeforeRestoration());
        }
    }
}
