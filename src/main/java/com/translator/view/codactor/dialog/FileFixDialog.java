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
import com.intellij.ui.components.JBTextArea;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.ai.modification.multi.MassAiCodeModificationService;
import com.translator.service.codactor.ai.modification.multi.MultiFileAiCodeModificationService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.factory.dialog.PromptContextBuilderDialogFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileFixDialog extends JDialog {
    private Project project;
    private CodactorToolWindowService codactorToolWindowService;
    private PromptContextService promptContextService;
    private PromptContextBuilderDialogFactory promptContextBuilderDialogFactory;
    private MassAiCodeModificationService massAiCodeModificationService;
    private MultiFileAiCodeModificationService multiFileAiCodeModificationService;
    private OpenAiModelService openAiModelService;
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JBTextArea description;
    private ComboBox<String> modelComboBox;
    private JButton advancedButton;
    private JBLabel hiddenLabel;
    private JBLabel descriptionLabel;
    private JButton addButton;
    private JButton removeButton;
    private JButton okButton;
    private JToggleButton applyToEachFileButton;
    private JToggleButton smartFixRequestButton;

    @Inject
    public FileFixDialog(Project project,
                         CodactorToolWindowService codactorToolWindowService,
                         @Assisted PromptContextService promptContextService,
                         PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                         MassAiCodeModificationService massAiCodeModificationService,
                         MultiFileAiCodeModificationService multiFileAiCodeModificationService,
                         OpenAiModelService openAiModelService,
                         @Assisted List<VirtualFile> selectedItems) {
        this.project = project;
        this.codactorToolWindowService = codactorToolWindowService;
        this.promptContextService = promptContextService;
        this.promptContextBuilderDialogFactory = promptContextBuilderDialogFactory;
        this.massAiCodeModificationService = massAiCodeModificationService;
        this.multiFileAiCodeModificationService = multiFileAiCodeModificationService;
        this.openAiModelService = openAiModelService;
        setLayout(new BorderLayout());
        setTitle("Fix Code");

        listModel = new DefaultListModel<>();

        initFileList(selectedItems);

        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileList.addListSelectionListener(e -> updateSelectedFilesLabel());

        descriptionLabel = new JBLabel("Enter the problem to fix with each of these files:");
        description = new JBTextArea(5, 20);

        hiddenLabel = new JBLabel();
        hiddenLabel.setVisible(false);

        // Instantiate centerPanel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(descriptionLabel, BorderLayout.NORTH);
        centerPanel.add(description, BorderLayout.CENTER);

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

        applyToEachFileButton = new JToggleButton("Apply below fix to each file");
        applyToEachFileButton.setSelected(true);
        applyToEachFileButton.setEnabled(false);
        applyToEachFileButton.addActionListener(e -> {
            applyToEachFileButton.setEnabled(!applyToEachFileButton.isSelected());
            if (applyToEachFileButton.isSelected()) {
                smartFixRequestButton.setSelected(false);
                smartFixRequestButton.setEnabled(true);
            }
            updateSelectedFilesLabel();
        });

        smartFixRequestButton = new JToggleButton("(Experimental) Smart fix request");
        smartFixRequestButton.setSelected(false);
        smartFixRequestButton.setEnabled(true);
        smartFixRequestButton.addActionListener(e -> {
            smartFixRequestButton.setEnabled(!smartFixRequestButton.isSelected());
            if (smartFixRequestButton.isSelected()) {
                applyToEachFileButton.setSelected(false);
                applyToEachFileButton.setEnabled(true);
            }
            updateSelectedFilesLabel();
        });

// Create a panel for the toggle buttons
        // Create a panel for the toggle buttons
        JPanel toggleButtonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints toggleButtonsConstraints = new GridBagConstraints();
        toggleButtonsConstraints.gridx = 0;
        toggleButtonsConstraints.gridy = 0;
        toggleButtonsConstraints.insets = new Insets(5, 0, 5, 5); // Top, left, bottom, right padding
        toggleButtonsPanel.add(applyToEachFileButton, toggleButtonsConstraints);

        toggleButtonsConstraints.gridx = 1;
        toggleButtonsPanel.add(smartFixRequestButton, toggleButtonsConstraints);

// Modify the northPanel layout
        northPanel.add(toggleButtonsPanel, BorderLayout.SOUTH);

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

        okButton = new JButton("Fix");
        okButton.setBorder(emptyBorder);
        okButton.addActionListener(e -> fixFiles());


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
        if (applyToEachFileButton.isSelected()) {
            if (fileList.getModel().getSize() == 1) {
                descriptionLabel.setText("Enter the problem to fix with this file:");
            } else if (fileList.getModel().getSize() == 0) {
                descriptionLabel.setText("Enter the problem to fix with each file added to this list:");
            } else {
                descriptionLabel.setText("Enter the problem to fix with each of these files:");
            }
        } else {
            descriptionLabel.setText("Enter the problem to fix within the files in this list:");
        }
    }

    private void fixFiles() {
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
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(FileFixDialog.this, "Please select at least one file.", "Error", JOptionPane.ERROR_MESSAGE));
                    return null;
                }
                if (applyToEachFileButton.isSelected()) {
                    massAiCodeModificationService.getFixedCode(selectedFiles, description.getText(), promptContextService.getPromptContext());
                    promptContextService.clearPromptContext();
                } else {
                    try {
                        multiFileAiCodeModificationService.fixCodeFiles(selectedFiles, description.getText(), promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


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

