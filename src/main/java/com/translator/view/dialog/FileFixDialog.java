package com.translator.view.dialog;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.service.context.PromptContextService;
import com.translator.service.factory.AutomaticMassCodeModificationServiceFactory;
import com.translator.service.modification.AutomaticMassCodeModificationService;
import com.translator.service.modification.multi.MultiFileModificationService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.factory.PromptContextBuilderFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileFixDialog extends JDialog {
    private Project project;
    private CodactorToolWindowService codactorToolWindowService;
    private PromptContextService promptContextService;
    private PromptContextBuilderFactory promptContextBuilderFactory;
    private AutomaticMassCodeModificationService automaticMassCodeModificationService;
    private MultiFileModificationService multiFileModificationService;
    private OpenAiModelService openAiModelService;
    private JBList<String> fileList;
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

    public FileFixDialog(Project project,
                         CodactorToolWindowService codactorToolWindowService,
                         PromptContextService promptContextService,
                         PromptContextBuilderFactory promptContextBuilderFactory,
                         AutomaticMassCodeModificationServiceFactory automaticMassCodeModificationServiceFactory,
                         MultiFileModificationService multiFileModificationService,
                         OpenAiModelService openAiModelService,
                         List<VirtualFile> selectedItems) {
        this.project = project;
        this.codactorToolWindowService = codactorToolWindowService;
        this.promptContextService = promptContextService;
        this.promptContextBuilderFactory = promptContextBuilderFactory;
        this.automaticMassCodeModificationService = automaticMassCodeModificationServiceFactory.create(promptContextService);
        this.multiFileModificationService = multiFileModificationService;
        this.openAiModelService = openAiModelService;
        setLayout(new BorderLayout());
        setTitle("Fix Code");

        listModel = new DefaultListModel<>();

        initFileList(selectedItems);

        fileList = new JBList<>(listModel);
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
        addButton.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, true);
            VirtualFile[] filesToAdd = FileChooser.chooseFiles(descriptor, project, null);
            Arrays.stream(filesToAdd).forEach(this::addFile);
        });

        // Add the Remove button to the removePanel
        removeButton = new JButton("Remove");
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

        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-4", "gpt-4-32k", "gpt-4-0314", "gpt-4-32k-0314"});
        modelComboBox.addActionListener(e -> {
            ComboBox<String> cb = (ComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                openAiModelService.setSelectedOpenAiModel(model);
            }
        });

        advancedButton = new JButton("(Advanced) Add Context");
        advancedButton.addActionListener(e -> {
            promptContextService.setStatusLabel(hiddenLabel);
            PromptContextBuilder promptContextBuilder = promptContextBuilderFactory.create(promptContextService);
            promptContextBuilder.setVisible(true);
        });

        okButton = new JButton("Fix");
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
            if (fileList.getItemsCount() == 1) {
                descriptionLabel.setText("Enter the problem to fix with this file:");
            } else if (fileList.getItemsCount() == 0) {
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
                    automaticMassCodeModificationService.getFixedCode(selectedFiles, description.getText());
                } else {
                    try {
                        multiFileModificationService.fixCodeFiles(selectedFiles, description.getText(), promptContextService.getPromptContext());
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

