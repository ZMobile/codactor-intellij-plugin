package com.translator.view.uml.dialog;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.ui.ComboBox;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;
import com.translator.view.uml.dialog.panel.PromptConnectionViewer;
import com.translator.view.uml.dialog.panel.PromptViewer;

import javax.swing.*;
import java.awt.*;

public class PromptNodeDialog extends JDialog {
    private PromptConnectionViewer promptConnectionViewer;
    private PromptViewer promptViewer;
    private LabeledRectangleFigure promptNodeFigure;
    private PromptNode promptNode;
    private JButton runButton;
    private ComboBox<String> modelComboBox;

    @Inject
    public PromptNodeDialog(@Assisted LabeledRectangleFigure promptNodeFigure, Gson gson) {
        super();

        this.promptNodeFigure = promptNodeFigure;
        this.promptNode = gson.fromJson(promptNodeFigure.getMetadata(), PromptNode.class);
        this.promptConnectionViewer = new PromptConnectionViewer(promptNode);
        this.promptViewer = new PromptViewer(promptNode);

        setTitle("Prompt Node");
        setSize(400, 500);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);

        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-4", "gpt-4-32k", "gpt-4-0314", "gpt-4-32k-0314"});
        modelComboBox.setMaximumSize(new Dimension(150, modelComboBox.getHeight()));
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
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(modelComboBox);

        runButton = new JButton("Run");
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
}