package com.translator.view.codactor.dialog;

import com.google.inject.assistedinject.Assisted;
import com.intellij.ide.plugins.newui.HorizontalLayout;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestion;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.file.MassCodeFileGeneratorService;
import com.translator.service.codactor.inquiry.InquiryService;
import com.translator.service.codactor.modification.AutomaticCodeModificationService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import com.translator.view.codactor.factory.dialog.PromptContextBuilderDialogFactory;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;

public class ProvisionalModificationCustomizerDialog extends JDialog implements Disposable {
    private Project project;
    private FileModificationSuggestion fileModificationSuggestion;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private CodactorToolWindowService codactorToolWindowService;
    private AutomaticCodeModificationService automaticCodeModificationService;
    private InquiryService inquiryService;
    private PromptContextService promptContextService;
    private FileModificationTrackerService fileModificationTrackerService;
    private OpenAiModelService openAiModelService;
    private MultiFileCreateDialogFactory multiFileCreateDialogFactory;
    private PromptContextBuilderDialogFactory promptContextBuilderDialogFactory;
    private PromptContextServiceFactory promptContextServiceFactory;
    private InquiryViewerFactory inquiryViewerFactory;
    private Editor defaultSolution;
    private Editor suggestedSolution;
    private Editor selectedEditor;

    @Inject
    public ProvisionalModificationCustomizerDialog(Project project,
                                                   @Assisted FileModificationSuggestion fileModificationSuggestion,
                                                   CodeSnippetExtractorService codeSnippetExtractorService,
                                                   CodactorToolWindowService codactorToolWindowService,
                                                   InquiryService inquiryService,
                                                   MassCodeFileGeneratorService massCodeFileGeneratorService,
                                                   AutomaticCodeModificationService automaticCodeModificationService,
                                                   FileModificationTrackerService fileModificationTrackerService,
                                                   OpenAiModelService openAiModelService,
                                                   MultiFileCreateDialogFactory multiFileCreateDialogFactory,
                                                   PromptContextServiceFactory promptContextServiceFactory,
                                                   PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                                                   InquiryViewerFactory inquiryViewerFactory) {
        setModal(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.project = project;
        this.fileModificationSuggestion = fileModificationSuggestion;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.inquiryService = inquiryService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.openAiModelService = openAiModelService;
        this.automaticCodeModificationService = automaticCodeModificationService;
        this.promptContextBuilderDialogFactory = promptContextBuilderDialogFactory;
        this.promptContextServiceFactory = promptContextServiceFactory;
        this.inquiryViewerFactory = inquiryViewerFactory;
        this.multiFileCreateDialogFactory = multiFileCreateDialogFactory;
        this.promptContextService = promptContextServiceFactory.create();
        EditorFactory editorFactory = EditorFactory.getInstance();
        String extension;
        if (fileModificationSuggestion.getFilePath() == null) {
            extension = "txt";
        } else {
            extension = fileModificationSuggestion.getFilePath().substring(fileModificationSuggestion.getFilePath().lastIndexOf(".") + 1);
        }
        FileModification fileModification = fileModificationTrackerService.getModification(fileModificationSuggestion.getModificationId());
        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension(extension);
        ApplicationManager.getApplication().invokeAndWait(() -> {
            Document document = editorFactory.createDocument(fileModification.getBeforeText());
            defaultSolution = editorFactory.createEditor(document, null);
            EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
            ((EditorEx) defaultSolution).setHighlighter(editorHighlighter);
            ((EditorEx) defaultSolution).setViewer(true);
            defaultSolution.getComponent().setPreferredSize(new Dimension(Integer.MAX_VALUE, defaultSolution.getComponent().getPreferredSize().height));
            defaultSolution = defaultSolution;
        });
        suggestedSolution = fileModificationSuggestion.getSuggestedCodeEditor();
        selectedEditor = suggestedSolution;
        setTitle("Custom DialogWrapper");
        initComponents();
    }

    private void initComponents() {
        Splitter verticalSplitter = new Splitter(true, 0.5f);

        JBPanel topPanel = new JBPanel(new BorderLayout());
        JBPanel bottomPanel = new JBPanel(new BorderLayout());

        verticalSplitter.setFirstComponent(topPanel);
        verticalSplitter.setSecondComponent(bottomPanel);

        Splitter topSplitter = new Splitter(false, 0.3f);
        topPanel.add(topSplitter, BorderLayout.CENTER);

        // JPanel with JScrollPane and JBList
        JBPanel listPanel = new JBPanel(new BorderLayout());
        ArrayList<Editor> editorList = new ArrayList<>();
        editorList.add(defaultSolution);
        editorList.add(suggestedSolution);
        JBList<String> list = new JBList<>();
        list.setModel(new AbstractListModel<String>() {
            final String[] strings = { "Default", "Suggested" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Editor) {
                    Editor editor = (Editor) value;
                    return super.getListCellRendererComponent(list, editor.getDocument().getText(), index, isSelected, cellHasFocus);
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        JBPanel editorPanel = new JBPanel(new BorderLayout());

        JPanel toolbarPanel = new JPanel(new HorizontalLayout(5));
        JButton acceptSolutionButton = new JButton("Accept Solution");
        acceptSolutionButton.addActionListener(e -> {
            if (selectedEditor == suggestedSolution) {
                fileModificationTrackerService.implementModificationUpdate(fileModificationSuggestion.getModificationId(), fileModificationSuggestion.getSuggestedCodeEditor().getDocument().getText(), false);
            } else {
                fileModificationTrackerService.removeModification(fileModificationSuggestion.getModificationId());
            }
            dispose();
        });
        JButton rejectAllChangesButton = new JButton("Reject All Changes");
        rejectAllChangesButton.addActionListener(e -> {
            fileModificationTrackerService.removeModification(fileModificationSuggestion.getModificationId());
            dispose();
        });
        toolbarPanel.add(acceptSolutionButton);
        toolbarPanel.add(rejectAllChangesButton);
        editorPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Add the first editor from the list
        if (!editorList.isEmpty()) {
            EditorSettings editorSettings = selectedEditor.getSettings();
            editorSettings.setLineNumbersShown(true);
            editorSettings.setVirtualSpace(false);
            editorSettings.setLineMarkerAreaShown(true);
            editorSettings.setFoldingOutlineShown(true);
            editorSettings.setAdditionalLinesCount(2);
            editorSettings.setAdditionalColumnsCount(3);

            editorPanel.add(selectedEditor.getComponent(), BorderLayout.CENTER);
        }

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex == -1) {
                    return;
                }
                if (selectedIndex == 0) {
                    list.setSelectedIndex(0);
                    selectedEditor = defaultSolution;
                    editorPanel.add(selectedEditor.getComponent(), BorderLayout.CENTER);
                } else if (selectedIndex == 1) {
                    list.setSelectedIndex(1);
                    selectedEditor = suggestedSolution;
                }
                JBPanel newEditorPanel = new JBPanel(new BorderLayout());
                newEditorPanel.add(toolbarPanel, BorderLayout.NORTH);

                // Add the first editor from the list
                if (!editorList.isEmpty()) {
                    EditorSettings editorSettings = selectedEditor.getSettings();
                    editorSettings.setLineNumbersShown(true);
                    editorSettings.setVirtualSpace(false);
                    editorSettings.setLineMarkerAreaShown(true);
                    editorSettings.setFoldingOutlineShown(true);
                    editorSettings.setAdditionalLinesCount(2);
                    editorSettings.setAdditionalColumnsCount(3);

                    newEditorPanel.add(selectedEditor.getComponent(), BorderLayout.CENTER);
                }
                topSplitter.setSecondComponent(newEditorPanel);
            }
        });
        JBScrollPane scrollPane = new JBScrollPane(list);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel with an editor

        topSplitter.setFirstComponent(listPanel);
        topSplitter.setSecondComponent(editorPanel);

        JBTextArea textArea = new JBTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        JBScrollPane scrollPane2 = new JBScrollPane(textArea);

        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton();
        button2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));

        JLabel hiddenLabel = new JLabel();
        hiddenLabel.setVisible(false);
        JComboBox<String> modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "gpt-4-0314", "gpt-4-32k-0314", "gpt-3.5-turbo-0613", "gpt-4-0613"});
        modelComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                openAiModelService.setSelectedOpenAiModel(model);
            }
        });
        JComboBox<String> modificationTypeComboBox = new ComboBox<>(new String[]{"Modify", "Modify Selected", "Fix", "Fix Selected", "Create", "Create Files", "Inquire", "Inquire Selected"});
        JLabel jLabel1 = new JLabel();
        JButton advancedButton = new JButton("(Advanced) Add Context");
        advancedButton.addActionListener(e -> {
            //ApplicationManager.getApplication().invokeLater(() -> {
            promptContextService.setStatusLabel(hiddenLabel);
            PromptContextBuilderDialog promptContextBuilderDialog = promptContextBuilderDialogFactory.create(promptContextService);
            promptContextBuilderDialog.show();
            //});
        });

        button1.setText("Modify");
        jLabel1.setText(" Implement the following modification(s) to this code file:");

        modificationTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) modificationTypeComboBox.getSelectedItem();
                updateLabelAndButton(selected, button1, jLabel1);
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
        JPanel comboBoxesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Set vertical gap to 0
        comboBoxesPanel.add(modelComboBox);
        comboBoxesPanel.add(modificationTypeComboBox);
        comboBoxesPanel.add(jLabel1);
        leftToolbar.add(comboBoxesPanel);
        JPanel rightToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5)); // Set horizontal gap to 0
        rightToolbar.add(hiddenLabel);
        rightToolbar.add(advancedButton);

        topToolbar.add(leftToolbar);
        topToolbar.add(rightToolbar, BorderLayout.EAST);

        bottomPanel.add(topToolbar, BorderLayout.NORTH);
        bottomPanel.add(scrollPane2, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.EAST);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assert modificationTypeComboBox.getSelectedItem() != null;
                if (modificationTypeComboBox.getSelectedItem().toString().equals("Modify")) {
                    String code = codeSnippetExtractorService.getAllText(selectedEditor);
                    if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCodeModification(fileModificationSuggestion.getId(), code, 0, code.length(), textArea.getText(), ModificationType.MODIFY, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Modify Selected")) {
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(selectedEditor);
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    if (code != null && !code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCodeModification(fileModificationSuggestion.getId(), code, selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.MODIFY, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix")) {
                    String code = codeSnippetExtractorService.getAllText(selectedEditor);
                    if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCodeFix(fileModificationSuggestion.getId(), code, 0, code.length(), textArea.getText(), ModificationType.FIX, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix Selected")) {
                    codactorToolWindowService.openModificationQueueViewerToolWindow();
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(selectedEditor);
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    if (code != null && !code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCodeFix(fileModificationSuggestion.getId(), code, selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.FIX, promptContextService.getPromptContext());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create")) {
                    if (!textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCodeCreation(fileModificationSuggestion.getFilePath(), 0, 0, textArea.getText(), promptContextService.getPromptContext());
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
                        String code = codeSnippetExtractorService.getAllText(selectedEditor);
                        String question = textArea.getText();
                        InquiryViewer inquiryViewer = inquiryViewerFactory.create();
                        inquiryService.createInquiry(inquiryViewer, fileModificationSuggestion.getFilePath(), code, question, promptContextService.getPromptContext(), openAiModelService.getSelectedOpenAiModel());
                        promptContextService.clearPromptContext();
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Inquire Selected")) {
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(selectedEditor);
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    String question = textArea.getText();
                    InquiryViewer inquiryViewer = inquiryViewerFactory.create();
                    inquiryService.createInquiry(inquiryViewer, fileModificationSuggestion.getFilePath(), code, question, promptContextService.getPromptContext(), openAiModelService.getSelectedOpenAiModel());
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

        setContentPane(verticalSplitter);
        pack();
    }

    private void updateLabelAndButton(String selected, JButton button1, JLabel jLabel1) {
        if (selected == null) {
            return;
        }
        switch (selected) {
            case "Modify":
                button1.setEnabled(!selectedEditor.equals(defaultSolution));
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to this code file:");
                break;
            case "Fix":
                button1.setEnabled(!selectedEditor.equals(defaultSolution));
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this code file:");
                break;
            case "Create":
                button1.setEnabled(!selectedEditor.equals(defaultSolution));
                button1.setText("Create");
                jLabel1.setText(" Create new code from scratch with the following description:");
                break;
            case "Create Files":
                button1.setEnabled(true);
                button1.setText("Create");
                jLabel1.setText(" (Experimental) Create multiple code files from the following description:");
                break;
            case "Inquire":
                button1.setEnabled(true);
                button1.setText("Ask");
                jLabel1.setText(" Ask the following question regarding this code file:");
                break;
            case "Modify Selected":
                button1.setEnabled(!selectedEditor.equals(defaultSolution));
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to the selected code:");
                break;
            case "Fix Selected":
                button1.setEnabled(!selectedEditor.equals(defaultSolution));
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this selected code:");
                break;
            case "Inquire Selected":
                button1.setEnabled(true);
                button1.setText("Ask");
                jLabel1.setText(" Ask the following question regarding this selected code:");
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + selected);
        }
    }

    public FileModificationSuggestion getFileModificationSuggestion() {
        return fileModificationSuggestion;
    }
}