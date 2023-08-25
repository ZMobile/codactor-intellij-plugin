package com.translator.view.codactor.console;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.*;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.translator.model.codactor.file.FileItem;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.file.SelectedFileFetcherService;
import com.translator.service.codactor.inquiry.InquiryService;
import com.translator.service.codactor.modification.AutomaticCodeModificationService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;
import com.translator.view.codactor.dialog.PromptContextBuilderDialog;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import com.translator.view.codactor.factory.dialog.PromptContextBuilderDialogFactory;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import com.translator.view.uml.CodactorUmlBuilderApplicationModel;
import com.translator.view.uml.CodactorUmlBuilderOSXApplication;
import com.translator.view.uml.CodactorUmlBuilderSDIApplication;
import com.translator.view.uml.factory.CodactorUmlBuilderApplicationModelFactory;
import com.translator.view.uml.factory.CodactorUmlBuilderViewFactory;
import org.jetbrains.annotations.NotNull;
import org.jhotdraw.app.Application;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;

public class CodactorConsole extends JBPanel<CodactorConsole> {
    private Project project;
    private JBTextArea textArea;
    private JButton button1;
    private JButton button2;
    private JComboBox<String> modelComboBox;
    private JComboBox<FileItem> fileComboBox;
    private JComboBox<String> modificationTypeComboBox;
    private JLabel jLabel1;
    private JBTextField languageInputTextField;
    private JBLabel jLabel2;
    private JBTextField fileTypeTextField;
    private JButton advancedButton;
    private JLabel hiddenLabel;
    private PromptContextService promptContextService;
    private CodactorToolWindowService codactorToolWindowService;
    private SelectedFileFetcherService selectedFileFetcherService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private AutomaticCodeModificationService automaticCodeModificationService;
    private InquiryService inquiryService;
    private OpenAiModelService openAiModelService;
    private MultiFileCreateDialogFactory multiFileCreateDialogFactory;
    private PromptContextBuilderDialogFactory promptContextBuilderDialogFactory;
    private CodactorUmlBuilderViewFactory codactorUmlBuilderViewFactory;
    private CodactorUmlBuilderApplicationModelFactory codactorUmlBuilderApplicationModelFactory;
    private InquiryViewerFactory inquiryViewerFactory;

    @Inject
    public CodactorConsole(Project project,
                           PromptContextServiceFactory promptContextServiceFactory,
                           CodactorToolWindowService codactorToolWindowService,
                           SelectedFileFetcherService selectedFileFetcherService,
                           CodeSnippetExtractorService codeSnippetExtractorService,
                           InquiryService inquiryService,
                           OpenAiModelService openAiModelService,
                           AutomaticCodeModificationService automaticCodeModificationService,
                           MultiFileCreateDialogFactory multiFileCreateDialogFactory,
                           PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                           CodactorUmlBuilderViewFactory codactorUmlBuilderViewFactory,
                           CodactorUmlBuilderApplicationModelFactory codactorUmlBuilderApplicationModelFactory,
                           InquiryViewerFactory inquiryViewerFactory) {
        super(new BorderLayout());
        this.project = project;
        this.promptContextService = promptContextServiceFactory.create();
        this.codactorToolWindowService = codactorToolWindowService;
        this.selectedFileFetcherService = selectedFileFetcherService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.inquiryService = inquiryService;
        this.openAiModelService = openAiModelService;
        this.automaticCodeModificationService = automaticCodeModificationService;
        this.multiFileCreateDialogFactory = multiFileCreateDialogFactory;
        this.promptContextBuilderDialogFactory = promptContextBuilderDialogFactory;
        this.codactorUmlBuilderViewFactory = codactorUmlBuilderViewFactory;
        this.codactorUmlBuilderApplicationModelFactory = codactorUmlBuilderApplicationModelFactory;
        this.inquiryViewerFactory = inquiryViewerFactory;

        textArea = new JBTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        JBScrollPane scrollPane = new JBScrollPane(textArea);

        button1 = new JButton("Button 1");
        button2 = new JButton();
        button2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));

        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "gpt-4-0314", "gpt-4-32k-0314", "gpt-3.5-turbo-0613", "gpt-4-0613"});
        modelComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                openAiModelService.setSelectedOpenAiModel(model);
            }
        });
        VirtualFile[] selectedFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
        VirtualFile[] openFiles = selectedFileFetcherService.getOpenFiles();
        fileComboBox = new ComboBox<>();
        fileComboBox.setVisible(
                (selectedFiles != null && selectedFiles.length > 1)
                || (openFiles != null && openFiles.length > 1));
        VirtualFile currentlyOpenFile = getSelectedFile();
        if (currentlyOpenFile != null) {
            fileComboBox.addItem(new FileItem(currentlyOpenFile.getPath()));
            if (selectedFiles.length > 1) {
                for (VirtualFile selectedFile : selectedFiles) {
                    if (!selectedFile.equals(currentlyOpenFile)) {
                        fileComboBox.addItem(new FileItem(selectedFile.getPath()));
                    }
                }
            } else if (openFiles.length > 1) {
                for (VirtualFile openFile : openFiles) {
                    if (!openFile.equals(currentlyOpenFile)) {
                        fileComboBox.addItem(new FileItem(openFile.getPath()));
                    }
                }
            }
        }
        modificationTypeComboBox = new ComboBox<>(new String[]{"Modify", "Modify Selected", "Fix", "Fix Selected", "Create", "Create Files", "Inquire", "Inquire Selected", "Translate"});
        jLabel1 = new JLabel();
        advancedButton = new JButton("(Advanced) Add Context");
        advancedButton.addActionListener(e -> {
                promptContextService.setStatusLabel(hiddenLabel);
                PromptContextBuilderDialog promptContextBuilderDialog = promptContextBuilderDialogFactory.create(promptContextService);
                promptContextBuilderDialog.show();
        });
        hiddenLabel = new JLabel();
        hiddenLabel.setVisible(false);

        button1.setText("Modify");
        jLabel1.setText(" Implement the following modification(s) to this code file:");
        /*modelComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                modificationTypeComboBox.removeAllItems();
                for (String option : options) {
                    modificationTypeComboBox.addItem(option);
                }
            }
        });*/

        FileEditorManagerListener fileEditorManagerListener = new FileEditorManagerListener() {
            @Override
            public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull Pair<FileEditor[], FileEditorProvider[]> editors) {
                FileEditorManagerListener.super.fileOpenedSync(source, file, editors);
            }

            @Override
            public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull List<FileEditorWithProvider> editorsWithProviders) {
                FileEditorManagerListener.super.fileOpenedSync(source, file, editorsWithProviders);
            }

            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                FileEditorManagerListener.super.fileOpened(source, file);
                VirtualFile[] selectedFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
                VirtualFile[] openFiles = selectedFileFetcherService.getOpenFiles();
                fileComboBox.setVisible(
                        (selectedFiles != null && selectedFiles.length > 1)
                                || (openFiles != null && openFiles.length > 1));fileComboBox.removeAllItems();
                VirtualFile currentlyOpenFile = getSelectedFile();
                if (currentlyOpenFile != null) {
                    fileComboBox.addItem(new FileItem(currentlyOpenFile.getPath()));
                    if (selectedFiles.length > 1) {
                        for (VirtualFile selectedFile : selectedFiles) {
                            if (!selectedFile.equals(currentlyOpenFile)) {
                                fileComboBox.addItem(new FileItem(selectedFile.getPath()));
                            }
                        }
                    } else if (openFiles.length > 1) {
                        for (VirtualFile openFile : openFiles) {
                            if (!openFile.equals(currentlyOpenFile)) {
                                fileComboBox.addItem(new FileItem(openFile.getPath()));
                            }
                        }
                    }
                }
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                FileEditorManagerListener.super.fileClosed(source, file);
                VirtualFile[] selectedFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
                VirtualFile[] openFiles = selectedFileFetcherService.getOpenFiles();
                fileComboBox.setVisible(
                        (selectedFiles != null && selectedFiles.length > 1)
                                || (openFiles != null && openFiles.length > 1));
                fileComboBox.removeAllItems();
                VirtualFile currentlyOpenFile = getSelectedFile();
                if (currentlyOpenFile != null) {
                    fileComboBox.addItem(new FileItem(currentlyOpenFile.getPath()));
                    if (selectedFiles.length > 1) {
                        for (VirtualFile selectedFile : selectedFiles) {
                            if (!selectedFile.equals(currentlyOpenFile)) {
                                fileComboBox.addItem(new FileItem(selectedFile.getPath()));
                            }
                        }
                    } else if (openFiles.length > 1) {
                        for (VirtualFile openFile : openFiles) {
                            if (!openFile.equals(currentlyOpenFile)) {
                                fileComboBox.addItem(new FileItem(openFile.getPath()));
                            }
                        }
                    }
                }
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                FileEditorManagerListener.super.selectionChanged(event);
                VirtualFile[] selectedFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
                VirtualFile[] openFiles = selectedFileFetcherService.getOpenFiles();
                fileComboBox.setVisible(
                        (selectedFiles != null && selectedFiles.length > 1)
                                || (openFiles != null && openFiles.length > 1));fileComboBox.removeAllItems();
                VirtualFile currentlyOpenFile = getSelectedFile();
                if (currentlyOpenFile != null) {
                    fileComboBox.addItem(new FileItem(currentlyOpenFile.getPath()));
                    if (selectedFiles.length > 1) {
                        for (VirtualFile selectedFile : selectedFiles) {
                            if (!selectedFile.equals(currentlyOpenFile)) {
                                fileComboBox.addItem(new FileItem(selectedFile.getPath()));
                            }
                        }
                    } else if (openFiles.length > 1) {
                        for (VirtualFile openFile : openFiles) {
                            if (!openFile.equals(currentlyOpenFile)) {
                                fileComboBox.addItem(new FileItem(openFile.getPath()));
                            }
                        }
                    }
                }
            }
        };
        MessageBus messageBus = project.getMessageBus();
        MessageBusConnection connection = messageBus.connect();

        // Register the listener
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileEditorManagerListener);


        modificationTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) modificationTypeComboBox.getSelectedItem();
                updateLabelAndButton(selected);
            }
        });

        JPanel buttonsPanel = new JPanel();
        GroupLayout buttonsPanelLayout = new GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
                buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(button2, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(button1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                        ));
        buttonsPanelLayout.setVerticalGroup(
                buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addComponent(button2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(button1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        JPanel topToolbar = new JPanel();
        topToolbar.setLayout(new BorderLayout());
        JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftToolbar.add(modelComboBox);
        leftToolbar.add(fileComboBox);
        leftToolbar.add(modificationTypeComboBox);
        leftToolbar.add(jLabel1);
        languageInputTextField = new JBTextField();
        languageInputTextField.setVisible(false);
        leftToolbar.add(languageInputTextField);
        jLabel2 = new JBLabel(" to file type: ");
        jLabel2.setVisible(false);
        leftToolbar.add(jLabel2);
        fileTypeTextField = new JBTextField();
        fileTypeTextField.setVisible(false);
        leftToolbar.add(fileTypeTextField);
        JPanel rightToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5)); // Set horizontal gap to 0
        rightToolbar.add(hiddenLabel);
        rightToolbar.add(advancedButton);
        JButton testoButton = new JButton("Test");
        testoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Application app;
                        String os = System.getProperty("os.name").toLowerCase();
                        if (os.startsWith("mac")) {
                            app = new CodactorUmlBuilderOSXApplication();
                        } else if (os.startsWith("win")) {
                            //app = new MDIApplication();
                            app = new CodactorUmlBuilderOSXApplication();
                        } else {
                            app = new CodactorUmlBuilderSDIApplication();
                        }

                        CodactorUmlBuilderApplicationModel model = codactorUmlBuilderApplicationModelFactory.create();
                        model.setName("JHotDraw Draw");
                        model.setVersion(getClass().getPackage().getImplementationVersion());
                        model.setCopyright("Copyright 2006-2009 (c) by the authors of JHotDraw and all its contributors.\n" +
                                "This software is licensed under LGPL or Creative Commons 3.0 Attribution.");
                        model.setViewFactory(codactorUmlBuilderViewFactory::create);
                        app.setModel(model);
                        String[] args = new String[0];
                        app.launch(args);
                    }
                });
            }
        });
        //rightToolbar.add(testoButton);

        topToolbar.add(leftToolbar);
        topToolbar.add(rightToolbar, BorderLayout.EAST);

        add(topToolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.EAST);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileItem fileItem = (FileItem) fileComboBox.getSelectedItem();
                assert fileItem != null;
                assert modificationTypeComboBox.getSelectedItem() != null;
                if (modificationTypeComboBox.getSelectedItem().toString().equals("Modify")) {
                    String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
                    if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCode(fileItem.getFilePath(), textArea.getText(), ModificationType.MODIFY, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Modify Selected")) {
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(fileItem.getFilePath());
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    if (code != null && !code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCode(fileItem.getFilePath(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.MODIFY_SELECTION, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix")) {
                    String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
                    if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getFixedCode(fileItem.getFilePath(), textArea.getText(), ModificationType.FIX, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix Selected")) {
                    codactorToolWindowService.openModificationQueueViewerToolWindow();
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(fileItem.getFilePath());
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    if (code != null && !code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getFixedCode(fileItem.getFilePath(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.FIX_SELECTION, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create")) {
                    if (!textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getCreatedCode(fileItem.getFilePath(), textArea.getText(), promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create Files")) {
                    if (!textArea.getText().isEmpty()) {
                        MultiFileCreateDialog multiFileCreateDialog = multiFileCreateDialogFactory.create(null, textArea.getText(), promptContextService, openAiModelService);
                        multiFileCreateDialog.setVisible(true);
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Inquire")) {
                    if (!textArea.getText().isEmpty()) {
                        String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
                        String question = textArea.getText();
                        InquiryViewer inquiryViewer = inquiryViewerFactory.create();
                        inquiryService.createInquiry(inquiryViewer, fileItem.getFilePath(), code, question, promptContextService.getPromptContext(), openAiModelService.getSelectedOpenAiModel());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Inquire Selected")) {
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(fileItem.getFilePath());
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    String question = textArea.getText();
                    InquiryViewer inquiryViewer = inquiryViewerFactory.create();
                    inquiryService.createInquiry(inquiryViewer, fileItem.getFilePath(), code, question, promptContextService.getPromptContext(), openAiModelService.getSelectedOpenAiModel());
                    promptContextService.clearPromptContext();
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Translate")) {
                    codactorToolWindowService.openModificationQueueViewerToolWindow();
                    automaticCodeModificationService.getTranslatedCode(fileItem.getFilePath(), languageInputTextField.getText(), fileTypeTextField.getText(), promptContextService.getPromptContext());
                    promptContextService.clearPromptContext();
                }
            }
        });

        button2.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Robot robot = new Robot();

            textArea.requestFocusInWindow();
            textArea.setText("");

            // Simulate a key press event for the CNTRL key.
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_CONTROL);

            // Simulate another key press event for the CNTRL key.
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }
});
    }

    private void updateLabelAndButton(String selected) {
        if (selected == null) {
            return;
        }
        switch (selected) {
            case "Modify":
                fileComboBox.setVisible(true);
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to this code file:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Fix":
                fileComboBox.setVisible(true);
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this code file:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Create":
                fileComboBox.setVisible(true);
                button1.setText("Create");
                jLabel1.setText(" Create new code from scratch with the following description:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Create Files":
                fileComboBox.setVisible(false);
                button1.setText("Create");
                jLabel1.setText(" (Experimental) Create multiple code files from the following description:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Inquire":
                fileComboBox.setVisible(true);
                button1.setText("Ask");
                jLabel1.setText(" Ask the following question regarding this code file:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Modify Selected":
                fileComboBox.setVisible(true);
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to the selected code:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Fix Selected":
                fileComboBox.setVisible(true);
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this selected code:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Inquire Selected":
                fileComboBox.setVisible(true);
                button1.setText("Ask");
                jLabel1.setText(" Ask the following question regarding this selected code:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Translate":
                fileComboBox.setVisible(true);
                jLabel1.setText(" to language: ");
                button1.setText("Translate");
                languageInputTextField.setVisible(true);
                jLabel2.setVisible(true);
                fileTypeTextField.setVisible(true);
                textArea.setVisible(false);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + selected);
        }
    }

    private VirtualFile getSelectedFile() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        Editor editor = fileEditorManager.getSelectedTextEditor();
        if (editor != null) {
            Document document = editor.getDocument();
            return FileDocumentManager.getInstance().getFile(document);
        }
        return null;
    }

    public void updateModelComboBox() {

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
    }

    public void updateModificationTypeComboBox(String selected) {
        modificationTypeComboBox.setSelectedItem(selected);
        int selectedIndex = -1;
        for (int i = 0; i < modificationTypeComboBox.getItemCount(); i++) {
            if (selected.equals(modificationTypeComboBox.getItemAt(i))) {
                selectedIndex = i;
                break;
            }
        }

        // Check if the selected element is in the combo box
        if (selectedIndex != -1 && selectedIndex != 0) {
            // Store the element at position 0
            String elementAtZero = modificationTypeComboBox.getItemAt(0);

            // Remove the selected element from the combo box
            modificationTypeComboBox.removeItemAt(selectedIndex);

            // Insert the selected element at the first position
            modificationTypeComboBox.insertItemAt(selected, 0);

            // Remove the element at position 1 (which was previously at position 0)
            modificationTypeComboBox.removeItemAt(1);

            // Insert the element that was previously at position 0 to the original position of the selected element
            modificationTypeComboBox.insertItemAt(elementAtZero, selectedIndex);

            // Set the selected index to 0
            modificationTypeComboBox.setSelectedIndex(0);
        }
        updateLabelAndButton(selected);
    }
}