package com.translator.view.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;

public class FileCreateDialog extends DialogWrapper {
    private JPanel contentPane;
    private JBTextField fileNameInput;
    private JBTextArea fileDescription;

    public FileCreateDialog() {
        super(true);
        initUIComponents();
        init();
    }

    private void initUIComponents() {
        contentPane = new JPanel(new BorderLayout());
        fileNameInput = new JBTextField();
        fileDescription = new JBTextArea();


        // Add components to content pane
        contentPane.add(createTopPanel(), BorderLayout.NORTH);
        contentPane.add(createMainPanel(), BorderLayout.CENTER);

        // Initialize dialog settings
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

    @Override
    protected void doOKAction() {
        // Input validation
        if (!fileNameInput.getText().contains(".") || fileNameInput.getText().trim().isEmpty()) {
            setErrorText("Please enter a valid file name with an extension.");
            return;
        }

        // Custom actions
        // e.g., create a file with the given name and description

        // Close the dialog
        close(DialogWrapper.OK_EXIT_CODE);
    }

    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    public JBTextField getFileNameInput() {
        return fileNameInput;
    }

    public JBTextArea getFileDescription() {
        return fileDescription;
    }
}
