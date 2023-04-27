package com.translator.view.dialog;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.translator.model.modification.ModificationType;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileModificationErrorDialog extends JDialog {

    private String filePath;
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private FileModificationTrackerService fileModificationTrackerService;

    public FileModificationErrorDialog(JFrame parent,
                                       String filePath,
                                       String error,
                                       ModificationType modificationType,
                                       OpenAiApiKeyService openAiApiKeyService,
                                       OpenAiModelService openAiModelService,
                                       FileModificationTrackerService fileModificationTrackerService) {
        super(parent, "File Modification Error", true);
        this.filePath = filePath;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.fileModificationTrackerService = fileModificationTrackerService;

        JBPanel messagePanel = new JBPanel();
        if (error != null) {
            messagePanel.add(new JBLabel("Failed to generate File Modification: " + modificationType + " " + filePath + " + " + error));
        } else {
            messagePanel.add(new JBLabel("Failed to generate File Modification: " + modificationType + " " + filePath));
        }
        JBPanel buttonPanel = new JBPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton removeButton = new JButton("Remove Modification");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Remove Modification action
                dispose();
            }
        });

        JButton leaveButton = new JButton("Leave Modification in Queue");
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Leave Modification in Queue action
                dispose();
            }
        });

        JButton retryButton = new JButton("Retry Modification");
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Retry Modification action
                dispose();
            }
        });

        // Add JComboBox modelComboBox
        ComboBox<String> modelComboBox = new ComboBox<>();
        // Populate modelComboBox with items
        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-4", "gpt-4-32k", "gpt-4-0314", "gpt-4-32k-0314"});
// Get the index of the selected element

// Get the index of the selected element
        String selectedElement = openAiModelService.getSelectedOpenAiModel();
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

        JButton reenterButton = new JButton("Re-enter openAi Api Key");
        reenterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Re-enter openAi Api Key action
                OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                dispose();
            }
        });

        buttonPanel.add(removeButton);
        buttonPanel.add(leaveButton);
        buttonPanel.add(retryButton);
        buttonPanel.add(modelComboBox); // Add modelComboBox to the buttonPanel
        buttonPanel.add(reenterButton);

        getContentPane().add(messagePanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }
}

