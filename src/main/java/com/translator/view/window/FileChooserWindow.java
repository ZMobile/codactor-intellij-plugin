package com.translator.view.window;

import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;
import com.translator.service.file.CodeFileGeneratorService;
import com.translator.service.ui.tool.CodactorToolWindowService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class FileChooserWindow extends JFrame implements ActionListener {
    private String description;
    private List<HistoricalContextObjectHolder> priorContext;
    private Inquiry inquiry;
    private InquiryChat inquiryChat;
    private CodeFileGeneratorService codeFileGeneratorService;
    private CodactorToolWindowService codactorToolWindowService;
    private JRadioButton defaultPathButton, customPathButton;
    private JToggleButton asyncFileCreationButton, oneAtATimeFileCreationButton;
    private JButton generateButton, cancelButton;
    private JLabel customPathLabel, languageLabel, fileTypeLabel;
    private JFileChooser fileChooser;
    private ButtonGroup toggleGroup, fileCreationModeGroup;
    private JTextField languageTextField, fileTypeTextField;

    public FileChooserWindow(Inquiry inquiry,
                             InquiryChat inquiryChat,
                             CodeFileGeneratorService codeFileGeneratorService,
                             CodactorToolWindowService codactorToolWindowService) {
        this.description = null;
        this.priorContext = null;
        this.inquiry = inquiry;
        this.inquiryChat = inquiryChat;
        this.codeFileGeneratorService = codeFileGeneratorService;
        this.codactorToolWindowService = codactorToolWindowService;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    public FileChooserWindow(String description,
                             List<HistoricalContextObjectHolder> priorContext,
                             CodeFileGeneratorService codeFileGeneratorService,
                             CodactorToolWindowService codactorToolWindowService) {
        this.description = description;
        this.priorContext = priorContext;
        this.inquiry = null;
        this.inquiryChat = null;
        this.codeFileGeneratorService = codeFileGeneratorService;
        this.codactorToolWindowService = codactorToolWindowService;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setTitle("(Experimental) Multi-File Code Generator");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10);

        // create the default and custom path buttons
        defaultPathButton = new JRadioButton("~/Codactor/Generated-Code");
        defaultPathButton.setSelected(true);
        customPathButton = new JRadioButton("Specify a directory");
        customPathButton.addActionListener(this);

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
        gbc.gridy = 1;
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

        // add the file creation mode buttons to a horizontal box
        JPanel fileCreationModePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        fileCreationModePanel.add(asyncFileCreationButton);
        fileCreationModePanel.add(oneAtATimeFileCreationButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(fileCreationModePanel, gbc);

        // Add the programming language label and text field
        languageLabel = new JLabel("Enter programming language:");
        languageTextField = new JTextField();
        languageTextField.setPreferredSize(new Dimension(300, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(languageLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 10);
        mainPanel.add(languageTextField, gbc);

        // Add the file type label and text field
        fileTypeLabel = new JLabel("Enter file type:");
        fileTypeTextField = new JTextField();
        fileTypeTextField.setPreferredSize(new Dimension(300, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(fileTypeLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 10);
        mainPanel.add(fileTypeTextField, gbc);

        // add the generate and cancel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        generateButton = new JButton("Generate");
        generateButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 0, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == customPathButton) {
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = fileChooser.getSelectedFile();
                customPathLabel.setText("Selected directory: " + selectedDirectory.getAbsolutePath());
                customPathLabel.setVisible(true);
                customPathLabel.setPreferredSize(new Dimension(500, 20));
            } else {
                toggleGroup.setSelected(defaultPathButton.getModel(), true);
            }
        } else if (e.getSource() == generateButton) {
            String path;
            if (defaultPathButton.isSelected()) {
                // Use the default directory
                path = System.getProperty("user.home") + "/Codactor/Generated-Code";
            } else {
                // Use the custom directory
                path = fileChooser.getSelectedFile().getAbsolutePath();
            }
            codactorToolWindowService.openModificationQueueViewerToolWindow();
            if (inquiry == null) {
                if (asyncFileCreationButton.isSelected()) {
                    codeFileGeneratorService.generateCodeFiles(description, languageTextField.getText(), fileTypeTextField.getText(), path, priorContext);
                } else {
                    codeFileGeneratorService.generateCodeFilesWithConsideration(description, languageTextField.getText(), fileTypeTextField.getText(), path, priorContext);
                }
            } else {
                if (asyncFileCreationButton.isSelected()) {
                    codeFileGeneratorService.generateCodeFiles(inquiry, inquiryChat, languageTextField.getText(), fileTypeTextField.getText(), path);
                } else {
                    codeFileGeneratorService.generateCodeFilesWithConsideration(inquiry, inquiryChat, languageTextField.getText(), fileTypeTextField.getText(), path);
                }
            }
            dispose();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }
}