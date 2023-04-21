package com.translator;

import com.google.inject.assistedinject.Assisted;
import com.intellij.ide.plugins.newui.HorizontalLayout;
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
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestion;
import com.translator.model.modification.ModificationType;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.code.GptToLanguageTransformerService;
import com.translator.service.context.PromptContextService;
import com.translator.service.context.PromptContextServiceImpl;
import com.translator.service.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.inquiry.InquiryService;
import com.translator.service.modification.AutomaticCodeModificationService;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.factory.PromptContextBuilderFactory;

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

public class ProvisionalModificationCustomizer extends JDialog {
    private Project project;
    private FileModificationSuggestion fileModificationSuggestion;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private CodactorToolWindowService codactorToolWindowService;
    private AutomaticCodeModificationService automaticCodeModificationService;
    private InquiryService inquiryService;
    private PromptContextService promptContextService;
    private PromptContextBuilderFactory promptContextBuilderFactory;
    private Editor defaultSolution;
    private Editor suggestedSolution;
    private Editor selectedEditor;

    @Inject
    public ProvisionalModificationCustomizer(Project project,
                                             @Assisted FileModificationSuggestion fileModificationSuggestion,
                                             CodeSnippetExtractorService codeSnippetExtractorService,
                                             CodactorToolWindowService codactorToolWindowService,
                                             InquiryService inquiryService,
                                             AutomaticCodeModificationServiceFactory automaticCodeModificationServiceFactory,
                                             PromptContextBuilderFactory promptContextBuilderFactory,
                                             FileModificationTrackerService fileModificationTrackerService) {
        setModal(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.project = project;
        this.fileModificationSuggestion = fileModificationSuggestion;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.inquiryService = inquiryService;
        this.promptContextService = new PromptContextServiceImpl();
        this.automaticCodeModificationService = automaticCodeModificationServiceFactory.create(promptContextService);
        this.promptContextBuilderFactory = promptContextBuilderFactory;
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
            Editor editor = editorFactory.createEditor(document, null);
            EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
            ((EditorEx) editor).setHighlighter(editorHighlighter);
            ((EditorEx) editor).setViewer(true);
            editor.getComponent().setPreferredSize(new Dimension(Integer.MAX_VALUE, editor.getComponent().getPreferredSize().height));
            defaultSolution = editor;
        });
        suggestedSolution = fileModificationSuggestion.getSuggestedCode();
        selectedEditor = suggestedSolution;
        setTitle("Custom DialogWrapper");
        initComponents();
    }

    private void initComponents() {
        Splitter verticalSplitter = new Splitter(true, 0.5f);

        JBPanel topPanel = new JBPanel(new BorderLayout());
        JBPanel bottomPanel = new JBPanel();

        verticalSplitter.setFirstComponent(topPanel);
        verticalSplitter.setSecondComponent(bottomPanel);

        Splitter topSplitter = new Splitter(false, 0.3f);
        topPanel.add(topSplitter, BorderLayout.CENTER);

        // JPanel with JScrollPane and JBList
        JBPanel listPanel = new JBPanel(new BorderLayout());
        ArrayList<Editor> editorList = new ArrayList<>();
        editorList.add(defaultSolution);
        editorList.add(suggestedSolution);
        JBList<Editor> list = new JBList<>(editorList);
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
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex == -1) {
                    return;
                }
                if (selectedIndex == 0) {
                    selectedEditor = defaultSolution;
                } else if (selectedIndex == 1) {
                    selectedEditor = suggestedSolution;
                }
            }
        });
        JBScrollPane scrollPane = new JBScrollPane(list);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel with an editor
        JBPanel editorPanel = new JBPanel(new BorderLayout());

        JPanel toolbarPanel = new JPanel(new HorizontalLayout(5));
        JButton acceptSolutionButton = new JButton("Accept Solution");
        JButton rejectAllChangesButton = new JButton("Reject All Changes");
        toolbarPanel.add(acceptSolutionButton);
        toolbarPanel.add(rejectAllChangesButton);
        editorPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Add the first editor from the list
        if (!editorList.isEmpty()) {
            Editor editor = editorList.get(0);
            EditorSettings editorSettings = editor.getSettings();
            editorSettings.setLineNumbersShown(true);
            editorSettings.setVirtualSpace(false);
            editorSettings.setLineMarkerAreaShown(true);
            editorSettings.setFoldingOutlineShown(true);
            editorSettings.setAdditionalLinesCount(2);
            editorSettings.setAdditionalColumnsCount(3);

            editorPanel.add(editor.getComponent(), BorderLayout.CENTER);
        }

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
        JComboBox<String> modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-4", "gpt-4-32k"});
        JComboBox<String> modificationTypeComboBox = new ComboBox<>(new String[]{"Modify", "Modify Selected", "Fix", "Fix Selected", "Create", "Create Files", "Inquire", "Inquire Selected"});
        JLabel jLabel1 = new JLabel();
        JButton advancedButton = new JButton("(Advanced) Add Context");
        advancedButton.addActionListener(e -> {
            //ApplicationManager.getApplication().invokeLater(() -> {
            promptContextService.setStatusLabel(hiddenLabel);
            PromptContextBuilder promptContextBuilder = promptContextBuilderFactory.create(promptContextService);
            promptContextBuilder.show();
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
        leftToolbar.add(modelComboBox);
        leftToolbar.add(modificationTypeComboBox);
        leftToolbar.add(jLabel1);
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
                        automaticCodeModificationService.getModifiedCodeModification(fileModificationSuggestion.getId(), code, 0, code.length(), textArea.getText(), ModificationType.MODIFY);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Modify Selected")) {
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(selectedEditor);
                    String code = null;
                    if (selectionModel != null) {
                        code = selectionModel.getSelectedText();
                    }
                    if (code != null && !code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCodeModification(fileModificationSuggestion.getId(), code, selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.MODIFY);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix")) {
                    String code = codeSnippetExtractorService.getAllText(selectedEditor);
                    if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCodeFix(fileModificationSuggestion.getId(), code, 0, code.length(), textArea.getText(), ModificationType.FIX);
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
                        automaticCodeModificationService.getModifiedCodeFix(fileModificationSuggestion.getId(), code, selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.FIX);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create")) {
                    if (!textArea.getText().isEmpty()) {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                        automaticCodeModificationService.getModifiedCodeCreation(fileModificationSuggestion.getFilePath(), 0, 0, textArea.getText());
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
                        String code = codeSnippetExtractorService.getAllText(selectedEditor);
                        String question = textArea.getText();
                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                        List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                        if (priorContextData != null) {
                            for (HistoricalContextObjectDataHolder data : priorContextData) {
                                priorContext.add(new HistoricalContextObjectHolder(data));
                            }
                        }
                        inquiryService.createInquiry(fileModificationSuggestion.getFilePath(), code, question, priorContext);
                    }
                } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Inquire Selected")) {
                    SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(selectedEditor);
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
                    inquiryService.createInquiry(fileModificationSuggestion.getFilePath(), code, question, priorContext);
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
                button1.setVisible(!selectedEditor.equals(defaultSolution));
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to this code file:");
                break;
            case "Fix":
                button1.setVisible(!selectedEditor.equals(defaultSolution));
                button1.setText("Fix");
                jLabel1.setText(" Fix the following error/problem in this code file:");
                break;
            case "Create":
                button1.setVisible(!selectedEditor.equals(defaultSolution));
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
                button1.setVisible(!selectedEditor.equals(defaultSolution));
                button1.setText("Modify");
                jLabel1.setText(" Implement the following modification(s) to the selected code:");
                break;
            case "Fix Selected":
                button1.setVisible(!selectedEditor.equals(defaultSolution));
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