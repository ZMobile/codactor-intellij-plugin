package com.translator.view.console;

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
import com.translator.PromptContextBuilder;
import com.translator.model.file.FileItem;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.modification.ModificationType;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.context.PromptContextService;
import com.translator.service.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.file.CodeFileGeneratorService;
import com.translator.service.file.SelectedFileFetcherService;
import com.translator.service.inquiry.InquiryService;
import com.translator.service.modification.AutomaticCodeModificationService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.dialog.MultiFileCreateDialog;
import com.translator.view.factory.PromptContextBuilderFactory;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
    JBTextField languageInputTextField;
    JBLabel jLabel2;
    JBTextField fileTypeTextField;

    private JButton advancedButton;
    private JLabel hiddenLabel;
    private PromptContextService promptContextService;
    private CodactorToolWindowService codactorToolWindowService;
    private SelectedFileFetcherService selectedFileFetcherService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private AutomaticCodeModificationService automaticCodeModificationService;
    private InquiryService inquiryService;
    private CodeFileGeneratorService codeFileGeneratorService;
    private OpenAiModelService openAiModelService;
    private PromptContextBuilderFactory promptContextBuilderFactory;

    @Inject
    public CodactorConsole(Project project,
                           PromptContextService promptContextService,
                           CodactorToolWindowService codactorToolWindowService,
                           SelectedFileFetcherService selectedFileFetcherService,
                           CodeSnippetExtractorService codeSnippetExtractorService,
                           InquiryService inquiryService,
                           CodeFileGeneratorService codeFileGeneratorService,
                           OpenAiModelService openAiModelService,
                           AutomaticCodeModificationServiceFactory automaticCodeModificationServiceFactory,
                           PromptContextBuilderFactory promptContextBuilderFactory) {
        super(new BorderLayout());
        this.project = project;
        this.promptContextService = promptContextService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.selectedFileFetcherService = selectedFileFetcherService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.inquiryService = inquiryService;
        this.codeFileGeneratorService = codeFileGeneratorService;
        this.openAiModelService = openAiModelService;
        this.automaticCodeModificationService = automaticCodeModificationServiceFactory.create(promptContextService);
        this.promptContextBuilderFactory = promptContextBuilderFactory;
        textArea = new JBTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        JBScrollPane scrollPane = new JBScrollPane(textArea);

        button1 = new JButton("Button 1");
        button2 = new JButton();
        button2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));

        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-4", "gpt-4-32k", "gpt-4-0314", "gpt-4-32k-0314"});
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
                PromptContextBuilder promptContextBuilder = promptContextBuilderFactory.create(promptContextService);
                promptContextBuilder.show();
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
                        automaticCodeModificationService.getModifiedCode(fileItem.getFilePath(), 0, code.length(), textArea.getText(), ModificationType.MODIFY);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Modify Selected")) {
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(fileItem.getFilePath());
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    if (code != null && !code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCode(fileItem.getFilePath(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.MODIFY);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix")) {
                    String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
                    if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getFixedCode(fileItem.getFilePath(), 0, code.length(), textArea.getText(), ModificationType.FIX);
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
                        automaticCodeModificationService.getFixedCode(fileItem.getFilePath(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.FIX);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create")) {
                    if (!textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getCreatedCode(fileItem.getFilePath(), textArea.getText());
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create Files")) {
                    if (!textArea.getText().isEmpty()) {
                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                        List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                        if (priorContextData != null) {
                            for (HistoricalContextObjectDataHolder data : priorContextData) {
                                priorContext.add(new HistoricalContextObjectHolder(data));
                            }
                        }
                        MultiFileCreateDialog multiFileCreateDialog = new MultiFileCreateDialog(null, textArea.getText(), openAiModelService, codactorToolWindowService, codeFileGeneratorService, promptContextService, promptContextBuilderFactory);
                        multiFileCreateDialog.setVisible(true);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Inquire")) {
                    if (!textArea.getText().isEmpty()) {
                        String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
                        String question = textArea.getText();
                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                        List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                        if (priorContextData != null) {
                            for (HistoricalContextObjectDataHolder data : priorContextData) {
                                priorContext.add(new HistoricalContextObjectHolder(data));
                            }
                        }
                        inquiryService.createInquiry(fileItem.getFilePath(), code, question, priorContext);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Inquire Selected")) {
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(fileItem.getFilePath());
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    String question = textArea.getText();
                    List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                    List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                    if (priorContextData != null) {
                        for (HistoricalContextObjectDataHolder data : priorContextData) {
                            priorContext.add(new HistoricalContextObjectHolder(data));
                        }
                    }
                    inquiryService.createInquiry(fileItem.getFilePath(), code, question, priorContext);
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Translate")) {
                    codactorToolWindowService.openModificationQueueViewerToolWindow();
                    automaticCodeModificationService.getTranslatedCode(fileItem.getFilePath(), languageInputTextField.getText(), fileTypeTextField.getText());
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
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to this code file:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Fix":
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this code file:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Create":
                button1.setText("Create");
                jLabel1.setText(" Create new code from scratch with the following description:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Create Files":
                button1.setText("Create");
                jLabel1.setText(" (Experimental) Create multiple code files from the following description:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Inquire":
                button1.setText("Ask");
                jLabel1.setText(" Ask the following question regarding this code file:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Modify Selected":
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to the selected code:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Fix Selected":
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this selected code:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Inquire Selected":
                button1.setText("Ask");
                jLabel1.setText(" Ask the following question regarding this selected code:");
                languageInputTextField.setVisible(false);
                jLabel2.setVisible(false);
                fileTypeTextField.setVisible(false);
                textArea.setVisible(true);
                break;
            case "Translate":
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
}