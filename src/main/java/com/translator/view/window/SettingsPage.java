package com.translator.view.window;

import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.service.openai.OpenAiModelService;

import javax.swing.*;
import java.awt.*;

public class SettingsPage extends JFrame {

    private JComboBox<String> aiModelComboBox;
    private FirebaseTokenService firebaseTokenService;
    private OpenAiModelService openAiModelService;

    public SettingsPage(FirebaseTokenService firebaseTokenService, OpenAiModelService openAiModelService) {
        this.firebaseTokenService = firebaseTokenService;
        this.openAiModelService = openAiModelService;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Settings");
        setSize(400, 300);

        JPanel mainPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        mainPanel.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel aiModelLabel = new JLabel("AI Model:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(aiModelLabel, constraints);

        aiModelComboBox = new JComboBox<>();
        aiModelComboBox.addItem("gpt-3.5-turbo");
        aiModelComboBox.addItem("gpt-4");
        aiModelComboBox.addItem("gpt-4-32k");
        aiModelComboBox.setSelectedItem(openAiModelService.getSelectedOpenAiModel());
        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel.add(aiModelComboBox, constraints);

        JButton logOutButton = new JButton("Log Out");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.WEST;
        logOutButton.addActionListener(e -> {
            firebaseTokenService.logout();
            System.exit(0);
        });
        mainPanel.add(logOutButton, constraints);

        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        toolbarPanel.add(cancelButton);
        JButton saveChangesButton = new JButton("Save Changes");
        saveChangesButton.addActionListener(e -> {
            openAiModelService.setSelectedOpenAiModel((String) aiModelComboBox.getSelectedItem());
            dispose();
        });
        toolbarPanel.add(saveChangesButton);
        buttonsPanel.add(toolbarPanel, BorderLayout.SOUTH);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2; // span the buttonsPanel across both columns
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        mainPanel.add(buttonsPanel, constraints);

        add(mainPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
