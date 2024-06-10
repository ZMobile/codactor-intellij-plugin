package com.translator.view.codactor.dialog;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.service.codactor.ai.modification.AiFileModificationRestarterService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.view.codactor.settings.CodactorConfigurable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileModificationErrorDialog extends JDialog {
    private Project project;
    private String modificationId;
    private String filePath;
    private DefaultConnectionService defaultConnectionService;
    private OpenAiModelService openAiModelService;
    private FileModificationTrackerService fileModificationTrackerService;
    private AiFileModificationRestarterService aiFileModificationRestarterService;

    @Inject
    public FileModificationErrorDialog(@Assisted("modificationId") String modificationId,
                                       @Assisted("filePath") String filePath,
                                       @Assisted("error") String error,
                                       @Assisted ModificationType modificationType,
                                       Project project,
                                       DefaultConnectionService defaultConnectionService,
                                       OpenAiModelService openAiModelService,
                                       FileModificationTrackerService fileModificationTrackerService,
                                       AiFileModificationRestarterService aiFileModificationRestarterService) {
        super();
        this.project = project;
        this.filePath = filePath;
        this.defaultConnectionService = defaultConnectionService;
        this.openAiModelService = openAiModelService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.aiFileModificationRestarterService = aiFileModificationRestarterService;

        JBPanel messagePanel = new JBPanel();
        if (error != null) {
            messagePanel.add(new JBLabel("Failed to generate File Modification: " + modificationType + " " + filePath + ": " + error));
        } else {
            messagePanel.add(new JBLabel("Failed to generate File Modification: " + modificationType + " " + filePath));
        }
        JBPanel buttonPanel = new JBPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton removeButton = new JButton("Remove Modification");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileModificationTrackerService.removeModification(modificationId);
                dispose();
            }
        });

        JButton leaveButton = new JButton("Leave Modification in Queue");
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JButton retryButton = new JButton("Retry Modification");
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
                aiFileModificationRestarterService.restartFileModification(fileModification);
                dispose();
            }
        });

        // Add JComboBox modelComboBox
        ComboBox<String> modelComboBox = new ComboBox<>();
        // Populate modelComboBox with items
        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "gpt-4o"});
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

        modelComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                openAiModelService.setSelectedOpenAiModel(model);
            }
        });

        JButton reenterButton = new JButton("Re-enter openAi Api Key");
        reenterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Re-enter openAi Api Key action
                CodactorConfigurable configurable = new CodactorConfigurable();
                ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
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
        //setLocationRelativeTo(parent);
    }
}

