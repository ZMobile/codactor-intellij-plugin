package com.translator.view.codactor.dialog;

import com.intellij.psi.PsiDirectory;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class FileCreateWithUnitTestsDialog extends JDialog {
    private PsiDirectory directory;
    private JTextArea codeDescription;
    private JButton regenerateAllButton;
    private JButton regenerateInterfaceButton;
    private JTextArea documentedInterface;
    private JButton regenerateTestsButton;
    private JCheckBox regenerateDescriptionsCheckBox;
    private JPanel unitTestsPanel;
    private JPanel contentPane;

    @Inject
    public FileCreateWithUnitTestsDialog(PsiDirectory directory) {
        setModal(true);
        initUIComponents();
        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.directory = directory;
    }

    private void initUIComponents() {
        contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Code description label and text area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPane.add(new JLabel("Code Description:"), gbc);

        gbc.gridy++;
        codeDescription = new JTextArea(3, 50);
        JScrollPane codeDescriptionScroll = new JScrollPane(codeDescription);
        contentPane.add(codeDescriptionScroll, gbc);

        // Regenerate all button
        gbc.gridy++;
        gbc.gridwidth = 1;
        regenerateAllButton = new JButton("Regenerate All");
        contentPane.add(regenerateAllButton, gbc);

        // Regenerate interface button
        gbc.gridy++;
        regenerateInterfaceButton = new JButton("Regenerate Interface");
        contentPane.add(regenerateInterfaceButton, gbc);

        // Documented interface label and editor
        gbc.gridy++;
        gbc.gridwidth = 2;
        contentPane.add(new JLabel("Documented Interface:"), gbc);

        gbc.gridy++;
        documentedInterface = new JTextArea(8, 50);
        JScrollPane documentedInterfaceScroll = new JScrollPane(documentedInterface);
        contentPane.add(documentedInterfaceScroll, gbc);

        // Regenerate tests button and checkbox
        gbc.gridy++;
        gbc.gridwidth = 1;
        regenerateTestsButton = new JButton("Regenerate Tests");
        contentPane.add(regenerateTestsButton, gbc);

        gbc.gridx = 1;
        regenerateDescriptionsCheckBox = new JCheckBox("Regenerate Test Descriptions");
        contentPane.add(regenerateDescriptionsCheckBox, gbc);

        // Unit tests label
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        contentPane.add(new JLabel("Unit Tests:"), gbc);

        // Unit test panel
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        unitTestsPanel = new JPanel(new GridBagLayout());
        JScrollPane unitTestsScroll = new JScrollPane(unitTestsPanel);
        unitTestsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(unitTestsScroll, gbc);

        populateUnitTestsPanel();

        setContentPane(contentPane);
    }

    private void populateUnitTestsPanel() {
        for (int i = 0; i < 3; i++) { // Example: 3 unit tests
            GridBagConstraints testDescriptionGbc = new GridBagConstraints();
            testDescriptionGbc.insets = new Insets(5, 5, 5, 5);
            testDescriptionGbc.fill = GridBagConstraints.HORIZONTAL;
            testDescriptionGbc.weightx = 1;
            testDescriptionGbc.gridx = 0;
            testDescriptionGbc.gridy = i * 3;

            // Unit test description
            JTextArea unitTestDescription = new JTextArea(2, 40);
            JScrollPane unitTestDescriptionScroll = new JScrollPane(unitTestDescription);
            unitTestsPanel.add(unitTestDescriptionScroll, testDescriptionGbc);

            // Regenerate button
            GridBagConstraints regenerateButtonGbc = new GridBagConstraints();
            regenerateButtonGbc.insets = new Insets(5, 5, 5, 5);
            regenerateButtonGbc.gridx = 1;
            regenerateButtonGbc.gridy = i * 3;
            JButton regenerateButton = new JButton("Regenerate Test");
            unitTestsPanel.add(regenerateButton, regenerateButtonGbc);

            // Unit test editor
            GridBagConstraints testEditorGbc = new GridBagConstraints();
            testEditorGbc.insets = new Insets(5, 5, 5, 5);
            testEditorGbc.fill = GridBagConstraints.HORIZONTAL;
            testEditorGbc.weightx = 1;
            testEditorGbc.gridwidth = 2;
            testEditorGbc.gridx = 0;
            testEditorGbc.gridy = i * 3 + 1;
            JTextArea unitTestEditor = new JTextArea(5, 50);
            JScrollPane unitTestEditorScroll = new JScrollPane(unitTestEditor);
            unitTestsPanel.add(unitTestEditorScroll, testEditorGbc);
        }
    }
}
