package com.translator.view.dialog;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;
import com.translator.service.file.CodeFileGeneratorService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class MultiFileCreateDialog extends DialogWrapper {
    // (Add other instance variables here as needed)

    private JPanel mainPanel;
    private JBTextArea descriptionTextArea;
    private List<HistoricalContextObjectHolder> priorContext;
    private Inquiry inquiry;
    private InquiryChat inquiryChat;
    private CodeFileGeneratorService codeFileGeneratorService;
    private CodactorToolWindowService codactorToolWindowService;
    private JRadioButton defaultPathButton, customPathButton;
    private JToggleButton asyncFileCreationButton, oneAtATimeFileCreationButton;
    private JLabel customPathLabel, languageLabel, fileTypeLabel;
    private JFileChooser fileChooser;
    private ButtonGroup toggleGroup, fileCreationModeGroup;
    private JBTextField languageTextField, fileTypeTextField;
    private VirtualFile selectedDirectory = null;
    // (Add the other constructor here)

    public MultiFileCreateDialog(List<HistoricalContextObjectHolder> priorContext) {
        super(true);
        this.priorContext = priorContext;
        initUI();
        init();
        setTitle("(Experimental) Multi-File Code Generator");
    }


    private void initUI() {
        setTitle("(Experimental) Multi-File Code Generator");
        setSize(600, 300);

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10);
        JLabel descriptionLabel = new JLabel("Enter a description for the files to be created:");
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
        defaultPathButton.setSelected(true);
        customPathButton = new JRadioButton("Specify a directory");
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

        toggleGroup = new ButtonGroup();
        toggleGroup.add(defaultPathButton);
        toggleGroup.add(customPathButton);

        // add the radio buttons to a horizontal box
        JPanel radioPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        radioPanel.add(defaultPathButton);
        radioPanel.add(customPathButton);
        mainPanel.add(radioPanel, gbc);

        // create the file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // add the custom path label
        customPathLabel = new JLabel("");
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
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
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
