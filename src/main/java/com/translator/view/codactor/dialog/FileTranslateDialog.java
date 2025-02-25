package com.translator.view.codactor.dialog;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.ai.modification.multi.MassAiCodeModificationService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.factory.dialog.PromptContextBuilderDialogFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileTranslateDialog extends JDialog {
    private Project project;
    private CodactorToolWindowService codactorToolWindowService;
    private PromptContextService promptContextService;
    private PromptContextBuilderDialogFactory promptContextBuilderDialogFactory;
    private MassAiCodeModificationService massAiCodeModificationService;
    private OpenAiModelService openAiModelService;
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JBTextField newLanguageField;
    private JBTextField newFileTypeField;
    private JBLabel languageLabel;
    private JBLabel fileTypeLabel;
    private ComboBox<String> modelComboBox;
    private JButton advancedButton;
    private JBLabel hiddenLabel;
    private JBLabel descriptionLabel;
    private JButton addButton;
    private JButton removeButton;
    private JButton okButton;

    @Inject
    public FileTranslateDialog(Project project,
                               CodactorToolWindowService codactorToolWindowService,
                               @Assisted PromptContextService promptContextService,
                               PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                               MassAiCodeModificationService massAiCodeModificationService,
                               OpenAiModelService openAiModelService,
                               @Assisted List<VirtualFile> selectedItems) {
        this.project = project;
        this.codactorToolWindowService = codactorToolWindowService;
        this.promptContextService = promptContextService;
        this.promptContextBuilderDialogFactory = promptContextBuilderDialogFactory;
        this.massAiCodeModificationService = massAiCodeModificationService;
        this.openAiModelService = openAiModelService;
        setLayout(new BorderLayout());
        setTitle("Translate Code");

        listModel = new DefaultListModel<>();

        initFileList(selectedItems);

        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileList.addListSelectionListener(e -> updateSelectedFilesLabel());

        descriptionLabel = new JBLabel("Enter the new programming language and file type for each of these files:");
        languageLabel = new JBLabel("New Programming Language:");
        fileTypeLabel = new JBLabel("New File Type:");
        newLanguageField = new JBTextField(10);
        newFileTypeField = new JBTextField(10);

        hiddenLabel = new JBLabel();
        hiddenLabel.setVisible(false);

        // Instantiate centerPanel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(descriptionLabel, BorderLayout.NORTH);

        // Create a JPanel for the two JTextFields and their labels
        JPanel textFieldsPanel = new JPanel(new GridLayout(2, 2));
        textFieldsPanel.add(languageLabel);
        textFieldsPanel.add(newLanguageField);
        textFieldsPanel.add(fileTypeLabel);
        textFieldsPanel.add(newFileTypeField);
        centerPanel.add(textFieldsPanel, BorderLayout.CENTER);

        addButton = new JButton("Add");
        Border emptyBorder = BorderFactory.createEmptyBorder();
        addButton.setBorder(emptyBorder);
        addButton.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, true);
            VirtualFile[] filesToAdd = FileChooser.chooseFiles(descriptor, project, null);
            Arrays.stream(filesToAdd).forEach(this::addFile);
        });

        // Add the Remove button to the removePanel
        removeButton = new JButton("Remove");
        removeButton.setBorder(emptyBorder);
        removeButton.addActionListener(e -> removeSelectedFiles());
        JPanel northPanel = new JPanel(new BorderLayout());

        int maxWidth = Math.max(addButton.getPreferredSize().width, removeButton.getPreferredSize().width);
        Dimension buttonSize = new Dimension(maxWidth, addButton.getPreferredSize().height);
        addButton.setPreferredSize(buttonSize);
        removeButton.setPreferredSize(buttonSize);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(addButton, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        leftPanel.add(removeButton, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 1; // Add this line
        leftPanel.add(new JPanel(), gridBagConstraints); // Add this line

        northPanel.add(new JBScrollPane(fileList), BorderLayout.CENTER);
        northPanel.add(leftPanel, BorderLayout.WEST);

        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "gpt-4o"});
        modelComboBox.addActionListener(e -> {
            ComboBox<String> cb = (ComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                openAiModelService.setSelectedOpenAiModel(model);
            }
        });

        advancedButton = new JButton("(Advanced) Add Context");
        advancedButton.setBorder(emptyBorder);
        advancedButton.addActionListener(e -> {
            promptContextService.setStatusLabel(hiddenLabel);
            PromptContextBuilderDialog promptContextBuilderDialog = promptContextBuilderDialogFactory.create(promptContextService);
            promptContextBuilderDialog.setVisible(true);
        });

        okButton = new JButton("Modify");
        okButton.setBorder(emptyBorder);
        okButton.addActionListener(e -> translateFiles());


        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        pack();
    }

    private void initFileList(List<VirtualFile> selectedItems) {
        for (VirtualFile item : selectedItems) {
            if (!item.isDirectory()) {
                listModel.addElement(item.getPath());
            }
        }
    }

    private void addFile(VirtualFile file) {
        if (!file.isDirectory()) {
            listModel.addElement(file.getPath());
        }
    }


    private void removeSelectedFiles() {
        List<String> selectedFiles = fileList.getSelectedValuesList();
        for (String file : selectedFiles) {
            listModel.removeElement(file);
        }
    }


    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4));
        bottomPanel.add(modelComboBox);
        bottomPanel.add(advancedButton);
        bottomPanel.add(hiddenLabel);
        bottomPanel.add(okButton);
        return bottomPanel;
    }

    private void updateSelectedFilesLabel() {
        if (fileList.getSelectedValuesList().size() == 1) {
            descriptionLabel.setText("Enter the new programming language and file type for this file:");
        } else if (fileList.getSelectedValuesList().isEmpty()) {
            descriptionLabel.setText("Enter the new programming language and file type for each file added to this list:");
        } else {
            descriptionLabel.setText("Enter the new programming language and file type for each of these files:");
        }
    }

    private void translateFiles() {
        codactorToolWindowService.openModificationQueueViewerToolWindow();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                List<String> selectedFiles = new ArrayList<>();
                for (int i = 0; i < listModel.size(); i++) {
                    String filePath = listModel.get(i);
                    selectedFiles.add(filePath);
                }
                if (selectedFiles.isEmpty()) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(FileTranslateDialog.this, "Please select at least one file.", "Error", JOptionPane.ERROR_MESSAGE));
                    return null;
                }
                massAiCodeModificationService.getTranslatedCode(selectedFiles, newLanguageField.getText(), newFileTypeField.getText(), promptContextService.getPromptContext());
                promptContextService.clearPromptContext();

                if (selectedFiles.size() == 1) {
                    // Open the modified file in the editor
                    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(selectedFiles.get(0));
                    if (file != null) {
                        SwingUtilities.invokeLater(() -> FileEditorManager.getInstance(project).openFile(file, true));
                    }
                }

                return null;
            }

            @Override
            protected void done() {
            }
        };

        worker.execute();
        dispose();
    }
}