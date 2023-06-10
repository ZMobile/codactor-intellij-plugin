package com.translator.view.uml.dialog.prompt;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.service.uml.node.NodeDialogWindowMapperService;
import com.translator.service.uml.node.PromptHighlighterService;
import com.translator.service.uml.node.PromptNodeDialogRunnerService;
import org.jhotdraw.draw.Drawing;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PromptNodeDialog extends JDialog {
    private Project project;
    private PromptConnectionViewer promptConnectionViewer;
    private PromptViewer promptViewer;
    private LabeledRectangleFigure promptNodeFigure;
    private Drawing drawing;
    private PromptNode promptNode;
    private JButton runButton;
    private JButton cancelButton;
    private JButton resetButton;
    private ComboBox<String> modelComboBox;
    private PromptNodeDialogRunnerService promptNodeDialogRunnerService;
    private PromptHighlighterService promptHighlighterService;
    private BackgroundTaskMapperService backgroundTaskMapperService;
    private NodeDialogWindowMapperService nodeDialogWindowMapperService;

    @Inject
    public PromptNodeDialog(@Assisted LabeledRectangleFigure promptNodeFigure,
                            @Assisted Drawing drawing,
                            Project project,
                            PromptNodeDialogRunnerService promptNodeDialogRunnerService,
                            PromptHighlighterService promptHighlighterService,
                            BackgroundTaskMapperService backgroundTaskMapperService,
                            NodeDialogWindowMapperService nodeDialogWindowMapperService,
                            Gson gson) {
        super();
        this.project = project;
        this.promptNodeFigure = promptNodeFigure;
        this.drawing = drawing;
        this.promptNode = gson.fromJson(promptNodeFigure.getMetadata(), PromptNode.class);
        this.promptConnectionViewer = new PromptConnectionViewer(this, promptNode, drawing, gson, nodeDialogWindowMapperService, promptHighlighterService);
        this.promptViewer = new PromptViewer(this, promptHighlighterService);
        this.promptNodeDialogRunnerService = promptNodeDialogRunnerService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;

        setTitle("Prompt Node");
        setSize(400, 500);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);

        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-4", "gpt-4-32k", "gpt-4-0314", "gpt-4-32k-0314"});
        //modelComboBox.setMaximumSize(new Dimension(150, runButton.getHeight()));
        String selectedElement = promptNode.getAiModel();
        int selectedIndex = -1;
        for (int i = 0; i < modelComboBox.getItemCount(); i++) {
            if (selectedElement.equals(modelComboBox.getItemAt(i))) {
                selectedIndex = i;
                break;
            }
        }

        // Check if the selected element is in the combo box
        if (selectedIndex != -1 && selectedIndex != 0) {
            // Store the element at position 0
            String elementAtZero = modelComboBox.getItemAt(0);

            // Remove the selected element from the combo box
            modelComboBox.removeItemAt(selectedIndex);

            // Insert the selected element at the first position
            modelComboBox.insertItemAt(selectedElement, 0);

            // Remove the element at position 1 (which was previously at position 0)
            modelComboBox.removeItemAt(1);

            // Insert the element that was previously at position 0 to the original position of the selected element
            modelComboBox.insertItemAt(elementAtZero, selectedIndex);

            // Set the selected index to 0
            modelComboBox.setSelectedIndex(0);
        }

        modelComboBox.addActionListener(e -> {
            ComboBox<String> cb = (ComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                promptNode.setAiModel(model);
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            backgroundTaskMapperService.cancelTask(promptNode.getId());
            promptNode.setProcessed(true);
            cancelButton.setVisible(false);
            resetButton.setVisible(true);
            runButton.setEnabled(true);
            runButton.setText("Re-Run");
        });
        cancelButton.setVisible(promptNode.isRunning());
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            System.out.println("Reset button clicked");
            promptViewer.updatePromptChatContents(promptNode.getPromptList());
            runButton.setText("Run");
        });
        resetButton.setVisible(promptNode.isProcessed());
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(modelComboBox);
        toolBar.add(cancelButton);
        toolBar.add(resetButton);
        runButton = new JButton("Run");
        runButton.setEnabled(!promptNode.isRunning());
        if (promptNode.isProcessed()) {
            runButton.setText("Re-run");
        }
        runButton.addActionListener(e -> {
            runButton.setEnabled(false);
            cancelButton.setVisible(true);
            System.out.println("Run button clicked");
            promptNodeDialogRunnerService.run(promptNodeFigure, promptNode, Objects.requireNonNull(modelComboBox.getSelectedItem()).toString());
            promptNodeFigure.setMetadata(gson.toJson(promptNode));
        });



        toolBar.add(runButton);

        add(toolBar, BorderLayout.NORTH);
        panel.add(promptConnectionViewer, BorderLayout.NORTH);
        panel.add(promptViewer, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }

    public PromptConnectionViewer getPromptConnectionViewer() {
        return promptConnectionViewer;
    }

    public PromptViewer getPromptViewer() {
        return promptViewer;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public JButton getRunButton() {
        return runButton;
    }

    public PromptNode getPromptNode() {
        return promptNode;
    }
}