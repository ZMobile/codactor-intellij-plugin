package com.translator.view.dialog;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.translator.PromptContextBuilder;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;
import com.translator.service.context.PromptContextService;
import com.translator.service.file.CodeFileGeneratorService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.factory.PromptContextBuilderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class MultiFileCreateDialog extends JDialog {
    // (Add other instance variables here as needed)

    private JPanel mainPanel;
    private JBTextArea descriptionTextArea;
    private List<HistoricalContextObjectHolder> priorContext;
    private Inquiry inquiry;
    private InquiryChat inquiryChat;
    private OpenAiModelService openAiModelService;
    private CodeFileGeneratorService codeFileGeneratorService;
    private CodactorToolWindowService codactorToolWindowService;
    private PromptContextService promptContextService;
    private JRadioButton defaultPathButton, customPathButton;
    private JToggleButton asyncFileCreationButton, oneAtATimeFileCreationButton;
    private JLabel customPathLabel, languageLabel, fileTypeLabel;
    private JFileChooser fileChooser;
    private ButtonGroup toggleGroup, fileCreationModeGroup;
    private JBTextField languageTextField, fileTypeTextField;
    private JComboBox<String> modelComboBox;
    private JButton advancedButton;
    private JLabel hiddenLabel;
    private JButton okButton;
    private VirtualFile selectedDirectory = null;

    public MultiFileCreateDialog(String filePath,
                                 String description,
                                 OpenAiModelService openAiModelService,
                                 CodactorToolWindowService codactorToolWindowService,
                                 CodeFileGeneratorService codeFileGeneratorService,
                                 PromptContextService promptContextService,
                                 PromptContextBuilderFactory promptContextBuilderFactory) {
        this.openAiModelService = openAiModelService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.codeFileGeneratorService = codeFileGeneratorService;
        this.promptContextService = promptContextService;
        setTitle("(Experimental) Multi-File Code Generator");
        setSize(600, 300);

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10);
        JLabel descriptionLabel = new JLabel("(GPT-4+ Recommended) Enter a description for the files to be created:");
        mainPanel.add(descriptionLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH; // Change this line
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Add this line
        gbc.insets = new Insets(10, 10, 0, 10);
        descriptionTextArea = new JBTextArea();
        if (description != null) {
            descriptionTextArea.setText(description);
        }
        descriptionTextArea.setRows(8); // Add this line, you can adjust the number of rows based on your preference
        descriptionTextArea.setColumns(60); // Add this line, you can adjust the number of columns based on your preference
        JBScrollPane scrollPane = new JBScrollPane(descriptionTextArea);
        mainPanel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10);

        // create the default and custom path buttons
        defaultPathButton = new JRadioButton("~/Codactor/Generated-Code");
        customPathButton = new JRadioButton("Specify a directory");

        // create the file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (filePath == null) {
            defaultPathButton.setSelected(true);
        } else {
            customPathButton.setSelected(true);
            selectedDirectory = LocalFileSystem.getInstance().findFileByPath(filePath);
            fileChooser.setSelectedFile(new File(filePath));
        }
        customPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
                Project project = ProjectManager.getInstance().getDefaultProject();
                VirtualFile[] selectedDirectories = FileChooser.chooseFiles(fileChooserDescriptor, project, null);

                if (selectedDirectories.length > 0) {
                    VirtualFile selectedDirectory = selectedDirectories[0];
                    customPathLabel.setText("Selected directory: " + selectedDirectory.getPath());
                    MultiFileCreateDialog.this.selectedDirectory = selectedDirectory;
                    customPathLabel.setVisible(true);
                    customPathLabel.setPreferredSize(new Dimension(500, 20));
                } else {
                    toggleGroup.setSelected(defaultPathButton.getModel(), true);
                }
            }
        });
        defaultPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customPathLabel.setVisible(false);
                customPathLabel.setPreferredSize(new Dimension(0, 0));
            }
        });

        toggleGroup = new ButtonGroup();
        toggleGroup.add(defaultPathButton);
        toggleGroup.add(customPathButton);

        // add the radio buttons to a horizontal box
        JPanel radioPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        radioPanel.add(defaultPathButton);
        radioPanel.add(customPathButton);
        mainPanel.add(radioPanel, gbc);

        // add the custom path label
        customPathLabel = new JLabel("");
        if (filePath != null) {
            customPathLabel.setText("Selected directory: " + filePath);
            customPathLabel.setVisible(true);
            customPathLabel.setPreferredSize(new Dimension(500, 20));
        }
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 0, 0);
        mainPanel.add(customPathLabel, gbc);

        // create the file creation mode buttons
        asyncFileCreationButton = new JToggleButton("Asynchronous File Creation (Faster)");
        asyncFileCreationButton.setSelected(false);
        asyncFileCreationButton.setEnabled(true);
        asyncFileCreationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                asyncFileCreationButton.setEnabled(!asyncFileCreationButton.isSelected());
                if (asyncFileCreationButton.isSelected()) {
                    oneAtATimeFileCreationButton.setSelected(false);
                    oneAtATimeFileCreationButton.setEnabled(true);
                }
            }
        });

        oneAtATimeFileCreationButton = new JToggleButton("One-at-a-time File Creation (Reliable)");
        oneAtATimeFileCreationButton.setSelected(true);
        oneAtATimeFileCreationButton.setEnabled(false);
        oneAtATimeFileCreationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oneAtATimeFileCreationButton.setEnabled(!oneAtATimeFileCreationButton.isSelected());
                if (oneAtATimeFileCreationButton.isSelected()) {
                    asyncFileCreationButton.setSelected(false);
                    asyncFileCreationButton.setEnabled(true);
                }
            }
        });

        fileCreationModeGroup = new ButtonGroup();
        fileCreationModeGroup.add(asyncFileCreationButton);
        fileCreationModeGroup.add(oneAtATimeFileCreationButton);
//centerPanel.add(new JLabel("File Description:"), BorderLayout.NORTH);
//        centerPanel.add(fileDescription, BorderLayout.CENTER);

        // add the file creation mode buttons to a horizontal box
        JPanel fileCreationModePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        fileCreationModePanel.add(asyncFileCreationButton);
        fileCreationModePanel.add(oneAtATimeFileCreationButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(fileCreationModePanel, gbc);

        // Add the programming language label and text field
        languageLabel = new JLabel("Enter programming language:");
        languageTextField = new JBTextField();
        languageTextField.setPreferredSize(new Dimension(300, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(languageLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 10);
        mainPanel.add(languageTextField, gbc);

        // Add the file type label and text field
        fileTypeLabel = new JBLabel("Enter file type:");
        fileTypeTextField = new JBTextField();
        fileTypeTextField.setPreferredSize(new Dimension(300, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(fileTypeLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 10);
        mainPanel.add(fileTypeTextField, gbc);

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
        promptContextService.setStatusLabel(hiddenLabel);

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

        // Add the new components to the UI
        // (You can adjust the GridBagConstraints values as needed)
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(new JLabel("Select AI model:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 0, 10);
        mainPanel.add(modelComboBox, gbc);

        // Add the necessary action listeners
        modelComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            // Perform the necessary actions when the model is selected
        });

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(advancedButton, gbc);
        okButton = new JButton("Create Files");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = descriptionTextArea.getText();
                String path;
                if (defaultPathButton.isSelected()) {
                    // Use the default directory
                    path = System.getProperty("user.home") + "/Codactor/Generated-Code";
                } else {
                    // Use the custom directory
                    path = selectedDirectory.getPath();
                }
                // (Implement file creation logic here)
                if (asyncFileCreationButton.isSelected()) {
                    codeFileGeneratorService.generateCodeFiles(description, languageTextField.getText(), fileTypeTextField.getText(), path, promptContextService.getPromptContext());
                } else {
                    codeFileGeneratorService.generateCodeFilesWithConsideration(description, languageTextField.getText(), fileTypeTextField.getText(), path, promptContextService.getPromptContext());
                }
                codactorToolWindowService.openModificationQueueViewerToolWindow();
                dispose();
            }
        });

        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(okButton, gbc);
        // Move all code from the initUI() method into the constructor
        // ...

        // Remove the initUI(); line
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public JToggleButton getAsyncFileCreationButton() {
        return asyncFileCreationButton;
    }

    public JBTextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    public JBTextField getLanguageTextField() {
        return languageTextField;
    }

    public JBTextField getFileTypeTextField() {
        return fileTypeTextField;
    }

    public JRadioButton getDefaultPathButton() {
        return defaultPathButton;
    }

    public VirtualFile getSelectedDirectory() {
        return selectedDirectory;
    }
}
