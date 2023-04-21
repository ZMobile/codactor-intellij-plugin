package com.translator.view.console;

import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.translator.PromptContextBuilder;
import com.translator.ProvisionalModificationCustomizer;
import com.translator.model.file.FileItem;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.modification.ModificationType;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.context.PromptContextService;
import com.translator.service.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.file.SelectedFileFetcherService;
import com.translator.service.inquiry.InquiryService;
import com.translator.service.modification.AutomaticCodeModificationService;
import com.translator.service.ui.tool.CodactorToolWindowService;
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
import java.util.Objects;
import java.util.List;

import static org.mozilla.javascript.ScriptRuntime.add;

public class CodactorConsole extends JBPanel<CodactorConsole> {
    private Project project;
    private JBTextArea textArea;
    private JButton button1;
    private JButton button2;
    private JComboBox<String> modelComboBox;
    private JComboBox<FileItem> fileComboBox;
    private JComboBox<String> modificationTypeComboBox;
    private JLabel jLabel1;
    private JButton advancedButton;
    private JLabel hiddenLabel;
    private PromptContextService promptContextService;
    private CodactorToolWindowService codactorToolWindowService;
    private SelectedFileFetcherService selectedFileFetcherService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private AutomaticCodeModificationService automaticCodeModificationService;
    private InquiryService inquiryService;
    private PromptContextBuilderFactory promptContextBuilderFactory;

    @Inject
    public CodactorConsole(Project project,
                           PromptContextService promptContextService,
                           CodactorToolWindowService codactorToolWindowService,
                           SelectedFileFetcherService selectedFileFetcherService,
                           CodeSnippetExtractorService codeSnippetExtractorService,
                           InquiryService inquiryService,
                           AutomaticCodeModificationServiceFactory automaticCodeModificationServiceFactory,
                           PromptContextBuilderFactory promptContextBuilderFactory) {
        super(new BorderLayout());
        this.project = project;
        this.promptContextService = promptContextService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.selectedFileFetcherService = selectedFileFetcherService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.inquiryService = inquiryService;
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

        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-4", "gpt-4-32k"});
        fileComboBox = new ComboBox<>();
        VirtualFile[] openFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
        fileComboBox.setVisible(openFiles.length > 1);
        for (VirtualFile openFile : openFiles) {
            fileComboBox.addItem(new FileItem(openFile.getPath()));
        }
        modificationTypeComboBox = new ComboBox<>(new String[]{"Modify", "Modify Selected", "Fix", "Fix Selected", "Create", "Create Files", "Inquire", "Inquire Selected"});
        jLabel1 = new JLabel();
        advancedButton = new JButton("(Advanced) Add Context");
        advancedButton.addActionListener(e -> {
            //ApplicationManager.getApplication().invokeLater(() -> {
                promptContextService.setStatusLabel(hiddenLabel);
                PromptContextBuilder promptContextBuilder = promptContextBuilderFactory.create(promptContextService);
                promptContextBuilder.show();
            //ProvisionalModificationCustomizer provisionalModificationCustomizer = new ProvisionalModificationCustomizer(project, new ArrayList<>());
            //provisionalModificationCustomizer.show();
            //});
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
                VirtualFile[] openFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
                fileComboBox.setVisible(openFiles.length > 1);
                fileComboBox.removeAllItems();
                for (VirtualFile openFile : openFiles) {
                    fileComboBox.addItem(new FileItem(openFile.getPath()));
                }
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                FileEditorManagerListener.super.fileClosed(source, file);
                VirtualFile[] openFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
                fileComboBox.setVisible(openFiles.length > 1);
                fileComboBox.removeAllItems();
                for (VirtualFile openFile : openFiles) {
                    fileComboBox.addItem(new FileItem(openFile.getPath()));
                }
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                FileEditorManagerListener.super.selectionChanged(event);
                VirtualFile[] openFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
                fileComboBox.setVisible(openFiles.length > 1);
                fileComboBox.removeAllItems();
                for (VirtualFile openFile : openFiles) {
                    fileComboBox.addItem(new FileItem(openFile.getPath()));
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
                        automaticCodeModificationService.getCreatedCode(fileItem.getFilePath(), 0, 0, textArea.getText());
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
                        String description = textArea.getText();
                        //FileChooserWindow fileChooserWindow = new FileChooserWindow(description, priorContext, codeFileGeneratorService);
                        //fileChooserWindow.setVisible(true);
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
                break;
            case "Fix":
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this code file:");
                break;
            case "Create":
                button1.setText("Create");
                jLabel1.setText(" Create new code from scratch with the following description:");
                break;
            case "Create Files":
                button1.setText("Create");
                jLabel1.setText(" (Experimental) Create multiple code files from the following description:");
                break;
            case "Inquire":
                button1.setText("Ask");
                jLabel1.setText(" Ask the following question regarding this code file:");
                break;
            case "Modify Selected":
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to the selected code:");
                break;
            case "Fix Selected":
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this selected code:");
                break;
            case "Inquire Selected":
                button1.setText("Ask");
                jLabel1.setText(" Ask the following question regarding this selected code:");
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + selected);
        }
    }
}