package com.translator.view.codactor.dialog;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.factory.CodeFileGeneratorServiceFactory;
import com.translator.service.codactor.ide.file.CodeFileGeneratorService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.view.codactor.factory.dialog.PromptContextBuilderDialogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileCreateDialog extends JDialog {
    private Project project;
    private PsiDirectory directory;
    private OpenAiModelService openAiModelService;
    private PromptContextService promptContextService;
    private CodeFileGeneratorService codeFileGeneratorService;
    private PromptContextBuilderDialogFactory promptContextBuilderDialogFactory;
    private JPanel contentPane;
    private JTextField fileNameInput;
    private JTextArea fileDescription;
    private JComboBox<String> modelComboBox;
    private JButton advancedButton;
    private JLabel hiddenLabel;
    private JCheckBox functionsToggleSwitch;
    private JButton okButton;
    private ActionListener okActionListener;

    @Inject
    public FileCreateDialog(Project project,
                            OpenAiModelService openAiModelService,
                            @Assisted PromptContextService promptContextService,
                            CodeFileGeneratorServiceFactory codeFileGeneratorServiceFactory,
                            PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                            @Assisted PsiDirectory directory) {
        this.project = project;
        this.openAiModelService = openAiModelService;
        this.promptContextService = promptContextService;
        this.codeFileGeneratorService = codeFileGeneratorServiceFactory.create(promptContextService);
        this.promptContextBuilderDialogFactory = promptContextBuilderDialogFactory;
        this.directory = directory;
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
        fileDescription.setLineWrap(true);
        functionsToggleSwitch = new JCheckBox("Enable Functions");
        functionsToggleSwitch.setSelected(true);
        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "gpt-4o"});
// Get the index of the selected element
        okActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton okButton = (JButton) e.getSource();
                FileCreateDialog fileCreateDialog = (FileCreateDialog) SwingUtilities.getWindowAncestor(okButton);

                String fileName = fileCreateDialog.getFileNameInput().getText();
                if (!fileName.contains(".") || fileName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(fileCreateDialog, "Please enter a valid file name with an extension.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String fileDescription = fileCreateDialog.getFileDescription().getText();
                    PsiElement createdElement = codeFileGeneratorService.createCodeFile(fileName, fileDescription, directory);
                    if (createdElement != null) {
                        if (createdElement instanceof PsiFile) {
                            FileEditorManager.getInstance(project).openFile(((PsiFile) createdElement).getVirtualFile(), true);
                        }
                        fileCreateDialog.setVisible(false);
                    }
                }
            }
        };
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
            PromptContextBuilderDialog promptContextBuilderDialog = promptContextBuilderDialogFactory.create(promptContextService);
            promptContextBuilderDialog.setVisible(true);
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
        bottomPanel.add(functionsToggleSwitch);
        bottomPanel.add(okButton);
        return bottomPanel;
    }

    public JTextField getFileNameInput() {
        return fileNameInput;
    }

    public JTextArea getFileDescription() {
        return fileDescription;
    }
}