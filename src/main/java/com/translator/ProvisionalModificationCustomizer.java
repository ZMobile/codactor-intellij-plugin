package com.translator;

/*import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.*;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;*/

import javax.swing.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author zantehays
 */
public class ProvisionalModificationCustomizer extends JFrame /*implements SearchListener*/ {
    /*private Map<String, String> extensionToSyntaxMap = new HashMap<>();
    private FileModification currentEditingFileModification;
    private JBTextArea display;
    private String currentEditingSuggestionId;
    private CollapsibleSectionPanel csp;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private FindToolBar findToolBar;
    private ReplaceToolBar replaceToolBar;
    private JToggleButton inquiryViewerButton = new JToggleButton();
    private JToggleButton queuedModificationButton = new JToggleButton();
    private ContextQueryDao contextQueryDao;
    private InquiryDao inquiryDao;
    private OpenAiApiKeyService openAiApiKeyService;
    private CodeModificationHistoryDao codeModificationHistoryDao;
    private OpenAiModelService openAiModelService;
    private FirebaseTokenService firebaseTokenService;
    private CodeModificationService codeModificationService;
    private FileModificationTrackerService fileModificationTrackerService;
    private SearchResultParserService searchResultParserService;
    private SplitPaneService parentSplitPaneService;
    private SplitPaneService splitPaneService;
    private PromptContextService promptContextService;
    private PromptContextBuilderFactory promptContextBuilderFactory;
    private ModificationQueueViewer modificationQueueViewer;
    private InquiryViewer inquiryViewer;
    private LimitedSwingWorkerExecutor aiTaskExecutor = new LimitedSwingWorkerExecutor();
    private LimitedSwingWorkerExecutor inquiryTaskExecutor = new LimitedSwingWorkerExecutor();
    private LimitedSwingWorkerExecutor historyFetchingTaskExecutor = new LimitedSwingWorkerExecutor();
    private boolean resolved = false;

    @Inject
    public ProvisionalModificationCustomizer(@Named("extensionToSyntaxMap") Map<String, String> extensionToSyntaxMap,
                                             @Assisted FileModification currentEditingFileModification,
                                             InquiryDao inquiryDao,
                                             ContextQueryDao contextQueryDao,
                                             CodeModificationHistoryDao codeModificationHistoryDao,
                                             OpenAiModelService openAiModelService,
                                             OpenAiApiKeyService openAiApiKeyService,
                                             FirebaseTokenService firebaseTokenService,
                                             CodeModificationService codeModificationService,
                                             FileModificationTrackerService fileModificationTrackerService,
                                             SplitPaneService parentSplitPaneService,
                                             ModificationQueueViewer modificationQueueViewer,
                                             PromptContextBuilderFactory promptContextBuilderFactory) {
        this.extensionToSyntaxMap = extensionToSyntaxMap;
        this.currentEditingFileModification = currentEditingFileModification;
        this.inquiryDao = inquiryDao;
        this.contextQueryDao = contextQueryDao;
        this.codeModificationHistoryDao = codeModificationHistoryDao;
        this.openAiModelService = openAiModelService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.firebaseTokenService = firebaseTokenService;
        this.codeModificationService = codeModificationService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.parentSplitPaneService = parentSplitPaneService;
        this.modificationQueueViewer = modificationQueueViewer;
        this.promptContextBuilderFactory = promptContextBuilderFactory;
        this.inquiryViewer = new InquiryViewer(extensionToSyntaxMap, openAiApiKeyService, openAiModelService, splitPaneService, inquiryDao, inquiryTaskExecutor);
        this.searchResultParserService = new SearchResultParserServiceImpl(rTextScrollPane1);

        initComponents();

        this.promptContextService = new PromptContextServiceImpl();
        promptContextService.setStatusLabel(jLabel2);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initSearchDialogs();

        currentEditingSuggestionId = currentEditingFileModification.getModificationOptions().get(0).getId();

        csp = new CollapsibleSectionPanel();

        jPanel4.add(csp);

        setJMenuBar(createMenuBar());
        pack();

        setTitle("Modification Customizer");

        splitPaneService = new SplitPaneServiceImpl(new ModificationQueueListButtonServiceImpl(fileModificationTrackerService));
        splitPaneService.setToolBar(jToolBar1);
        splitPaneService.setSplitPane(jSplitPane3);
        splitPaneService.retractRightPanel();


        display = currentEditingFileModification.getModificationOptions().get(0).getDisplay();
        initializeNewDisplay(display);
        rTextScrollPane1.setViewportView(display);
        rTextScrollPane1.setLineNumbersEnabled(true);
        searchResultParserService.setDisplay(display);

        promptInput.setCurrentLineHighlightColor(new Color(0, 0, 0, 0));

        // Create an array of selection objects
        List<JPanelSelectionObject> selectionObjects = new ArrayList<>();
        selectionObjects.add(new JPanelSelectionObject("Queued Modifications", modificationQueueViewer));
        selectionObjects.add(new JPanelSelectionObject("Inquiry Viewer", inquiryViewer));
        //selectionObjects.add(new JPanelSelectionObject("Local History", new JPanel()));


// Create a button for each selection object and add it to the toolbar
        for (JPanelSelectionObject selectionObject : selectionObjects) {
            // Create the button and set its text and alignment
            JToggleButton button = null;
            if (selectionObject.getName().equals("Queued Modifications")) {
                button = queuedModificationButton;
            } else if (selectionObject.getName().equals("Inquiry Viewer")) {
                button = inquiryViewerButton;
            }

            assert button != null;
            //button.setVerticalTextPosition(JToggleButton.CENTER);
            //button.setHorizontalTextPosition(JToggleButton.LEFT);
            button.setUI(new VerticalButtonUI());
            button.setFocusPainted(false);
            String htmlText = "<html><body style='transform: rotate(-90deg); white-space: nowrap;'>" + selectionObject.getName() + "</body></html>";
            button.setText(htmlText);


            // Set the preferred size of the button based on the text
            int width = 20;
            int height = 145;
            button.setPreferredSize(new Dimension(width, height));
            final JToggleButton jToggleButton = button;
            // Add the action listener to expand the right component with the selection object's panel
            button.addActionListener(e -> {
                boolean isSelected;
                isSelected = !jToggleButton.getBackground().equals(jToolBar1.getBackground());
                if (isSelected) {
                    splitPaneService.retractRightPanel();
                } else {
                    int index = jToolBar1.getComponentIndex(jToggleButton);
                    splitPaneService.expandRightPanel(index, selectionObject.getPanel());
                }
            });


            // Add the button to the toolbar
            jToolBar1.add(button);
            jToolBar1.setMargin(new Insets(0, 0, 0, 0));
        }

// Add a glue component to push the buttons to the top
        jToolBar1.add(Box.createVerticalGlue());

// Add a glue component to push the buttons to the top
        jToolBar1.add(Box.createVerticalGlue());


        jToolBar4.setFloatable(false);
        jToolBar4.setBorderPainted(false);
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);
        jToolBar1.setFloatable(false);
        jToolBar1.setBorderPainted(false);
        jToolBar1.setLayout(new BoxLayout(jToolBar1, BoxLayout.Y_AXIS));

        jToggleButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!resolved) {
                    resolved = true;
                    if (currentEditingSuggestionId != null) {
                        fileModificationTrackerService.implementModificationUpdate(currentEditingFileModification.getId(), display.getText());
                    } else {
                        fileModificationTrackerService.removeModification(currentEditingFileModification.getId());
                    }
                    if (parentSplitPaneService.getRightComponent() instanceof CodeSnippetListViewer) {
                        parentSplitPaneService.retractRightPanel();
                    }
                    dispose();
                }
            }
        });


        jLabel1.setText(" Implement the following modification(s) to the code file:");
        jButton1.setText("Modify");
        jButton2.setMnemonic(0);

        jToolBar3.setFloatable(false);
        jToolBar3.setBorderPainted(false);
        jButton3.setText("(Advanced) Add Context");
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PromptContextBuilder promptContextBuilder = promptContextBuilderFactory.create(promptContextService);
                promptContextBuilder.setVisible(true);
            }
        });

        languageInputTextField.setVisible(false);
        jLabel3.setVisible(false);
        fileTypeInputTextField.setVisible(false);


        DefaultListModel<String> suggestions = new DefaultListModel<>();
        suggestions.addElement("Default");
        for (int i = 0; i < currentEditingFileModification.getModificationOptions().size(); i++) {
            suggestions.addElement("Solution " + (i + 1));
        }
        jList1.setModel(suggestions);
        JBTextArea defaultDisplay = new JBTextArea(currentEditingFileModification.getBeforeText());
        defaultDisplay.setSyntaxEditingStyle(display.getSyntaxEditingStyle());
        jList1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = jList1.getSelectedIndex();
                    if (selectedIndex > 0) {
                        promptInput.setVisible(true);
                        jButton1.setVisible(true);
                        jButton2.setVisible(true);
                        currentEditingSuggestionId = currentEditingFileModification.getModificationOptions().get(selectedIndex - 1).getId();
                        display = currentEditingFileModification.getModificationOptions().get(selectedIndex - 1).getDisplay();
                        display.setEditable(true);
                        initializeNewDisplay(display);
                        rTextScrollPane1.setViewportView(display);
                        searchResultParserService.setDisplay(display);
                        // Handle the selected item here
                    } else if (selectedIndex == 0) {
                        currentEditingSuggestionId = null;
                        display = defaultDisplay;
                        display.setEditable(false);
                        initializeNewDisplay(display);
                        rTextScrollPane1.setViewportView(display);
                        searchResultParserService.setDisplay(display);
                        promptInput.setVisible(false);
                        jButton1.setVisible(false);
                        jButton2.setVisible(false);
                    }
                }
            }
        });
        jList1.setSelectedIndex(1);
        jToolBar3.setFloatable(false);
        List<String> options = Arrays.asList("Modify", "Modify Selected", "Fix", "Fix Selected", "Create", "Inquire", "Inquire Selected");
        ComboBoxModel<String> model = new DefaultComboBoxModel<>(options.toArray(new String[0]));
        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) jComboBox1.getSelectedItem();
                jButton2.setVisible(true);
                jLabel3.setVisible(false);
                languageInputTextField.setVisible(false);
                fileTypeInputTextField.setVisible(false);
                promptInput.setVisible(true);
                assert selected != null;
                if (selected.equals("Modify")) {
                    jButton1.setText("Modify");
                    jLabel1.setText(" Implement the following modification(s) to this code file:");
                } else if (selected.equals("Fix")) {
                    jButton1.setText("Fix");
                    jLabel1.setText(" Fix the following error/problem in this code file:");
                } else if (selected.equals("Create")) {
                    jButton1.setText("Create");
                    jLabel1.setText(" Create new code from scratch with the following description:");
                } else if (selected.equals("Inquire")) {
                    jButton1.setText("Ask");
                    jLabel1.setText(" Ask the following question regarding this code file:");
                } else if (selected.equals("Modify Selected")) {
                    jButton1.setText("Modify");
                    jLabel1.setText(" Implement the following modification(s) to the selected code:");
                } else if (selected.equals("Fix Selected")) {
                    jButton1.setText("Fix");
                    jLabel1.setText(" Fix the following error/problem in this selected code:");
                } else if (selected.equals("Inquire Selected")) {
                    jButton1.setText("Ask");
                    jLabel1.setText(" Ask the following question regarding this selected code:");
                }
            }
        });

        jComboBox1.setModel(model);

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) jComboBox1.getSelectedItem();
                if (selected.equals("Modify")) {
                    if (!display.getText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String code = display.getText();
                        String modification = promptInput.getText();
                        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, 0, code.length(), ModificationType.MODIFY);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, code, modification, ModificationType.MODIFY, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeModification(desktopCodeModificationRequestResource);
                                if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                                    fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                                    fileModificationTrackerService.queueModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, fileModificationSuggestionModificationRecord.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Modify Selected")) {
                    if (display.getSelectedText() != null && !display.getSelectedText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String code = display.getSelectedText();
                        String modification = promptInput.getText();
                        int startIndex = display.getSelectionStart();
                        int endIndex = display.getSelectionEnd();
                        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, startIndex, endIndex, ModificationType.MODIFY_SELECTION);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, code, modification, ModificationType.MODIFY_SELECTION, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeModification(desktopCodeModificationRequestResource);
                                if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                                    fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                                    fileModificationTrackerService.queueModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, fileModificationSuggestionModificationRecord.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Fix")) {
                    if (!display.getText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String code = display.getText();
                        String error = promptInput.getText();
                        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, 0, code.length(), ModificationType.FIX);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, code, error, ModificationType.FIX, openAiApiKey,  openAiModelService.getSelectedOpenAiModel(), priorContext);
                                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeFix(desktopCodeModificationRequestResource);
                                if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                                    fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                                    fileModificationTrackerService.queueModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, fileModificationSuggestionModificationRecord.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Fix Selected")) {
                    if (display.getSelectedText() != null && !display.getSelectedText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String code = display.getSelectedText();
                        String error = promptInput.getSelectedText();
                        int startIndex = display.getSelectionStart();
                        int endIndex = display.getSelectionEnd();
                        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, startIndex, endIndex, ModificationType.FIX_SELECTION);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, code, error, ModificationType.FIX_SELECTION, openAiApiKey,  openAiModelService.getSelectedOpenAiModel(), priorContext);
                                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeFix(desktopCodeModificationRequestResource);
                                if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                                    fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                                    fileModificationTrackerService.queueModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, fileModificationSuggestionModificationRecord.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Create")) {
                    if (!promptInput.getText().isEmpty()) {
                        String description = promptInput.getText();
                        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(currentEditingFileModification.getFilePath(), currentEditingSuggestionId, 0, 0, ModificationType.CREATE);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(currentEditingFileModification.getFilePath(), description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeCreation(desktopCodeCreationRequestResource);
                                if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                                    fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                                    fileModificationTrackerService.queueModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, fileModificationSuggestionModificationRecord.getResponseCode(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Inquire")) {
                    if (!promptInput.getText().isEmpty()) {
                        String code = display.getText();
                        String question = promptInput.getText();
                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                        List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                        if (priorContextData != null) {
                            for (HistoricalContextObjectDataHolder data : priorContextData) {
                                priorContext.add(new HistoricalContextObjectHolder(data));
                            }
                        }
                        Inquiry temporaryInquiry = new Inquiry(null, currentEditingFileModification.getFilePath(), code, question, priorContext);
                        inquiryViewer.updateInquiryContents(temporaryInquiry);
                        inquiryViewer.setLoadingChat(true);
                        splitPaneService.expandRightPanel(1, inquiryViewer);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                Inquiry inquiry = inquiryDao.createInquiry(currentEditingFileModification.getFilePath(), code, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                inquiryViewer.updateInquiryContents(inquiry);
                                inquiryViewer.setLoadingChat(false);
                                splitPaneService.expandRightPanel(1, inquiryViewer);
                                promptContextService.clearPromptContext();
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Inquire Selected")) {
                    if (display.getSelectedText() != null && !display.getSelectedText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String code = display.getSelectedText();
                        String question = promptInput.getText();
                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                        List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                        if (priorContextData != null) {
                            for (HistoricalContextObjectDataHolder data : priorContextData) {
                                priorContext.add(new HistoricalContextObjectHolder(data));
                            }
                        }
                        Inquiry temporaryInquiry = new Inquiry(null, currentEditingFileModification.getFilePath(), code, question, priorContext);
                        inquiryViewer.updateInquiryContents(temporaryInquiry);
                        inquiryViewer.setLoadingChat(true);
                        splitPaneService.expandRightPanel(1, inquiryViewer);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                Inquiry inquiry = inquiryDao.createInquiry(currentEditingFileModification.getFilePath(), code, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                inquiryViewer.updateInquiryContents(inquiry);
                                inquiryViewer.setLoadingChat(false);
                                splitPaneService.expandRightPanel(1, inquiryViewer);
                                promptContextService.clearPromptContext();
                                return null;
                            }
                        };
                        worker.execute();
                    }
                }
            }
        });

        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Robot robot = new Robot();
                    promptInput.requestFocusInWindow();
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

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();


            }
        };
        //fileOpener.setFileFilter(filter);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileOpener = new JFileChooser();
        saveDialog = new JFileChooser();
        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();
        jFileChooser1 = new JFileChooser();
        jBTextAreaEditorKit1 = new org.fife.ui.rsyntaxtextarea.JBTextAreaEditorKit();
        jPopupMenu1 = new JPopupMenu();
        jDialog1 = new JDialog();
        jPanel6 = new JPanel();
        jTextField1 = new JTextField();
        jPasswordField1 = new JPasswordField();
        jPanel1 = new JPanel();
        jSplitPane1 = new JSplitPane();
        jPanel2 = new JPanel();
        jBScrollPane3 = new JBScrollPane();
        jList1 = new JList<>();
        jPanel5 = new JPanel();
        jPanel3 = new JPanel();
        jToolBar2 = new JToolBar();
        jComboBox1 = new JComboBox<>();
        jLabel1 = new JLabel();
        languageInputTextField = new JTextField();
        jLabel3 = new JLabel();
        fileTypeInputTextField = new JTextField();
        jButton2 = new JButton();
        jButton1 = new JButton();
        jBScrollPane1 = new JBScrollPane();
        promptInput = new org.fife.ui.rtextarea.JBTextArea();
        jToolBar4 = new JToolBar();
        jLabel2 = new JLabel();
        jButton3 = new JButton();
        jPanel4 = new JPanel();
        jSplitPane3 = new JSplitPane();
        jBScrollPane2 = new JBScrollPane();
        jList2 = new JList<>();
        jPanel7 = new JPanel();
        rTextScrollPane1 = new org.fife.ui.rtextarea.JBScrollPane();
        jBTextArea2 = new org.fife.ui.rsyntaxtextarea.JBTextArea();
        jToolBar3 = new JToolBar();
        jToggleButton1 = new JToggleButton();
        jToolBar1 = new JToolBar();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jTextField1.setText("jTextField2");

        GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(160, 160, 160)
                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(162, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        GroupLayout jDialog1Layout = new GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPasswordField1.setText("jPasswordField1");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(255, 255, 255));

        jList1.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setMaximumSize(new Dimension(80, 95));
        jList1.setMinimumSize(new Dimension(80, 95));
        jList1.setPreferredSize(new Dimension(80, 95));
        jBScrollPane3.setViewportView(jList1);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jBScrollPane3, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jBScrollPane3, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jToolBar2.setRollover(true);

        jComboBox1.setPreferredSize(new Dimension(125, 23));
        jToolBar2.add(jComboBox1);

        jLabel1.setText("jLabel1");
        jToolBar2.add(jLabel1);

        languageInputTextField.setMinimumSize(new Dimension(50, 30));
        languageInputTextField.setPreferredSize(new Dimension(50, 30));
        jToolBar2.add(languageInputTextField);

        jLabel3.setText(" to file type: ");
        jToolBar2.add(jLabel3);
        jLabel3.getAccessibleContext().setAccessibleName(" to file type:");
        jLabel3.getAccessibleContext().setAccessibleDescription("");

        fileTypeInputTextField.setMinimumSize(new Dimension(50, 30));
        fileTypeInputTextField.setPreferredSize(new Dimension(50, 30));
        fileTypeInputTextField.setSize(new Dimension(50, 30));
        fileTypeInputTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fileTypeInputTextFieldActionPerformed(evt);
            }
        });
        jToolBar2.add(fileTypeInputTextField);

        //String userHome = System.getProperty("user.home");
        //jButton2.setIcon(new javax.swing.ImageIcon(userHome + "/Codactor/resources/microphone_icon.png"));
        jButton2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));
        jButton2.setMaximumSize(new Dimension(80, 23));
        jButton2.setMinimumSize(new Dimension(80, 23));
        jButton2.setPreferredSize(new Dimension(80, 23));
        jButton2.setSize(new Dimension(80, 23));
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Enter");
        jButton1.setToolTipText("");
        jButton1.setMaximumSize(new Dimension(80, 23));
        jButton1.setMinimumSize(new Dimension(80, 23));
        jButton1.setPreferredSize(new Dimension(80, 23));
        jButton1.setSize(new Dimension(80, 23));

        jBScrollPane1.setBorder(null);

        promptInput.setColumns(20);
        promptInput.setRows(5);
        promptInput.setCurrentLineHighlightColor(new Color(242, 242, 242));
        jBScrollPane1.setViewportView(promptInput);

        jToolBar4.setRollover(true);

        jLabel2.setText("jLabel2");
        jToolBar4.add(jLabel2);

        jButton3.setText("jButton3");
        jButton3.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar4.add(jButton3);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 508, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jToolBar4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar4, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jButton1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
        );

        jSplitPane3.setResizeWeight(0.5);
        jSplitPane3.setToolTipText("");

        jList2.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane2.setViewportView(jList2);

        jSplitPane3.setRightComponent(jBScrollPane2);

        jBTextArea2.setColumns(20);
        jBTextArea2.setRows(5);
        rTextScrollPane1.setViewportView(jBTextArea2);

        jToolBar3.setBorder(null);
        jToolBar3.setRollover(true);

        jToggleButton1.setText("Accept Solution");
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar3.add(jToggleButton1);

        GroupLayout jPanel7Layout = new GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 365, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(rTextScrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(rTextScrollPane1, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
        );

        jSplitPane3.setLeftComponent(jPanel7);

        jToolBar1.setBorder(null);
        jToolBar1.setOrientation(SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSplitPane3, GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3)
            .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jSplitPane1.setRightComponent(jPanel5);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSplitPane1)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void fileTypeInputTextFieldActionPerformed(ActionEvent evt) {//GEN-FIRST:event_fileTypeInputTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fileTypeInputTextFieldActionPerformed

    private void initializeNewDisplay(JBTextArea newDisplay) {
        newDisplay.setCurrentLineHighlightColor(new Color(242, 242, 242));
        newDisplay.setColumns(20);
        newDisplay.setRows(5);
        newDisplay.setCodeFoldingEnabled(true);
        newDisplay.setMarkOccurrences(true);
        newDisplay.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                newDisplay.setCurrentLineHighlightColor(new Color(242,242,242));
            }

            @Override
            public void focusLost(FocusEvent e) {
                newDisplay.setCurrentLineHighlightColor(new Color(0, 0, 0, 0));
            }
        });
        newDisplay.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0 || (e.getModifiers() & KeyEvent.META_MASK) != 0)) {
                    newDisplay.setSelectionStart(0);
                    newDisplay.setSelectionEnd(newDisplay.getText().length());
                }
            }
        });
        if (currentEditingSuggestionId != null) {
            fileModificationTrackerService.getDocumentListenerService().insertModificationSuggestionDocumentListener(currentEditingSuggestionId);
        }
    }

    private class GoToLineAction extends AbstractAction {

        GoToLineAction() {
            super("Go To Line...");
            int c = getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            GoToDialog dialog = new GoToDialog(ProvisionalModificationCustomizer.this);
            dialog.setMaxLineNumberAllowed(display.getLineCount());
            dialog.setVisible(true);
            int line = dialog.getLineNumber();
            if (line>0) {
                try {
                    display.setCaretPosition(display.getLineStartOffset(line-1));
                } catch (BadLocationException ble) { // Never happens
                    UIManager.getLookAndFeel().provideErrorFeedback(display);
                    ble.printStackTrace();
                }
            }
        }

    }

    private class LookAndFeelAction extends AbstractAction {

        private UIManager.LookAndFeelInfo info;

        LookAndFeelAction(UIManager.LookAndFeelInfo info) {
            putValue(NAME, info.getName());
            this.info = info;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UIManager.setLookAndFeel(info.getClassName());
                SwingUtilities.updateComponentTreeUI(ProvisionalModificationCustomizer.this);
                if (findDialog!=null) {
                    findDialog.updateUI();
                    replaceDialog.updateUI();
                }
                pack();
            } catch (RuntimeException re) {
                throw re; // FindBugs
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ShowFindDialogAction extends AbstractAction {

        ShowFindDialogAction() {
            super("Find...");
            int c = getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            findDialog.setVisible(true);
        }

    }

    private class ShowReplaceDialogAction extends AbstractAction {

        ShowReplaceDialogAction() {
            super("Replace...");
            int c = getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            replaceDialog.setVisible(true);
        }

    }

    private void initSearchDialogs() {

        findDialog = new FindDialog(this, this);

        replaceDialog = new ReplaceDialog(this, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);

        // Create toolbars and tie their search contexts together also.
        findToolBar = new FindToolBar(this);
        findToolBar.setSearchContext(context);
        replaceToolBar = new ReplaceToolBar(this);
        replaceToolBar.setSearchContext(context);

    }

    @Override
    public void searchEvent(SearchEvent e) {

        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                result = SearchEngine.markAll(display, context);
                break;
            case FIND:
                result = SearchEngine.markAll(display, context);
                if (result.getMarkedCount() == 0) {
                    UIManager.getLookAndFeel().provideErrorFeedback(display);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(display, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(display);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(display, context);
                JOptionPane.showMessageDialog(null, result.getCount() +
                        " occurrences replaced.");
                break;
        }

        String text;
        if (result.wasFound()) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
        } else if (type == SearchEvent.Type.FIND && result.getMarkedCount()>0) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
            searchResultParserService.findNext(context.getSearchFor());
        } else if (type == SearchEvent.Type.MARK_ALL) {
            if (result.getMarkedCount()>0) {
                text = "Occurrences marked: " + result.getMarkedCount();
            }
            else {
                text = "";
            }
        }
        else {
            text = "Text not found";
            JOptionPane.showMessageDialog(display, "Text not found", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addItem(Action a, ButtonGroup bg, JMenu menu) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
        bg.add(item);
        menu.add(item);
    }

    private JMenuBar createMenuBar() {

        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        file.addSeparator();
        JMenuItem settings = new JMenuItem("Settings");
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsPage settingsPage = new SettingsPage(firebaseTokenService, openAiModelService);
                settingsPage.setVisible(true);
            }
        });

        file.add(settings);

        mb.add(file);

        JMenu menu = new JMenu("Search");
        menu.add(new JMenuItem(new ShowFindDialogAction()));
        menu.add(new JMenuItem(new ShowReplaceDialogAction()));
        menu.add(new JMenuItem(new GoToLineAction()));
        menu.addSeparator();


        int ctrl = getToolkit().getMenuShortcutKeyMask();
        int shift = InputEvent.SHIFT_MASK;
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl|shift);
        Action a = csp.addBottomComponent(ks, findToolBar);
        a.putValue(Action.NAME, "Show Find Search Bar");
        menu.add(new JMenuItem(a));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_R, ctrl|shift);
        a = csp.addBottomComponent(ks, replaceToolBar);
        a.putValue(Action.NAME, "Show Replace Search Bar");
        menu.add(new JMenuItem(a));


        mb.add(menu);

        return mb;
    }

    public String getSelectedText() {
        return display.getSelectedText();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JFileChooser fileOpener;
    private JTextField fileTypeInputTextField;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JDialog jDialog1;
    private JFileChooser jFileChooser1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JList<String> jList1;
    private JList<String> jList2;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JPanel jPanel7;
    private JPasswordField jPasswordField1;
    private JPopupMenu jPopupMenu1;
    private JBScrollPane jBScrollPane1;
    private JBScrollPane jBScrollPane2;
    private JBScrollPane jBScrollPane3;
    private JComboBox<String> jComboBox1;
    private JSplitPane jSplitPane1;
    private JSplitPane jSplitPane3;
    private JTextField jTextField1;
    private JToggleButton jToggleButton1;
    private JToolBar jToolBar1;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JToolBar jToolBar4;
    private JTextField languageInputTextField;
    private org.fife.ui.rtextarea.JBTextArea promptInput;
    private org.fife.ui.rsyntaxtextarea.JBTextArea jBTextArea2;
    private org.fife.ui.rsyntaxtextarea.JBTextAreaEditorKit jBTextAreaEditorKit1;
    private org.fife.ui.rtextarea.JBScrollPane rTextScrollPane1;
    private JFileChooser saveDialog;*/
    // End of variables declaration//GEN-END:variables
}
