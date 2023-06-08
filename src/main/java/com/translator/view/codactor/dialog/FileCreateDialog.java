package com.translator.view.codactor.dialog;

import com.intellij.openapi.ui.ComboBox;
import com.translator.PromptContextBuilder;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.view.codactor.factory.PromptContextBuilderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FileCreateDialog extends JDialog {
    private OpenAiModelService openAiModelService;
    private PromptContextService promptContextService;
    private PromptContextBuilderFactory promptContextBuilderFactory;
    private JPanel contentPane;
    private JTextField fileNameInput;
    private JTextArea fileDescription;
    private JComboBox<String> modelComboBox;
    private JButton advancedButton;
    private JLabel hiddenLabel;
    private JButton okButton;
    private ActionListener okActionListener;

    public FileCreateDialog(OpenAiModelService openAiModelService,
                            PromptContextService promptContextService,
                            PromptContextBuilderFactory promptContextBuilderFactory,
                            ActionListener okActionListener) {
        this.openAiModelService = openAiModelService;
        this.promptContextService = promptContextService;
        this.promptContextBuilderFactory = promptContextBuilderFactory;
        this.okActionListener = okActionListener;
        setModal(true);
        initUIComponents();
        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initUIComponents() {
        contentPane = new JPanel(new BorderLayout());
        fileNameInput = new JTextField();
        fileDescription = new JTextArea();
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
        modelComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                openAiModelService.setSelectedOpenAiModel(model);
            }
        });
        advancedButton = new JButton("(Advanced) Add Context");
        hiddenLabel = new JLabel();
        hiddenLabel.setVisible(false);

        modelComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                openAiModelService.setSelectedOpenAiModel(model);
            }
        });

        advancedButton.addActionListener(e -> {
            promptContextService.setStatusLabel(hiddenLabel);
            PromptContextBuilder promptContextBuilder = promptContextBuilderFactory.create(promptContextService);
            promptContextBuilder.setVisible(true);
        });

        contentPane.add(createTopPanel(), BorderLayout.NORTH);
        contentPane.add(createMainPanel(), BorderLayout.CENTER);
        okButton = new JButton("Create File");
        okButton.addActionListener(okActionListener);


        contentPane.add(createBottomPanel(), BorderLayout.SOUTH);
        // ... Other component additions ...
        setTitle("Create New File");
        setModal(true);
        pack();
    }


    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("File Name:"), BorderLayout.WEST);
        topPanel.add(fileNameInput, BorderLayout.CENTER);
        return topPanel;
    }

    private JPanel createMainPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("File Description:"), BorderLayout.NORTH);
        centerPanel.add(fileDescription, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4));
        bottomPanel.add(modelComboBox);
        bottomPanel.add(advancedButton);
        bottomPanel.add(hiddenLabel);
        bottomPanel.add(okButton); // Add the okButton here
        return bottomPanel;
    }

    public JTextField getFileNameInput() {
        return fileNameInput;
    }

    public JTextArea getFileDescription() {
        return fileDescription;
    }
}