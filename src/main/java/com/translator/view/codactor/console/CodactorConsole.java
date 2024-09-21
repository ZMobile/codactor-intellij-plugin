package com.translator.view.codactor.console;

import com.google.gson.Gson;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.*;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.translator.model.codactor.ide.file.FileItem;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.ai.chat.functions.InquiryFunctionCallProcessorService;
import com.translator.service.codactor.ai.chat.functions.directives.test.RunTestAndGetOutputService;
import com.translator.service.codactor.ai.chat.functions.directives.test.RunTestAndGetOutputServiceImpl;
import com.translator.service.codactor.ai.modification.AiCodeModificationService;
import com.translator.service.codactor.ai.modification.authorization.VerifyIsTestFileService;
import com.translator.service.codactor.ai.modification.authorization.VerifyIsTestFileServiceImpl;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.ide.editor.psi.FindImplementationsService;
import com.translator.service.codactor.ide.editor.psi.FindUsagesService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.ide.file.SelectedFileFetcherService;
import com.translator.service.codactor.ai.chat.inquiry.InquiryService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.service.codactor.test.*;
import com.translator.service.codactor.ui.ModificationTypeComboBoxService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;
import com.translator.view.codactor.dialog.PromptContextBuilderDialog;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import com.translator.view.codactor.factory.dialog.PromptContextBuilderDialogFactory;
import com.translator.view.codactor.listener.file.CodactorFileEditorManagerListener;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import com.translator.view.uml.application.CodactorUmlBuilderApplication;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CodactorConsole extends JBPanel<CodactorConsole> {
    private Project project;
    private JBTextArea textArea;
    private JButton button1;
    private JButton button2;
    private ComboBox<String> modelComboBox;
    private ComboBox<FileItem> fileComboBox;
    private ComboBox<String> modificationTypeComboBox;
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
    private AiCodeModificationService aiCodeModificationService;
    private InquiryService inquiryService;
    private OpenAiModelService openAiModelService;
    private ModificationTypeComboBoxService modificationTypeComboBoxService;
    // For testing purposes
    private Gson gson;
    private FindImplementationsService findImplementationsService;
    private FindUsagesService findUsagesService;
    private CodeHighlighterService codeHighlighterService;
    // For testing purposes
    private CodactorUmlBuilderApplication codactorUmlBuilderApplication;
    private MultiFileCreateDialogFactory multiFileCreateDialogFactory;
    private PromptContextBuilderDialogFactory promptContextBuilderDialogFactory;
    private InquiryViewerFactory inquiryViewerFactory;
    private InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService;
    private DiffEditorGeneratorService diffEditorGeneratorService;

    @Inject
    public CodactorConsole(Project project,
                           PromptContextServiceFactory promptContextServiceFactory,
                           CodactorToolWindowService codactorToolWindowService,
                           SelectedFileFetcherService selectedFileFetcherService,
                           CodeSnippetExtractorService codeSnippetExtractorService,
                           InquiryService inquiryService,
                           OpenAiModelService openAiModelService,
                           ModificationTypeComboBoxService modificationTypeComboBoxService,
                           AiCodeModificationService aiCodeModificationService,
                           Gson gson,
                           FindImplementationsService findImplementationsService,
                           FindUsagesService findUsagesService,
                           CodeHighlighterService codeHighlighterService,
                           //CodactorUmlBuilderApplication codactorUmlBuilderApplication,
                           MultiFileCreateDialogFactory multiFileCreateDialogFactory,
                           PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                           InquiryViewerFactory inquiryViewerFactory,
                           InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService,
                           DiffEditorGeneratorService diffEditorGeneratorService) {
        super(new BorderLayout());
        this.project = project;
        this.promptContextService = promptContextServiceFactory.create();
        this.codactorToolWindowService = codactorToolWindowService;
        this.selectedFileFetcherService = selectedFileFetcherService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.inquiryService = inquiryService;
        this.openAiModelService = openAiModelService;
        this.modificationTypeComboBoxService = modificationTypeComboBoxService;
        this.aiCodeModificationService = aiCodeModificationService;
        this.gson = gson;
        this.findImplementationsService = findImplementationsService;
        this.findUsagesService = findUsagesService;
        this.codeHighlighterService = codeHighlighterService;
        //this.codactorUmlBuilderApplication = codactorUmlBuilderApplication;
        this.multiFileCreateDialogFactory = multiFileCreateDialogFactory;
        this.promptContextBuilderDialogFactory = promptContextBuilderDialogFactory;
        this.inquiryViewerFactory = inquiryViewerFactory;
        this.inquiryFunctionCallProcessorService = inquiryFunctionCallProcessorService;
        this.diffEditorGeneratorService = diffEditorGeneratorService;

        textArea = new JBTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        JBScrollPane scrollPane = new JBScrollPane(textArea);

        button1 = new JButton("Button 1");
        Border emptyBorder = BorderFactory.createEmptyBorder();
        button1.setBorder(emptyBorder);
        button2 = new JButton();
        button2.setBorder(emptyBorder);
        button2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));

        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "gpt-4o"});
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
        modificationTypeComboBox = modificationTypeComboBoxService.getModificationTypeComboBox();
        jLabel1 = new JLabel();
        advancedButton = new JButton("(Advanced) Add Context");
        advancedButton.setBorder(emptyBorder);
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

        FileEditorManagerListener fileEditorManagerListener = new CodactorFileEditorManagerListener(project, selectedFileFetcherService, fileComboBox);
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

        JButton testButton = new JButton("Test");
        testButton.setBorder(emptyBorder);
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VerifyIsTestFileService verifyIsTestFileService = new VerifyIsTestFileServiceImpl();
                RunTestAndGetOutputService runTestAndGetOutputService = new RunTestAndGetOutputServiceImpl(project, verifyIsTestFileService);
                try {
                    System.out.println("Running test...");
                    System.out.println("output: " + runTestAndGetOutputService.runTestAndGetOutput("C:\\Users\\hzant\\IdeaProjects\\codactor-intellij-plugin\\src\\main\\java\\com\\translator\\service\\codactor\\line\\LineCounterServiceImplTest.java"));
                    System.out.println("Test completed.");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                /*String subjectLine = "Add two methods to the end of this class, one that prints \"Wohoo World One!\" and another that prints \"Hello World Two!\"";
                String beforeCode = "package com.translator.service.modification;\n\nimport com.translator.dao.user.record.modification.FileModificationRecordDao;\nimport com.translator.dao.user.record.modification.FileModificationSuggestionModificationRecordDao;\nimport com.translator.dao.user.record.modification.FileModificationSuggestionRecordDao;\nimport com.translator.model.api.openai.ChatGptCompletionResultResource;\nimport com.translator.model.api.openai.GptCodeCompletionResponseResource;\nimport com.translator.model.api.user.desktop.modification.DesktopCodeModificationResponseResource;\nimport com.translator.model.user.record.context.HistoricalContextObjectHolder;\nimport com.translator.model.user.record.modification.FileModificationRecord;\nimport com.translator.model.user.record.modification.FileModificationSuggestionModificationRecord;\nimport com.translator.model.user.record.modification.FileModificationSuggestionRecord;\nimport com.translator.model.user.record.modification.ModificationType;\nimport com.translator.service.inquiry.SubjectLineGeneratorAsyncService;\nimport com.translator.service.openai.ChatGptService;\nimport com.translator.service.openai.GptThreeService;\nimport com.translator.service.string.LanguageExtractorService;\nimport com.translator.service.string.StringIndentationSynchronizationService;\nimport com.translator.service.string.diff.StringCodeOmittedRestorerService;\nimport com.translator.service.user.record.modification.FileModificationRecordService;\nimport com.translator.service.user.record.modification.FileModificationSuggestionModificationRecordService;\nimport com.translator.service.user.record.modification.FileModificationSuggestionRecordService;\nimport org.springframework.stereotype.Service;\n\n\nimport java.util.ArrayList;\nimport java.util.List;\nimport java.util.concurrent.CompletableFuture;\nimport java.util.concurrent.ExecutionException;\n\n@Service\npublic class CodeModifierServiceImpl implements CodeModifierService {\n    private final FileModificationRecordDao fileModificationRecordDao;\n    private final FileModificationSuggestionRecordDao fileModificationSuggestionRecordDao;\n    private final FileModificationSuggestionModificationRecordDao fileModificationSuggestionModificationRecordDao;\n    private final ChatGptService chatGptService;\n    private final GptThreeService gptThreeService;\n    private final FileModificationRecordService fileModificationRecordService;\n    private final FileModificationSuggestionRecordService fileModificationSuggestionRecordService;\n    private final FileModificationSuggestionModificationRecordService fileModificationSuggestionModificationRecordService;\n    private final LanguageExtractorService languageExtractorService;\n    private final SubjectLineGeneratorAsyncService subjectLineGeneratorAsyncService;\n    private final StringIndentationSynchronizationService stringIndentationSynchronizationService;\n    private final StringCodeOmittedRestorerService stringCodeOmittedRestorerService;\n\n\n    public CodeModifierServiceImpl(FileModificationRecordDao fileModificationRecordDao,\n                                   FileModificationSuggestionRecordDao fileModificationSuggestionRecordDao,\n                                   FileModificationSuggestionModificationRecordDao fileModificationSuggestionModificationRecordDao,\n                                   ChatGptService chatGptService,\n                                   GptThreeService gptThreeService,\n                                   FileModificationRecordService fileModificationRecordService,\n                                   FileModificationSuggestionRecordService fileModificationSuggestionRecordService,\n                                   FileModificationSuggestionModificationRecordService fileModificationSuggestionModificationRecordService,\n                                   LanguageExtractorService languageExtractorService,\n                                   SubjectLineGeneratorAsyncService subjectLineGeneratorAsyncService,\n                                   StringIndentationSynchronizationService stringIndentationSynchronizationService,\n                                   StringCodeOmittedRestorerService stringCodeOmittedRestorerService) {\n        this.fileModificationRecordDao \u003d fileModificationRecordDao;\n        this.fileModificationSuggestionRecordDao \u003d fileModificationSuggestionRecordDao;\n        this.fileModificationSuggestionModificationRecordDao \u003d fileModificationSuggestionModificationRecordDao;\n        this.chatGptService \u003d chatGptService;\n        this.gptThreeService \u003d gptThreeService;\n        this.fileModificationRecordService \u003d fileModificationRecordService;\n        this.fileModificationSuggestionRecordService \u003d fileModificationSuggestionRecordService;\n        this.fileModificationSuggestionModificationRecordService \u003d fileModificationSuggestionModificationRecordService;\n        this.languageExtractorService \u003d languageExtractorService;\n        this.subjectLineGeneratorAsyncService \u003d subjectLineGeneratorAsyncService;\n        this.stringIndentationSynchronizationService \u003d stringIndentationSynchronizationService;\n        this.stringCodeOmittedRestorerService \u003d stringCodeOmittedRestorerService;\n    }\n\n\n    @Override\n    public List\u003cGptCodeCompletionResponseResource\u003e modifyCode(String openAiApiKey, String code, String modification, String model, List\u003cHistoricalContextObjectHolder\u003e priorContext, boolean azure, String azureResource, String azureDeployment) {\n        String prompt \u003d \"Modify this code: \\\"\" + code + \"\\\" with the following modification(s): \\\"\" + modification + \".\";\n        if (!model.equalsIgnoreCase(\"text-davinci-003\")) {\n            List\u003cGptCodeCompletionResponseResource\u003e options \u003d new ArrayList\u003c\u003e();\n            ChatGptCompletionResultResource chatGptCompletionResultResource;\n            if (priorContext !\u003d null) {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, priorContext, azure, azureResource, azureDeployment);\n            } else {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, azure, azureResource, azureDeployment);\n            }\n            if (chatGptCompletionResultResource.getChoices() \u003d\u003d null) {\n                options.add(new GptCodeCompletionResponseResource(null, chatGptCompletionResultResource.getError()));\n                return options;\n            }\n            String receivedCode \u003d chatGptCompletionResultResource.getChoices().get(0).getMessage().getContent();\n            receivedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, receivedCode);\n            String codeBeforeRestoration \u003d receivedCode;\n            receivedCode \u003d stringCodeOmittedRestorerService.restoreOmittedString(code, receivedCode);\n            String error \u003d chatGptCompletionResultResource.getError();\n            GptCodeCompletionResponseResource gptCodeCompletionResponseResource \u003d new GptCodeCompletionResponseResource(receivedCode, codeBeforeRestoration, error);\n            options.add(gptCodeCompletionResponseResource);\n            return options;\n        } else {\n            return gptThreeService.askForCode(openAiApiKey, prompt);\n        }\n    }\n\n\n\n    @Override\n    public GptCodeCompletionResponseResource modifyCodeSingular(String openAiApiKey, String code, String modification, String model, List\u003cHistoricalContextObjectHolder\u003e priorContext, boolean azure, String azureResource, String azureDeployment) {\n        String prompt \u003d \"Modify this code: \\\"\" + code + \"\\\" with the following modification(s): \\\"\" + modification + \".\";\n        if (!model.equalsIgnoreCase(\"text-davinci-003\")) {\n            ChatGptCompletionResultResource chatGptCompletionResultResource;\n            if (priorContext !\u003d null) {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, priorContext, azure, azureResource, azureDeployment);\n            } else {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, azure, azureResource, azureDeployment);\n            }\n            if (chatGptCompletionResultResource.getChoices() \u003d\u003d null) {\n                return new GptCodeCompletionResponseResource(null, chatGptCompletionResultResource.getError());\n            }\n            String receivedCode \u003d chatGptCompletionResultResource.getChoices().get(0).getMessage().getContent();\n            receivedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, receivedCode);\n            String codeBeforeRestoration \u003d receivedCode;\n            receivedCode \u003d stringCodeOmittedRestorerService.restoreOmittedString(code, receivedCode);\n            String error \u003d chatGptCompletionResultResource.getError();\n            return new GptCodeCompletionResponseResource(receivedCode, codeBeforeRestoration, error);\n        } else {\n            return gptThreeService.askForCodeSingular(prompt, openAiApiKey);\n        }\n    }\n\n    @Override\n    public DesktopCodeModificationResponseResource modifyDesktopCodeFile(String userId, String openAiApiKey, String filePath, String code, String modification, ModificationType modificationType, List\u003cHistoricalContextObjectHolder\u003e priorContext, String model, boolean azure, String azureResource, String azureDeployment) {\n        FileModificationRecord fileModificationRecord \u003d new FileModificationRecord(userId, filePath, code, modification, modificationType, model);\n        fileModificationRecordService.createFileModificationRecord(fileModificationRecord);\n        CompletableFuture\u003cString\u003e subjectLineFuture \u003d subjectLineGeneratorAsyncService.generateSubjectLine(modification, openAiApiKey, azure, azureResource, azureDeployment);\n        List\u003cGptCodeCompletionResponseResource\u003e modifiedCodeOptions \u003d modifyCode(openAiApiKey, code, modification, model, priorContext, azure, azureResource, azureDeployment);\n        List\u003cFileModificationSuggestionRecord\u003e fileModificationSuggestionRecords \u003d new ArrayList\u003c\u003e();\n        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource \u003d new DesktopCodeModificationResponseResource(fileModificationRecord);\n        String subjectLine \u003d null;\n        try {\n            subjectLine \u003d subjectLineFuture.get();\n            FileModificationRecord newFileModificationRecord \u003d fileModificationRecordDao.get(fileModificationRecord.getId());\n            newFileModificationRecord.setSubjectLine(subjectLine);\n            desktopCodeModificationResponseResource.setSubjectLine(subjectLine);\n            fileModificationRecordDao.put(newFileModificationRecord);\n        } catch (InterruptedException | ExecutionException e) {\n            throw new RuntimeException(e);\n        }\n        for (GptCodeCompletionResponseResource modifiedCode : modifiedCodeOptions) {\n            if (modifiedCode.getError() !\u003d null) {\n                return new DesktopCodeModificationResponseResource(modifiedCode.getError());\n            }\n            String language \u003d languageExtractorService.extractLanguage(modifiedCode.getCode());\n            String isolatedCode \u003d languageExtractorService.isolateCode(modifiedCode.getCode());\n            String isolatedCodeBeforeRestoration \u003d languageExtractorService.isolateCode(modifiedCode.getCodeBeforeRestoration());\n            FileModificationSuggestionRecord fileModificationSuggestionRecord \u003d new FileModificationSuggestionRecord(userId, fileModificationRecord.getId(), modificationType, filePath, subjectLine, code, fileModificationRecord.getModification(), isolatedCodeBeforeRestoration, isolatedCode, language, priorContext);\n            fileModificationRecord.getModificationSuggestionRecordIds().add(fileModificationSuggestionRecord.getId());\n            desktopCodeModificationResponseResource.getModificationSuggestions().add(fileModificationSuggestionRecord);\n            fileModificationSuggestionRecords.add(fileModificationSuggestionRecord);\n        }\n        fileModificationRecordService.createFileModificationRecord(fileModificationRecord);\n        fileModificationSuggestionRecordService.createFileModificationSuggestionRecords(fileModificationSuggestionRecords);\n        return desktopCodeModificationResponseResource;\n    }\n\n    @Override\n    public FileModificationSuggestionModificationRecord modifyDesktopCodeFileModification(String suggestionId, String openAiApiKey, String code, String modification, ModificationType modificationType, List\u003cHistoricalContextObjectHolder\u003e priorContext, String model, boolean azure, String azureResource, String azureDeployment) {\n        FileModificationSuggestionRecord fileModificationSuggestionRecord \u003d fileModificationSuggestionRecordDao.get(suggestionId);\n        CompletableFuture\u003cString\u003e subjectLineFuture \u003d subjectLineGeneratorAsyncService.generateSubjectLine(modification, openAiApiKey, azure, azureResource, azureDeployment);\n        GptCodeCompletionResponseResource modifiedCode \u003d modifyCodeSingular(openAiApiKey, code, modification, model, priorContext, azure, azureResource, azureDeployment);\n        if (modifiedCode.getError() !\u003d null) {\n            return new FileModificationSuggestionModificationRecord(modifiedCode.getError());\n        }\n        String isolatedCode \u003d languageExtractorService.isolateCode(modifiedCode.getCode());\n        FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord \u003d new FileModificationSuggestionModificationRecord(fileModificationSuggestionRecord.getUserId(), fileModificationSuggestionRecord.getModificationId(), suggestionId, fileModificationSuggestionRecord.getFilePath(), code, modification, isolatedCode, modificationType, priorContext);\n        fileModificationSuggestionModificationRecordService.createFileModificationSuggestionModificationRecord(fileModificationSuggestionModificationRecord);\n        try {\n            String subjectLine \u003d subjectLineFuture.get();\n            FileModificationSuggestionModificationRecord newFileModificationSuggestionModificationRecord \u003d fileModificationSuggestionModificationRecordDao.get(fileModificationSuggestionModificationRecord.getId());\n            newFileModificationSuggestionModificationRecord.setSubjectLine(subjectLine);\n            fileModificationSuggestionModificationRecordDao.put(newFileModificationSuggestionModificationRecord);\n            fileModificationSuggestionModificationRecord \u003d newFileModificationSuggestionModificationRecord;\n        } catch (InterruptedException | ExecutionException e) {\n            throw new RuntimeException(e);\n        }\n        return fileModificationSuggestionModificationRecord;\n    }\n\n    @Override\n    public List\u003cGptCodeCompletionResponseResource\u003e fixCodeBug(String openAiApiKey, String code, String bug, String model, List\u003cHistoricalContextObjectHolder\u003e priorContext, boolean azure, String azureResource, String azureDeployment) {\n        String prompt \u003d \"This code: \\\"\" + code + \"\\\" contains the following bug/error: \\\"\" + bug + \"\\\" What might the fix for this bug/error look like?\";\n        if (!model.equalsIgnoreCase(\"text-davinci-003\")) {\n            List\u003cGptCodeCompletionResponseResource\u003e options \u003d new ArrayList\u003c\u003e();\n            ChatGptCompletionResultResource chatGptCompletionResultResource;\n            if (priorContext !\u003d null) {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, priorContext, azure, azureResource, azureDeployment);\n            } else {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, azure, azureResource, azureDeployment);\n            }\n            if (chatGptCompletionResultResource.getChoices() \u003d\u003d null) {\n                options.add(new GptCodeCompletionResponseResource(null, chatGptCompletionResultResource.getError()));\n                return options;\n            }\n            String receivedCode \u003d chatGptCompletionResultResource.getChoices().get(0).getMessage().getContent();\n            receivedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, receivedCode);\n            String codeBeforeRestoration \u003d receivedCode;\n            receivedCode \u003d stringCodeOmittedRestorerService.restoreOmittedString(code, receivedCode);\n            String error \u003d chatGptCompletionResultResource.getError();\n            GptCodeCompletionResponseResource gptCodeCompletionResponseResource \u003d new GptCodeCompletionResponseResource(receivedCode, codeBeforeRestoration, error);\n            options.add(gptCodeCompletionResponseResource);\n            return options;\n        } else {\n            return gptThreeService.askForCode(openAiApiKey, prompt);\n        }\n    }\n\n\n    @Override\n    public GptCodeCompletionResponseResource fixCodeBugSingular(String openAiApiKey, String code, String bug, String model, List\u003cHistoricalContextObjectHolder\u003e priorContext, boolean azure, String azureResource, String azureDeployment) {\n        String prompt \u003d \"This code: \\\"\" + code + \"\\\" contains the following bug/error: \\\"\" + bug + \"\\\" What might the fix for this bug/error look like?\";\n        if (!model.equalsIgnoreCase(\"text-davinci-003\")) {\n            ChatGptCompletionResultResource chatGptCompletionResultResource;\n            if (priorContext !\u003d null) {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, priorContext, azure, azureResource, azureDeployment);\n            } else {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, azure, azureResource, azureDeployment);\n            }\n            if (chatGptCompletionResultResource.getChoices() \u003d\u003d null) {\n                return new GptCodeCompletionResponseResource(null, chatGptCompletionResultResource.getError());\n            }\n            String receivedCode \u003d chatGptCompletionResultResource.getChoices().get(0).getMessage().getContent();\n            receivedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, receivedCode);\n            String codeBeforeRestoration \u003d receivedCode;\n            receivedCode \u003d stringCodeOmittedRestorerService.restoreOmittedString(code, receivedCode);\n            String error \u003d chatGptCompletionResultResource.getError();\n            return new GptCodeCompletionResponseResource(receivedCode, codeBeforeRestoration, error);\n        } else {\n            return gptThreeService.askForCodeSingular(prompt, openAiApiKey);\n        }\n    }\n\n\n    @Override\n    public DesktopCodeModificationResponseResource fixDesktopCodeFileBug(String userId, String openAiApiKey, String filePath, String code, String bug, ModificationType modificationType, List\u003cHistoricalContextObjectHolder\u003e priorContext, String model, boolean azure, String azureResource, String azureDeployment) {\n        FileModificationRecord fileModificationRecord \u003d new FileModificationRecord(userId, filePath, code, bug, modificationType, model);\n        fileModificationRecordService.createFileModificationRecord(fileModificationRecord);\n        CompletableFuture\u003cString\u003e subjectLineFuture \u003d subjectLineGeneratorAsyncService.generateSubjectLine(bug, openAiApiKey, azure, azureResource, azureDeployment);\n        List\u003cGptCodeCompletionResponseResource\u003e fixedCodeOptions \u003d fixCodeBug(openAiApiKey, code, bug, model, priorContext, azure, azureResource, azureDeployment);\n        List\u003cFileModificationSuggestionRecord\u003e fileModificationSuggestionRecords \u003d new ArrayList\u003c\u003e();\n        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource \u003d new DesktopCodeModificationResponseResource(fileModificationRecord);\n        String subjectLine \u003d null;\n        try {\n            subjectLine \u003d subjectLineFuture.get();\n            FileModificationRecord newFileModificationRecord \u003d fileModificationRecordDao.get(fileModificationRecord.getId());\n            newFileModificationRecord.setSubjectLine(subjectLine);\n            desktopCodeModificationResponseResource.setSubjectLine(subjectLine);\n            fileModificationRecordDao.put(newFileModificationRecord);\n        } catch (InterruptedException | ExecutionException e) {\n            throw new RuntimeException(e);\n        }\n        for (GptCodeCompletionResponseResource fixedCode : fixedCodeOptions) {\n            if (fixedCode.getError() !\u003d null) {\n                return new DesktopCodeModificationResponseResource(fixedCode.getError());\n            }\n            String language \u003d languageExtractorService.extractLanguage(fixedCode.getCode());\n            String isolatedCode \u003d languageExtractorService.isolateCode(fixedCode.getCode());\n            String isolatedCodeBeforeRestoration \u003d languageExtractorService.isolateCode(fixedCode.getCodeBeforeRestoration());\n            FileModificationSuggestionRecord fileModificationSuggestionRecord \u003d new FileModificationSuggestionRecord(userId, fileModificationRecord.getId(), modificationType, filePath, subjectLine, code, fileModificationRecord.getModification(), isolatedCodeBeforeRestoration, isolatedCode, language, priorContext);\n            fileModificationRecord.getModificationSuggestionRecordIds().add(fileModificationSuggestionRecord.getId());\n            desktopCodeModificationResponseResource.getModificationSuggestions().add(fileModificationSuggestionRecord);\n            fileModificationSuggestionRecords.add(fileModificationSuggestionRecord);\n        }\n        fileModificationSuggestionRecordService.createFileModificationSuggestionRecords(fileModificationSuggestionRecords);\n        return desktopCodeModificationResponseResource;\n    }\n\n    @Override\n    public FileModificationSuggestionModificationRecord fixDesktopCodeFileModificationBug(String suggestionId, String openAiApiKey, String code, String bug, ModificationType modificationType, List\u003cHistoricalContextObjectHolder\u003e priorContext, String model, boolean azure, String azureResource, String azureDeployment) {\n        FileModificationSuggestionRecord fileModificationSuggestionRecord \u003d fileModificationSuggestionRecordDao.get(suggestionId);\n        GptCodeCompletionResponseResource fixedCode \u003d fixCodeBugSingular(openAiApiKey, code, bug, model, priorContext, azure, azureResource, azureDeployment);\n        if (fixedCode.getError() !\u003d null) {\n            return new FileModificationSuggestionModificationRecord(fixedCode.getError());\n        }\n        String isolatedCode \u003d languageExtractorService.isolateCode(fixedCode.getCode());\n        isolatedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, isolatedCode);\n        FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord \u003d new FileModificationSuggestionModificationRecord(fileModificationSuggestionRecord.getUserId(), fileModificationSuggestionRecord.getModificationId(), suggestionId, fileModificationSuggestionRecord.getFilePath(), code, bug, isolatedCode, modificationType, priorContext);\n        fileModificationSuggestionModificationRecordService.createFileModificationSuggestionModificationRecord(fileModificationSuggestionModificationRecord);\n        return fileModificationSuggestionModificationRecord;\n    }\n}";
                String modification = "Add two methods to the end of this class, one that prints \"Wohoo World One!\" and another that prints \"Hello World Two!\"";
                String suggestedCodeBeforeRestoration = "package com.translator.service.modification;\n\n//... [Code omitted for brevity]\n\n@Service\npublic class CodeModifierServiceImpl implements CodeModifierService {\n    //... [Code omitted for brevity]\n\n\n    public CodeModifierServiceImpl(\n    //... [Code omitted for brevity]\n    ) {\n        //... [Code omitted for brevity]\n    }\n    \n    public void printHelloWorldTwo() {\n        System.out.println(\"Hello World Two!\");\n    }\n    \n    @Override\n    public DesktopCodeModificationResponseResource modifyDesktopCodeFile(\n    //... [Code omitted for brevity]\n    ) {\n        //... [Code omitted for brevity]\n    }\n    \n    @Override\n    public FileModificationSuggestionModificationRecord modifyDesktopCodeFileModification(\n    //... [Code omitted for brevity]\n    ) {\n        //... [Code omitted for brevity]\n    }\n    \n    @Override\n    public List\u003cGptCodeCompletionResponseResource\u003e fixCodeBug(\n    //... [Code omitted for brevity]\n    ) {\n        //... [Code omitted for brevity]\n    }\n    \n    \n    @Override\n    public GptCodeCompletionResponseResource fixCodeBugSingular(\n    //... [Code omitted for brevity]\n    ) {\n        //... [Code omitted for brevity]\n    }\n    \n    \n    @Override\n    public DesktopCodeModificationResponseResource fixDesktopCodeFileBug(\n    //... [Code omitted for brevity]\n    ) {\n        //... [Code omitted for brevity]\n    }\n    \n    @Override\n    public FileModificationSuggestionModificationRecord fixDesktopCodeFileModificationBug(\n        //... [Code omitted for brevity]\n    ) {\n        //... [Code omitted for brevity]\n    }\n    }\n";
                String suggestedCode = "\nimport com.translator.service.string.diff.StringCodeOmittedRestorerService;\nimport com.translator.service.user.record.modification.FileModificationRecordService;\nimport com.translator.service.user.record.modification.FileModificationSuggestionModificationRecordService;\nimport com.translator.service.user.record.modification.FileModificationSuggestionRecordService;\nimport org.springframework.stereotype.Service;\n\n\n\nimport java.util.ArrayList;\nimport java.util.List;\nimport java.util.concurrent.CompletableFuture;\nimport java.util.concurrent.ExecutionException;\n\n@Service\npublic class CodeModifierServiceImpl implements CodeModifierService {\n    private final FileModificationRecordDao fileModificationRecordDao;\n    private final FileModificationSuggestionRecordDao fileModificationSuggestionRecordDao;\n    private final FileModificationSuggestionModificationRecordDao fileModificationSuggestionModificationRecordDao;\n    private final ChatGptService chatGptService;\n    private final GptThreeService gptThreeService;\n    private final FileModificationRecordService fileModificationRecordService;\n    private final FileModificationSuggestionRecordService fileModificationSuggestionRecordService;\n    private final FileModificationSuggestionModificationRecordService fileModificationSuggestionModificationRecordService;\n    private final LanguageExtractorService languageExtractorService;\n    private final SubjectLineGeneratorAsyncService subjectLineGeneratorAsyncService;\n    private final StringIndentationSynchronizationService stringIndentationSynchronizationService;\n    private final StringCodeOmittedRestorerService stringCodeOmittedRestorerService;\n\n\n\n    public CodeModifierServiceImpl(FileModificationRecordDao fileModificationRecordDao,\n                                   FileModificationSuggestionRecordDao fileModificationSuggestionRecordDao,\n                                   FileModificationSuggestionModificationRecordDao fileModificationSuggestionModificationRecordDao,\n                                   ChatGptService chatGptService,\n                                   GptThreeService gptThreeService,\n                                   FileModificationRecordService fileModificationRecordService,\n                                   FileModificationSuggestionRecordService fileModificationSuggestionRecordService,\n                                   FileModificationSuggestionModificationRecordService fileModificationSuggestionModificationRecordService,\n                                   LanguageExtractorService languageExtractorService,\n                                   SubjectLineGeneratorAsyncService subjectLineGeneratorAsyncService,\n                                   StringIndentationSynchronizationService stringIndentationSynchronizationService,\n                                   StringCodeOmittedRestorerService stringCodeOmittedRestorerService) {\n        this.fileModificationRecordDao \u003d fileModificationRecordDao;\n        this.fileModificationSuggestionRecordDao \u003d fileModificationSuggestionRecordDao;\n        this.fileModificationSuggestionModificationRecordDao \u003d fileModificationSuggestionModificationRecordDao;\n        this.chatGptService \u003d chatGptService;\n        this.gptThreeService \u003d gptThreeService;\n        this.fileModificationRecordService \u003d fileModificationRecordService;\n        this.fileModificationSuggestionRecordService \u003d fileModificationSuggestionRecordService;\n        this.fileModificationSuggestionModificationRecordService \u003d fileModificationSuggestionModificationRecordService;\n        this.languageExtractorService \u003d languageExtractorService;\n        this.subjectLineGeneratorAsyncService \u003d subjectLineGeneratorAsyncService;\n        this.stringIndentationSynchronizationService \u003d stringIndentationSynchronizationService;\n        this.stringCodeOmittedRestorerService \u003d stringCodeOmittedRestorerService;\n    }\n\n\n\n    @Override\n    public List\u003cGptCodeCompletionResponseResource\u003e modifyCode(String openAiApiKey, String code, String modification, String model, List\u003cHistoricalContextObjectHolder\u003e priorContext, boolean azure, String azureResource, String azureDeployment) {\n        String prompt \u003d \"Modify this code: \\\"\" + code + \"\\\" with the following modification(s): \\\"\" + modification + \".\";\n        if (!model.equalsIgnoreCase(\"text-davinci-003\")) {\n            List\u003cGptCodeCompletionResponseResource\u003e options \u003d new ArrayList\u003c\u003e();\n            ChatGptCompletionResultResource chatGptCompletionResultResource;\n            if (priorContext !\u003d null) {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, priorContext, azure, azureResource, azureDeployment);\n            } else {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, azure, azureResource, azureDeployment);\n            }\n            if (chatGptCompletionResultResource.getChoices() \u003d\u003d null) {\n                options.add(new GptCodeCompletionResponseResource(null, chatGptCompletionResultResource.getError()));\n                return options;\n            }\n            String receivedCode \u003d chatGptCompletionResultResource.getChoices().get(0).getMessage().getContent();\n            receivedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, receivedCode);\n            String codeBeforeRestoration \u003d receivedCode;\n            receivedCode \u003d stringCodeOmittedRestorerService.restoreOmittedString(code, receivedCode);\n            String error \u003d chatGptCompletionResultResource.getError();\n            GptCodeCompletionResponseResource gptCodeCompletionResponseResource \u003d new GptCodeCompletionResponseResource(receivedCode, codeBeforeRestoration, error);\n            options.add(gptCodeCompletionResponseResource);\n            return options;\n        } else {\n            return gptThreeService.askForCode(openAiApiKey, prompt);\n        }\n    }\n    \n\n\n\n\n    \n    @Override\n    public GptCodeCompletionResponseResource modifyCodeSingular(String openAiApiKey, String code, String modification, String model, List\u003cHistoricalContextObjectHolder\u003e priorContext, boolean azure, String azureResource, String azureDeployment) {\n        String prompt \u003d \"Modify this code: \\\"\" + code + \"\\\" with the following modification(s): \\\"\" + modification + \".\";\n        if (!model.equalsIgnoreCase(\"text-davinci-003\")) {\n            ChatGptCompletionResultResource chatGptCompletionResultResource;\n            if (priorContext !\u003d null) {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, priorContext, azure, azureResource, azureDeployment);\n            } else {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, azure, azureResource, azureDeployment);\n            }\n            if (chatGptCompletionResultResource.getChoices() \u003d\u003d null) {\n                return new GptCodeCompletionResponseResource(null, chatGptCompletionResultResource.getError());\n            }\n            String receivedCode \u003d chatGptCompletionResultResource.getChoices().get(0).getMessage().getContent();\n            receivedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, receivedCode);\n            String codeBeforeRestoration \u003d receivedCode;\n            receivedCode \u003d stringCodeOmittedRestorerService.restoreOmittedString(code, receivedCode);\n            String error \u003d chatGptCompletionResultResource.getError();\n            return new GptCodeCompletionResponseResource(receivedCode, codeBeforeRestoration, error);\n        } else {\n            return gptThreeService.askForCodeSingular(prompt, openAiApiKey);\n        }\n    }\n    public DesktopCodeModificationResponseResource modifyDesktopCodeFile(\n\n    ) {\n\n    }\n\n    \n    @Override\n    public DesktopCodeModificationResponseResource modifyDesktopCodeFile(String userId, String openAiApiKey, String filePath, String code, String modification, ModificationType modificationType, List\u003cHistoricalContextObjectHolder\u003e priorContext, String model, boolean azure, String azureResource, String azureDeployment) {\n        FileModificationRecord fileModificationRecord \u003d new FileModificationRecord(userId, filePath, code, modification, modificationType, model);\n        fileModificationRecordService.createFileModificationRecord(fileModificationRecord);\n        CompletableFuture\u003cString\u003e subjectLineFuture \u003d subjectLineGeneratorAsyncService.generateSubjectLine(modification, openAiApiKey, azure, azureResource, azureDeployment);\n        List\u003cGptCodeCompletionResponseResource\u003e modifiedCodeOptions \u003d modifyCode(openAiApiKey, code, modification, model, priorContext, azure, azureResource, azureDeployment);\n        List\u003cFileModificationSuggestionRecord\u003e fileModificationSuggestionRecords \u003d new ArrayList\u003c\u003e();\n        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource \u003d new DesktopCodeModificationResponseResource(fileModificationRecord);\n        String subjectLine \u003d null;\n        try {\n            subjectLine \u003d subjectLineFuture.get();\n            FileModificationRecord newFileModificationRecord \u003d fileModificationRecordDao.get(fileModificationRecord.getId());\n            newFileModificationRecord.setSubjectLine(subjectLine);\n            desktopCodeModificationResponseResource.setSubjectLine(subjectLine);\n            fileModificationRecordDao.put(newFileModificationRecord);\n        } catch (InterruptedException | ExecutionException e) {\n            throw new RuntimeException(e);\n        }\n        for (GptCodeCompletionResponseResource modifiedCode : modifiedCodeOptions) {\n            if (modifiedCode.getError() !\u003d null) {\n                return new DesktopCodeModificationResponseResource(modifiedCode.getError());\n            }\n            String language \u003d languageExtractorService.extractLanguage(modifiedCode.getCode());\n            String isolatedCode \u003d languageExtractorService.isolateCode(modifiedCode.getCode());\n            String isolatedCodeBeforeRestoration \u003d languageExtractorService.isolateCode(modifiedCode.getCodeBeforeRestoration());\n            FileModificationSuggestionRecord fileModificationSuggestionRecord \u003d new FileModificationSuggestionRecord(userId, fileModificationRecord.getId(), modificationType, filePath, subjectLine, code, fileModificationRecord.getModification(), isolatedCodeBeforeRestoration, isolatedCode, language, priorContext);\n            fileModificationRecord.getModificationSuggestionRecordIds().add(fileModificationSuggestionRecord.getId());\n            desktopCodeModificationResponseResource.getModificationSuggestions().add(fileModificationSuggestionRecord);\n            fileModificationSuggestionRecords.add(fileModificationSuggestionRecord);\n        }\n        fileModificationRecordService.createFileModificationRecord(fileModificationRecord);\n        fileModificationSuggestionRecordService.createFileModificationSuggestionRecords(fileModificationSuggestionRecords);\n        return desktopCodeModificationResponseResource;\n    }\n    public FileModificationSuggestionModificationRecord modifyDesktopCodeFileModification(\n\n    ) {\n\n    }\n\n    \n    @Override\n    public FileModificationSuggestionModificationRecord modifyDesktopCodeFileModification(String suggestionId, String openAiApiKey, String code, String modification, ModificationType modificationType, List\u003cHistoricalContextObjectHolder\u003e priorContext, String model, boolean azure, String azureResource, String azureDeployment) {\n        FileModificationSuggestionRecord fileModificationSuggestionRecord \u003d fileModificationSuggestionRecordDao.get(suggestionId);\n        CompletableFuture\u003cString\u003e subjectLineFuture \u003d subjectLineGeneratorAsyncService.generateSubjectLine(modification, openAiApiKey, azure, azureResource, azureDeployment);\n        GptCodeCompletionResponseResource modifiedCode \u003d modifyCodeSingular(openAiApiKey, code, modification, model, priorContext, azure, azureResource, azureDeployment);\n        if (modifiedCode.getError() !\u003d null) {\n            return new FileModificationSuggestionModificationRecord(modifiedCode.getError());\n        }\n        String isolatedCode \u003d languageExtractorService.isolateCode(modifiedCode.getCode());\n        FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord \u003d new FileModificationSuggestionModificationRecord(fileModificationSuggestionRecord.getUserId(), fileModificationSuggestionRecord.getModificationId(), suggestionId, fileModificationSuggestionRecord.getFilePath(), code, modification, isolatedCode, modificationType, priorContext);\n        fileModificationSuggestionModificationRecordService.createFileModificationSuggestionModificationRecord(fileModificationSuggestionModificationRecord);\n        try {\n            String subjectLine \u003d subjectLineFuture.get();\n            FileModificationSuggestionModificationRecord newFileModificationSuggestionModificationRecord \u003d fileModificationSuggestionModificationRecordDao.get(fileModificationSuggestionModificationRecord.getId());\n            newFileModificationSuggestionModificationRecord.setSubjectLine(subjectLine);\n            fileModificationSuggestionModificationRecordDao.put(newFileModificationSuggestionModificationRecord);\n            fileModificationSuggestionModificationRecord \u003d newFileModificationSuggestionModificationRecord;\n        } catch (InterruptedException | ExecutionException e) {\n            throw new RuntimeException(e);\n        }\n        return fileModificationSuggestionModificationRecord;\n    }\n    public List\u003cGptCodeCompletionResponseResource\u003e fixCodeBug(\n\n    ) {\n\n    }\n\n    \n    \n    @Override\n    public List\u003cGptCodeCompletionResponseResource\u003e fixCodeBug(String openAiApiKey, String code, String bug, String model, List\u003cHistoricalContextObjectHolder\u003e priorContext, boolean azure, String azureResource, String azureDeployment) {\n        String prompt \u003d \"This code: \\\"\" + code + \"\\\" contains the following bug/error: \\\"\" + bug + \"\\\" What might the fix for this bug/error look like?\";\n        if (!model.equalsIgnoreCase(\"text-davinci-003\")) {\n            List\u003cGptCodeCompletionResponseResource\u003e options \u003d new ArrayList\u003c\u003e();\n            ChatGptCompletionResultResource chatGptCompletionResultResource;\n            if (priorContext !\u003d null) {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, priorContext, azure, azureResource, azureDeployment);\n            } else {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, azure, azureResource, azureDeployment);\n            }\n            if (chatGptCompletionResultResource.getChoices() \u003d\u003d null) {\n                options.add(new GptCodeCompletionResponseResource(null, chatGptCompletionResultResource.getError()));\n                return options;\n            }\n            String receivedCode \u003d chatGptCompletionResultResource.getChoices().get(0).getMessage().getContent();\n            receivedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, receivedCode);\n            String codeBeforeRestoration \u003d receivedCode;\n            receivedCode \u003d stringCodeOmittedRestorerService.restoreOmittedString(code, receivedCode);\n            String error \u003d chatGptCompletionResultResource.getError();\n            GptCodeCompletionResponseResource gptCodeCompletionResponseResource \u003d new GptCodeCompletionResponseResource(receivedCode, codeBeforeRestoration, error);\n            options.add(gptCodeCompletionResponseResource);\n            return options;\n        } else {\n            return gptThreeService.askForCode(openAiApiKey, prompt);\n        }\n    }\n    public GptCodeCompletionResponseResource fixCodeBugSingular(\n\n    ) {\n\n    }\n\n\n    \n    \n    @Override\n    public GptCodeCompletionResponseResource fixCodeBugSingular(String openAiApiKey, String code, String bug, String model, List\u003cHistoricalContextObjectHolder\u003e priorContext, boolean azure, String azureResource, String azureDeployment) {\n        String prompt \u003d \"This code: \\\"\" + code + \"\\\" contains the following bug/error: \\\"\" + bug + \"\\\" What might the fix for this bug/error look like?\";\n        if (!model.equalsIgnoreCase(\"text-davinci-003\")) {\n            ChatGptCompletionResultResource chatGptCompletionResultResource;\n            if (priorContext !\u003d null) {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, priorContext, azure, azureResource, azureDeployment);\n            } else {\n                chatGptCompletionResultResource \u003d chatGptService.askForCode(openAiApiKey, prompt, model, azure, azureResource, azureDeployment);\n            }\n            if (chatGptCompletionResultResource.getChoices() \u003d\u003d null) {\n                return new GptCodeCompletionResponseResource(null, chatGptCompletionResultResource.getError());\n            }\n            String receivedCode \u003d chatGptCompletionResultResource.getChoices().get(0).getMessage().getContent();\n            receivedCode \u003d stringIndentationSynchronizationService.synchronizeIndentation(code, receivedCode);\n            String codeBeforeRestoration \u003d receivedCode;\n            receivedCode \u003d stringCodeOmittedRestorerService.restoreOmittedString(code, receivedCode);\n            String error \u003d chatGptCompletionResultResource.getError();\n            return new GptCodeCompletionResponseResource(receivedCode, codeBeforeRestoration, error);\n        } else {\n            return gptThreeService.askForCodeSingular(prompt, openAiApiKey);\n        }\n    }\n    public DesktopCodeModificationResponseResource fixDesktopCodeFileBug(\n\n    ) {\n\n    }\n\n\n    \n    @Override\n    public DesktopCodeModificationResponseResource fixDesktopCodeFileBug(String userId, String openAiApiKey, String filePath, String code, String bug, ModificationType modificationType, List\u003cHistoricalContextObjectHolder\u003e priorContext, String model, boolean azure, String azureResource, String azureDeployment) {\n        FileModificationRecord fileModificationRecord \u003d new FileModificationRecord(userId, filePath, code, bug, modificationType, model);\n        fileModificationRecordService.createFileModificationRecord(fileModificationRecord);\n        CompletableFuture\u003cString\u003e subjectLineFuture \u003d subjectLineGeneratorAsyncService.generateSubjectLine(bug, openAiApiKey, azure, azureResource, azureDeployment);\n        List\u003cGptCodeCompletionResponseResource\u003e fixedCodeOptions \u003d fixCodeBug(openAiApiKey, code, bug, model, priorContext, azure, azureResource, azureDeployment);\n        List\u003cFileModificationSuggestionRecord\u003e fileModificationSuggestionRecords \u003d new ArrayList\u003c\u003e();\n        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource \u003d new DesktopCodeModificationResponseResource(fileModificationRecord);\n        String subjectLine \u003d null;\n        try {\n            subjectLine \u003d subjectLineFuture.get();\n            FileModificationRecord newFileModificationRecord \u003d fileModificationRecordDao.get(fileModificationRecord.getId());\n            newFileModificationRecord.setSubjectLine(subjectLine);\n            desktopCodeModificationResponseResource.setSubjectLine(subjectLine);\n            fileModificationRecordDao.put(newFileModificationRecord);\n        } catch (InterruptedException | ExecutionException e) {\n            throw new RuntimeException(e);\n        }\n        for (GptCodeCompletionResponseResource fixedCode : fixedCodeOptions) {\n            if (fixedCode.getError() !\u003d null) {\n                return new DesktopCodeModificationResponseResource(fixedCode.getError());\n            }\n            String language \u003d languageExtractorService.extractLanguage(fixedCode.getCode());\n            String isolatedCode \u003d languageExtractorService.isolateCode(fixedCode.getCode());\n            String isolatedCodeBeforeRestoration \u003d languageExtractorService.isolateCode(fixedCode.getCodeBeforeRestoration());\n            FileModificationSuggestionRecord fileModificationSuggestionRecord \u003d new FileModificationSuggestionRecord(userId, fileModificationRecord.getId(), modificationType, filePath, subjectLine, code, fileModificationRecord.getModification(), isolatedCodeBeforeRestoration, isolatedCode, language, priorContext);\n            fileModificationRecord.getModificationSuggestionRecordIds().add(fileModificationSuggestionRecord.getId());\n            desktopCodeModificationResponseResource.getModificationSuggestions().add(fileModificationSuggestionRecord);\n            fileModificationSuggestionRecords.add(fileModificationSuggestionRecord);\n        }\n        fileModificationSuggestionRecordService.createFileModificationSuggestionRecords(fileModificationSuggestionRecords);\n        return desktopCodeModificationResponseResource;\n    }\n    public FileModificationSuggestionModificationRecord fixDesktopCodeFileModificationBug(\n\n    ) {\n\n    }\n    }\n\n    public void printHelloWorldTwo() {\n        System.out.println(\"Hello World Two!\");\n    }\n\n    @Override\n    public FileModificationSuggestionModificationRecord fixDesktopCodeFileModificationBug(\n\n    ) {\n\n    }\n\n    @Override\n    public FileModificationSuggestionModificationRecord modifyDesktopCodeFileModification(\n\n    ) {\n\n    }\n\n    public CodeModifierServiceImpl(\n\n    ) {\n\n    }\n\n    @Override\n    public DesktopCodeModificationResponseResource fixDesktopCodeFileBug(\n\n    ) {\n\n    }\n\n    @Override\n    public List\u003cGptCodeCompletionResponseResource\u003e fixCodeBug(\n\n    ) {\n\n    }\n\n    @Override\n    public GptCodeCompletionResponseResource fixCodeBugSingular(\n\n    ) {\n\n    }\n\n    @Override\n    public DesktopCodeModificationResponseResource modifyDesktopCodeFile(\n\n    ) {\n\n    }\n}";

                DiffStringService diffStringService = new DiffStringServiceImpl();
                StringCodeOmittedRestorerService stringCodeOmittedRestorerService = new StringCodeOmittedRestorerServiceImpl();
                FusedMethodHandlerService fusedMethodHandlerService = new FusedMethodHandlerServiceImpl();
                StringCodeOmittedRestorationManagementService stringCodeOmittedRestorationManagementService = new StringCodeOmittedRestorationManagementServiceImpl(diffStringService, stringCodeOmittedRestorerService, fusedMethodHandlerService);

                        //Test


                String suggestedCodeAfterRestoration = null;

                    suggestedCodeAfterRestoration = "test"; //stringCodeOmittedRestorerService.restoreOmittedCode(beforeCode, suggestedCodeBeforeRestoration);

                //Editor editor = diffEditorGeneratorService.createDiffEditor(beforeCode, suggestedCodeAfterRestoration);
                EditorFactory editorFactory = EditorFactory.getInstance();
                Document document0 = editorFactory.createDocument(beforeCode);
                Editor editor = editorFactory.createEditor(document0);

                StringTokenizerService stringTokenizerService = new StringTokenizerServiceImpl();
                GoogleDiffMatchPatchService googleDiffMatchPatchService = new GoogleDiffMatchPatchServiceImpl();
                String googleRestructuredCode = googleDiffMatchPatchService.reconstructCodeWithGoogle(
                        beforeCode, suggestedCodeBeforeRestoration);
                Document document = editorFactory.createDocument("Restructured code:\n" + googleDiffMatchPatchService.reconstructCodeWithGoogle(
                        beforeCode, suggestedCodeBeforeRestoration));
                Editor editor5 = editorFactory.createEditor(editorFactory.createDocument(stringTokenizerService.reconstructModifiedCode(beforeCode, suggestedCodeBeforeRestoration)));
                Editor editor2 = editorFactory.createEditor(document);
                String diffString = stringTokenizerService.generateDiffString(beforeCode, suggestedCodeBeforeRestoration);
                Document document2 = editorFactory.createDocument("Diff Code:\n" + diffString);
                Editor editor3 = editorFactory.createEditor(document2);
                List<Range> topBeforeStopRanges = new ArrayList<>();
                List<Range> topModifiedStopRanges = new ArrayList<>();
                List<Range> bottomBeforeStopRanges = new ArrayList<>();
                List<Range> bottomModifiedStopRanges = new ArrayList<>();
                String restoredString = stringTokenizerService.reconstructModifiedCodeWithRestoration(topBeforeStopRanges, topModifiedStopRanges, bottomBeforeStopRanges, bottomModifiedStopRanges, beforeCode, suggestedCodeBeforeRestoration);
                Document document3 = editorFactory.createDocument("Diff Code restored to after code\n: " + restoredString);
                Editor editor4 = editorFactory.createEditor(document3);

                Editor afterRestoration = diffEditorGeneratorService.createDiffEditor(beforeCode, restoredString);
                TextAttributes blueTextAttributes = new TextAttributes();
                blueTextAttributes.setBackgroundColor(Color.decode("#009688"));
                TextAttributes greenTextAttributes = new TextAttributes();
                greenTextAttributes.setBackgroundColor(Color.decode("#228B22"));

                // Create a new JPanel with a BoxLayout that stacks its children on top of each other
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

                List<Boolean> booleans = new ArrayList<>();
                System.out.println("Bottom before stop ranges size: " + bottomBeforeStopRanges.size());
                for (Range range : bottomBeforeStopRanges) {
                    boolean rangeBoolean = false;
                    booleans.add(rangeBoolean);
                }

                for (int i = 0; i < bottomBeforeStopRanges.size(); i++) {
                    JButton button = new JButton();
                    button.setBorder(emptyBorder);
                    int finalI = i;
                    button.setText("Button " + finalI + " deactivated");
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            boolean rangeBoolean = booleans.get(finalI);
                            rangeBoolean = !rangeBoolean;
                            int number = finalI + 1;
                            if (rangeBoolean) {
                                button.setText("Button " + finalI + " activated");
                            } else {
                                button.setText("Button " + finalI + " deactivated");
                            }
                            booleans.remove(finalI);
                            booleans.add(finalI, rangeBoolean);
                            editor.getMarkupModel().removeAllHighlighters();
                            editor5.getMarkupModel().removeAllHighlighters();
                            for (int ii = 0; ii < bottomBeforeStopRanges.size(); ii++) {
                                Range range = bottomBeforeStopRanges.get(ii);
                                if (booleans.get(ii)) {
                                    editor.getMarkupModel().addRangeHighlighter(
                                            range.getStartIndex(),
                                            range.getEndIndex(),
                                            HighlighterLayer.ADDITIONAL_SYNTAX,
                                            greenTextAttributes,
                                            HighlighterTargetArea.EXACT_RANGE);
                                }
                            }
                            for (int ii = 0; ii < bottomBeforeStopRanges.size(); ii++) {
                                Range range = topBeforeStopRanges.get(ii);
                                if (booleans.get(ii)) {
                                    editor.getMarkupModel().addRangeHighlighter(
                                            range.getStartIndex(),
                                            range.getEndIndex(),
                                            HighlighterLayer.ADDITIONAL_SYNTAX,
                                            blueTextAttributes,
                                            HighlighterTargetArea.EXACT_RANGE);
                                }
                            }

                            for (int ii = 0; ii < bottomBeforeStopRanges.size(); ii++) {
                                Range range = bottomModifiedStopRanges.get(ii);
                                if (booleans.get(ii)) {
                                    editor5.getMarkupModel().addRangeHighlighter(
                                            range.getStartIndex(),
                                            range.getEndIndex(),
                                            HighlighterLayer.ADDITIONAL_SYNTAX,
                                            greenTextAttributes,
                                            HighlighterTargetArea.EXACT_RANGE);
                                }
                            }
                            for (int ii = 0; ii < bottomBeforeStopRanges.size(); ii++) {
                                Range range = topModifiedStopRanges.get(ii);
                                if (booleans.get(ii)) {
                                    editor5.getMarkupModel().addRangeHighlighter(
                                            range.getStartIndex(),
                                            range.getEndIndex(),
                                            HighlighterLayer.ADDITIONAL_SYNTAX,
                                            blueTextAttributes,
                                            HighlighterTargetArea.EXACT_RANGE);
                                }
                            }
                        }
                    });
                    panel.add(button);
                }

                // Add the Editors to the panel instead of the jFrame
                //panel.add(beforeRestoration.getComponent());
                panel.add(editor.getComponent());
                //panel.add(editor2.getComponent());
                panel.add(editor5.getComponent());
                //panel.add(editor3.getComponent());
                //panel.add(editor4.getComponent());
                panel.add(afterRestoration.getComponent());

                // Add the panel to the JScrollPane
                JScrollPane jbScrollPane = new JScrollPane(panel);

                // Create a new JFrame and add the JScrollPane
                JFrame jFrame = new JFrame();
                jFrame.getContentPane().add(jbScrollPane);

                // Pack the JFrame, which sizes it to fit the preferred size of all the contained components
                jFrame.pack();

                jFrame.setVisible(true);

                //inquiryFunctionCallProcessorService.testMethod();
                /*System.out.println("Selected tree view file: ");
                VirtualFile[] selectedFiles = selectedFileFetcherService.getSelectedFilesInTreeView();
                if (selectedFiles != null) {
                    for (VirtualFile selectedFile : selectedFiles) {
                        System.out.println(selectedFile.getPath());
                    }
                }*/

            }
        });
        rightToolbar.add(testButton);

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
                        aiCodeModificationService.getModifiedCode(fileItem.getFilePath(), textArea.getText(), ModificationType.MODIFY, promptContextService.getPromptContext());
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
                        aiCodeModificationService.getModifiedCode(fileItem.getFilePath(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.MODIFY_SELECTION, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix")) {
                    String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
                    if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        aiCodeModificationService.getFixedCode(fileItem.getFilePath(), textArea.getText(), ModificationType.FIX, promptContextService.getPromptContext());
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
                        aiCodeModificationService.getFixedCode(fileItem.getFilePath(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.FIX_SELECTION, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create")) {
                    if (!textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        aiCodeModificationService.getCreatedCode(fileItem.getFilePath(), textArea.getText(), promptContextService.getPromptContext());
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
                        //inquiryViewer.getInquiryChatBoxViewer().getToolBar().setVisible(false);
                        inquiryService.createInquiry(inquiryViewer, fileItem.getFilePath(), code, question, promptContextService.getPromptContext(), openAiModelService.getSelectedOpenAiModel());
                        codactorToolWindowService.createInquiryViewerToolWindow(inquiryViewer);
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
                    //inquiryViewer.getInquiryChatBoxViewer().getToolBar().setVisible(false);
                    inquiryService.createInquiry(inquiryViewer, fileItem.getFilePath(), code, question, promptContextService.getPromptContext(), openAiModelService.getSelectedOpenAiModel());
                    codactorToolWindowService.createInquiryViewerToolWindow(inquiryViewer);
                    promptContextService.clearPromptContext();
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Translate")) {
                    codactorToolWindowService.openModificationQueueViewerToolWindow();
                    aiCodeModificationService.getTranslatedCode(fileItem.getFilePath(), languageInputTextField.getText(), fileTypeTextField.getText(), promptContextService.getPromptContext());
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

    private void updateRanges() {

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
}