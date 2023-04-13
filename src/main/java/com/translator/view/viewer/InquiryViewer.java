package com.translator.view.viewer;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;
import com.translator.model.inquiry.InquiryChatType;
import com.translator.model.modification.RecordType;
import com.translator.service.constructor.CodeFileGeneratorService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.view.menu.TextAreaWindow;
import com.translator.view.renderer.InquiryChatRenderer;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.service.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.view.window.FileChooserWindow;
import com.translator.worker.LimitedSwingWorker;
import com.translator.worker.LimitedSwingWorkerExecutor;
import com.intellij.ui.components.JBTextArea;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author zantehays
 */
public class InquiryViewer extends JPanel {
    private Inquiry inquiry = null;
    private InquiryDao inquiryDao;
    private JList<InquiryChatViewer> jList1;
    private ListSelectionListener listSelectionListener;
    private JPanel promptInputPanel;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JBScrollPane jBScrollPane1;
    private JButton jButton1;
    private JButton jButton2;
    private JButton otherInquiriesButton;
    private JButton newInquiryButton;
    private JButton discardInquiryButton;
    private JButton whatWasChangedButton;
    private JButton whatDoesThisDoButton;
    private JPopupMenu jPopupMenu1;
    private JMenuItem editItem;
    private JMenuItem regenerateItem;
    private JMenuItem previousChat;
    private JMenuItem nextChat;
    private JMenuItem autoGenerate;
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private CodactorToolWindowService codactorToolWindowService;
    private CodeFileGeneratorService codeFileGeneratorService;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private HistoricalModificationListViewer historicalModificationListViewer;
    private InquiryListViewer inquiryListViewer;
    private LimitedSwingWorkerExecutor inquiryTaskExecutor;
    private JBTextArea promptInput;
    private int selectedChat;

    public InquiryViewer(OpenAiApiKeyService openAiApiKeyService,
                         OpenAiModelService openAiModelService,
                         CodactorToolWindowService codactorToolWindowService,
                         CodeFileGeneratorService codeFileGeneratorService,
                         InquiryDao inquiryDao,
                         LimitedSwingWorkerExecutor inquiryTaskExecutor) {
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.codeFileGeneratorService = codeFileGeneratorService;
        this.textAreaHeightCalculatorService = new TextAreaHeightCalculatorServiceImpl();
        this.inquiryDao = inquiryDao;
        this.inquiryTaskExecutor = inquiryTaskExecutor;
        this.historicalModificationListViewer = null;
        this.inquiryListViewer = null;
        this.inquiry = new Inquiry(null, null, null, null, null, null, null, null, null, null);
        this.selectedChat = -1;
        initComponents();
    }

    private void initComponents() {
        jList1 = new JList<>();
        //jList1.setMaximumSize(new Dimension(getWidth(), Integer.MAX_VALUE));
        jList1.setModel(new DefaultListModel<>());
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList1.setCellRenderer(new InquiryChatRenderer());
        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = jList1.getSelectedIndex();
                    if (selectedIndex == -1) {
                        return;
                    }
                    selectedChat = selectedIndex;
                    InquiryChatViewer selectedInquiryChatViewer = jList1.getModel().getElementAt(selectedIndex);
                    JBTextArea selectedJBTextArea = (JBTextArea) selectedInquiryChatViewer.getComponents()[1];
                    Color highlightColor = Color.decode("#7FFFD4");
                    //Highlight the whole text are
                    try {
                        selectedJBTextArea.getHighlighter().addHighlight(0, selectedJBTextArea.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                    for (int i = 0; i < jList1.getModel().getSize(); i++) {
                        if (i == selectedChat) {
                            continue;
                        }
                        InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(i);
                        JBTextArea jBTextArea = (JBTextArea) inquiryChatViewer.getComponents()[1];
                        jBTextArea.getHighlighter().removeAllHighlights();
                    }
                }
            }
        };
        jList1.addListSelectionListener(listSelectionListener);
        jList1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //If Cntrl + C is pressed (or command for mac users)
                if (e.getKeyCode() == KeyEvent.VK_C && (e.isControlDown() || e.isMetaDown())) {
                    if (selectedChat != -1) {
                        //Copy the selected text to the clipboard
                        InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
                        JBTextArea jBTextArea = (JBTextArea) inquiryChatViewer.getComponents()[1];
                        StringSelection selection = new StringSelection(jBTextArea.getText());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, null);
                    }
                }
            }
        });
        jList1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                InquiryChatViewer inquiryChatViewer = null;
                if (e.getButton() == MouseEvent.BUTTON3) {
                    inquiryChatViewer = jList1.getModel().getElementAt(jList1.locationToIndex(e.getPoint()));
                    jList1.setSelectedIndex(jList1.locationToIndex(e.getPoint()));
                    InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                    if (inquiryChat == null || inquiryChat.getFrom().equalsIgnoreCase("assistant") || jList1.locationToIndex(e.getPoint()) == 0) {
                        editItem.setEnabled(false);
                        regenerateItem.setEnabled(false);
                        previousChat.setEnabled(false);
                        nextChat.setEnabled(false);
                    } else {
                        if (inquiryChat.getFrom().equalsIgnoreCase("user")) {
                            editItem.setEnabled(true);
                            regenerateItem.setEnabled(true);
                        } else {
                            editItem.setEnabled(false);
                            regenerateItem.setEnabled(false);
                        }
                        if (inquiryChat.getAlternateInquiryChatIds().isEmpty()) {
                            previousChat.setEnabled(false);
                            nextChat.setEnabled(false);
                        } else {
                            previousChat.setEnabled(!Objects.equals(inquiryChat.getId(), inquiryChat.getAlternateInquiryChatIds().get(0)));
                            nextChat.setEnabled(inquiryChat.getId() != null && !Objects.equals(inquiryChat.getId(), inquiryChat.getAlternateInquiryChatIds().get(inquiryChat.getAlternateInquiryChatIds().size() - 1)));
                        }
                    }
                    jPopupMenu1.show(jList1, e.getX(), e.getY());
                }
                if (selectedChat == -1) {
                    return;
                }
                if (inquiryChatViewer == null) {
                    inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
                }
                if (e.getClickCount() == 2) {
                    JBTextArea jBTextArea = (JBTextArea) inquiryChatViewer.getComponents()[1];
                    TextAreaWindow textAreaWindow = new TextAreaWindow(jBTextArea.getText());
                }
            }
        });
        jBScrollPane1 = new JBScrollPane(jList1);
        jBScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // Add JBTextArea
        promptInput = new JBTextArea();
        promptInput.setLineWrap(true); // Wrap text to next line
        promptInput.setWrapStyleWord(true);
        JBScrollPane jBScrollPane2 = new JBScrollPane(promptInput);

        // Add comment label
        //commentLabel = new JLabel("Ask questions regarding the change(s) above");
        //commentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        otherInquiriesButton = new JButton("Previous Inquiries");
        otherInquiriesButton.setFocusable(false);
        otherInquiriesButton.setHorizontalTextPosition(SwingConstants.CENTER);
        otherInquiriesButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        otherInquiriesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(otherInquiriesButton);

        newInquiryButton = new JButton("New Inquiry");
        newInquiryButton.setFocusable(false);
        newInquiryButton.setHorizontalTextPosition(SwingConstants.CENTER);
        newInquiryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        newInquiryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(newInquiryButton);

        discardInquiryButton = new JButton("Discard Inquiry");
        discardInquiryButton.setFocusable(false);
        discardInquiryButton.setHorizontalTextPosition(SwingConstants.CENTER);
        discardInquiryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        discardInquiryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(discardInquiryButton);

        jToolBar3 = new JToolBar();
        jToolBar3.setFloatable(false);
        jToolBar3.setBorderPainted(false);

        whatWasChangedButton = new JButton("\"What was changed?\"");
        whatWasChangedButton.setFocusable(false);
        whatWasChangedButton.setHorizontalTextPosition(SwingConstants.CENTER);
        whatWasChangedButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        whatWasChangedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar3.add(whatWasChangedButton);
        jToolBar3.addSeparator();

        whatDoesThisDoButton = new JButton("\"What does this do?\"");
        whatDoesThisDoButton.setFocusable(false);
        whatDoesThisDoButton.setHorizontalTextPosition(SwingConstants.CENTER);
        whatDoesThisDoButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        whatDoesThisDoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar3.add(whatDoesThisDoButton);
        jToolBar3.setVisible(false);

        otherInquiriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inquiryListViewer.updateInquiryList();
                codactorToolWindowService.openInquiryListViewerToolWindow();
            }
        });

        newInquiryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                historicalModificationListViewer.updateModificationList();
                codactorToolWindowService.openHistoricalModificationListViewerToolWindow();
            }
        });

        //CodeSnippetListViewer codeSnippetListViewer = this;
        discardInquiryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inquiry.getAfterCode() != null || !inquiry.getChats().isEmpty()) {
                    updateInquiryContents(new Inquiry(null, null, null, null, null, null, null, null, null, null));
                }
                /*ProvisionalModificationCustomizer provisionalModificationCustomizer = new ProvisionalModificationCustomizer(fileModification, codeModificationService, fileModificationTrackerService, aiTaskExecutor, codeSnippetListViewer, splitPaneService);
                provisionalModificationCustomizer.setVisible(true);*/
            }
        });

        whatWasChangedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                askInquiryQuestion(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), "What was changed in this modification?");
            }
        });

        whatDoesThisDoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                askInquiryQuestion(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), "What does this code do?");
            }
        });

        jButton2 = new JButton();
        //String userHome = System.getProperty("user.home");
        //InputStream stream = getClass().getResourceAsStream("/microphone_icon.png");
        jButton2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));
        //jButton2.setIcon(new javax.swing.ImageIcon(userHome + "/Codactor/resources/microphone_icon.png"));
        jButton2.setMaximumSize(new Dimension(80, 23));
        jButton2.setMinimumSize(new Dimension(80, 23));
        jButton2.setPreferredSize(new Dimension(80, 23));
        jButton2.setSize(new Dimension(80, 23));
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Robot robot = null;
                try {
                    robot = new Robot();
                    promptInput.requestFocusInWindow();
                    // Simulate a key press event for the CNTRL key.
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyRelease(KeyEvent.VK_CONTROL);

                    // Simulate another key press event for the CNTRL key.
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        jButton1 = new JButton();
        jButton1.setText("Ask");
        jButton1.setToolTipText("");
        jButton1.setMaximumSize(new Dimension(80, 23));
        jButton1.setMinimumSize(new Dimension(80, 23));
        jButton1.setPreferredSize(new Dimension(80, 23));
        jButton1.setSize(new Dimension(80, 23));
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (promptInput.getText().isEmpty()) return;
                if (!inquiry.getChats().isEmpty()){
                    InquiryChat previousInquiryChat = inquiry.getChats().stream()
                            .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                            .orElseThrow();
                    askContinuedQuestion(previousInquiryChat.getId(), promptInput.getText());
                } else if (inquiry.getSubjectRecordId() != null){
                    askInquiryQuestion(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), promptInput.getText());
                } else {
                    askNewGeneralInquiryQuestion(promptInput.getText());
                }
                componentResized((DefaultListModel<InquiryChatViewer>) jList1.getModel());
                promptInput.setText("");
            }
        });
        jPopupMenu1 = new JPopupMenu();

        editItem = new JMenuItem("Edit");
        regenerateItem = new JMenuItem("Regenerate");
        previousChat = new JMenuItem("Show Previous Chat");
        nextChat = new JMenuItem("Show Next Chat");
        autoGenerate = new JMenuItem("(Experimental) Auto-Generate");

        editItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
                InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                TextAreaWindow.TextAreaWindowActionListener textAreaWindowActionListener = new TextAreaWindow.TextAreaWindowActionListener() {
                    @Override
                    public void onOk(String text) {
                        editQuestion(inquiryChat.getId(), text);
                    }
                };
                new TextAreaWindow("Edit Message", inquiryChat.getMessage(), true, "Cancel", "Ok", textAreaWindowActionListener);
            }
        });
        regenerateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
                InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                editQuestion(inquiryChat.getId(), inquiryChat.getMessage());
            }
        });

        previousChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedChat > 0){
                    InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
                    InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                    int indexOfInquiryChat = inquiryChat.getAlternateInquiryChatIds().indexOf(inquiryChat.getId());
                    if (indexOfInquiryChat == -1) {
                        indexOfInquiryChat = inquiryChat.getAlternateInquiryChatIds().size();
                    }
                    String previousChatId = inquiryChat.getAlternateInquiryChatIds().get(indexOfInquiryChat - 1);
                    InquiryChat previousInquiryChat = inquiry.getChats().stream()
                            .filter(inquiryChatQuery -> (inquiryChatQuery.getId() != null && inquiryChatQuery.getId().equals(previousChatId)) || (inquiryChatQuery.getId() == null && previousChatId == null))
                            .findFirst()
                            .orElseThrow();
                    InquiryChat newerInquiryChat = findNextInquiryChat(inquiry.getChats(), previousInquiryChat);
                    if (newerInquiryChat != null) {
                        while (newerInquiryChat != null) {
                            previousInquiryChat = newerInquiryChat;
                            newerInquiryChat = findNextInquiryChat(inquiry.getChats(), previousInquiryChat);
                        }
                    }
                    updateInquiryContents(inquiry, previousInquiryChat);
                }
            }
        });
        nextChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedChat < jList1.getModel().getSize() - 1){
                    InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
                    InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                    int indexOfInquiryChat = inquiryChat.getAlternateInquiryChatIds().indexOf(inquiryChat.getId());
                    String nextChatId = inquiryChat.getAlternateInquiryChatIds().get(indexOfInquiryChat + 1);
                    InquiryChat nextInquiryChat = inquiry.getChats().stream()
                            .filter(inquiryChatQuery -> (inquiryChatQuery.getId() != null && inquiryChatQuery.getId().equals(nextChatId)) || (inquiryChatQuery.getId() == null && nextChatId == null))
                            .findFirst()
                            .orElseThrow();
                    InquiryChat newerInquiryChat = findNextInquiryChat(inquiry.getChats(), nextInquiryChat);
                    if (newerInquiryChat != null) {
                        while (newerInquiryChat != null) {
                            nextInquiryChat = newerInquiryChat;
                            newerInquiryChat = findNextInquiryChat(inquiry.getChats(), nextInquiryChat);
                        }
                    }
                    updateInquiryContents(inquiry, nextInquiryChat);
                }
            }
        });
        autoGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedChat > 0){
                    InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
                    InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                    FileChooserWindow fileChooserWindow = new FileChooserWindow(inquiry, inquiryChat, codeFileGeneratorService);
                    fileChooserWindow.setVisible(true);
                }
            }
        });

        jPopupMenu1.add(editItem);
        jPopupMenu1.add(regenerateItem);
        jPopupMenu1.addSeparator();
        jPopupMenu1.add(previousChat);
        jPopupMenu1.add(nextChat);
        jPopupMenu1.addSeparator();
        jPopupMenu1.add(autoGenerate);
        promptInputPanel = new JPanel();
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                        .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(promptInputPanel, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)); // Add JBTextArea to layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE, Short.MAX_VALUE) // Set size for jList1
                        .addGap(5)
                        .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(5)
                        // Add a gap of 20 between jList1 and JBTextArea
                        .addComponent(promptInputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Set size for JBTextArea
                        //.addComponent(doneLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE) // Add a label to mark end of code section
        );

        // Set the vertical layout
        GroupLayout promptInputPanelLayout = new GroupLayout(promptInputPanel);
        promptInputPanel.setLayout(promptInputPanelLayout);
        promptInputPanelLayout.setHorizontalGroup(
                promptInputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(promptInputPanelLayout.createSequentialGroup()
                                .addComponent(jBScrollPane2, GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(promptInputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0))
        );
        promptInputPanelLayout.setVerticalGroup(
                promptInputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(promptInputPanelLayout.createSequentialGroup()
                                .addGroup(promptInputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jBScrollPane2, 100, 100, 100)
                                        .addGroup(promptInputPanelLayout.createSequentialGroup()
                                                .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(jButton1, 64, 64, 64))
                                .addGap(0, 0, 0)))
        );
    }

    public void updateInquiryContents(Inquiry inquiry) {
        if (inquiry == null) {
            jList1.setModel(new DefaultListModel<>());
            return;
        }
        InquiryChat previousInquiryChat = null;
        if (!inquiry.getChats().isEmpty()) {
            previousInquiryChat = inquiry.getChats().stream()
                    .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                    .orElseThrow();
        }
        updateInquiryContents(inquiry, previousInquiryChat);
    }

    public void updateInquiryContents(Inquiry inquiry, InquiryChat previousInquiryChat) {
        this.inquiry = inquiry;
        if (inquiry == null) {
            jList1.setModel(new DefaultListModel<>());
            return;
        }
        DefaultListModel<InquiryChatViewer> model = new DefaultListModel<>();
        if (inquiry.getBeforeCode() == null && inquiry.getSubjectRecordId() != null) {
            inquiry.setBeforeCode("");
        }
        if (inquiry.getDescription() != null) {
            String text = inquiry.getModificationType() + ": " + inquiry.getDescription().trim();
            InquiryChatViewer descriptionViewer = new InquiryChatViewer(text, "User", InquiryChatType.INSTIGATOR_PROMPT);
            model.addElement(descriptionViewer);
            if (inquiry.getBeforeCode() != null) {
                String beforeCodeText = "```" + inquiry.getBeforeCode().trim() + "```";
                InquiryChatViewer beforeViewer = new InquiryChatViewer(beforeCodeText, "Before", InquiryChatType.CODE_SNIPPET);
                model.addElement(beforeViewer);
            }
            if (inquiry.getAfterCode() != null) {
                String afterCodeText = "```" + inquiry.getSubjectCode().trim() + "```";
                InquiryChatViewer afterViewer = new InquiryChatViewer(afterCodeText, "After", InquiryChatType.CODE_SNIPPET);
                model.addElement(afterViewer);
            }
        } else if (inquiry.getSubjectCode() != null) {
            String subjectCodeText = "```" + inquiry.getSubjectCode().trim() + "```";
            InquiryChatViewer subjectCodeViewer = new InquiryChatViewer(subjectCodeText, "Code", InquiryChatType.CODE_SNIPPET);
            model.addElement(subjectCodeViewer);
            String text = inquiry.getInitialQuestion().trim();
            InquiryChatViewer descriptionViewer = new InquiryChatViewer(text, "User", InquiryChatType.INSTIGATOR_PROMPT);
            model.addElement(descriptionViewer);
        }
        List<InquiryChat> finalizedChatList = new ArrayList<>();
        //Find the most recent chat by filtering by creationTimestamp
        List<InquiryChat> chatList = inquiry.getChats();
        if (!chatList.isEmpty()) {
            jToolBar3.setVisible(false);
            finalizedChatList.add(previousInquiryChat);
            String previousInquiryChatId = previousInquiryChat.getPreviousInquiryChatId();
            if (previousInquiryChatId != null) {
                while (previousInquiryChat != null) {
                    previousInquiryChat = findPreviousInquiryChat(chatList, previousInquiryChat);
                    if (previousInquiryChat != null) {
                        findAlternatesForInquiryChat(chatList, previousInquiryChat);
                        finalizedChatList.add(previousInquiryChat);
                    }
                }
            }
        } else if (inquiry.getDescription() != null) {
            jToolBar3.setVisible(true);
        }
        Collections.reverse(finalizedChatList);
        for (InquiryChat chat : finalizedChatList) {
            InquiryChatViewer chatViewer = new InquiryChatViewer(chat, chat.getFrom());
            model.addElement(chatViewer);
        }
        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //InquiryViewer.this.componentResized(model);
            }
        };
        jList1.getParent().addComponentListener(componentListener);
        jList1.setModel(model);
        JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public String getFileExtension(String filePath) {
        if (filePath == null) {
            return null;
        }
        return filePath.substring(filePath.lastIndexOf(".") + 1).trim();
    }

    private InquiryChat findPreviousInquiryChat(List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        return inquiryChats.stream()
                .filter(inquiryChatQuery ->  (inquiryChatQuery.getId() != null && inquiryChatQuery.getId().equals(inquiryChat.getPreviousInquiryChatId())))
                .findFirst()
                .orElse(null);
    }

    private InquiryChat findNextInquiryChat(List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        return inquiryChats.stream()
                .filter(inquiryChatQuery -> inquiryChatQuery.getPreviousInquiryChatId() != null && inquiryChatQuery.getPreviousInquiryChatId().equals(inquiryChat.getId()))
                .findFirst()
                .orElse(null);
    }

    private void findAlternatesForInquiryChat(List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        List<InquiryChat> alternateInquiryChats = inquiryChats.stream()
                .filter(inquiryChatQuery ->
                        (inquiryChatQuery.getPreviousInquiryChatId() != null && inquiryChatQuery.getPreviousInquiryChatId().equals(inquiryChat.getPreviousInquiryChatId()))
                                || (inquiryChatQuery.getPreviousInquiryChatId() == null && inquiryChat.getPreviousInquiryChatId() == null))
                .sorted(Comparator.comparing(InquiryChat::getCreationTimestamp))
                .collect(Collectors.toList());
        //Sorted by creationTimestamp
        List<String> alternateInquiryChatIds = alternateInquiryChats.stream()
                .map(InquiryChat::getId)
                .collect(Collectors.toList());
        inquiryChat.setAlternateInquiryChatIds(alternateInquiryChatIds);
    }

    private void askNewGeneralInquiryQuestion(String question) {
        InquiryChat temporaryChat = new InquiryChat(null, null, null, null, "User", question);
        inquiry.getChats().add(temporaryChat);
        updateInquiryContents(inquiry);
        LimitedSwingWorker worker = new LimitedSwingWorker(inquiryTaskExecutor) {
            @Override
            protected Void doInBackground() {
                setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                Inquiry inquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                if (inquiry != null) {
                    updateInquiryContents(inquiry);
                    componentResized((DefaultListModel<InquiryChatViewer>) jList1.getModel());
                }
                setLoadingChat(false);
                return null;
            }
        };
        worker.execute();
    }


    private void askInquiryQuestion(String subjectRecordId, RecordType recordType, String question) {
        jToolBar3.setVisible(false);
        InquiryChat temporaryChat = new InquiryChat(null, null, null, null, "User", question);
        inquiry.getChats().add(temporaryChat);
        updateInquiryContents(inquiry);
        LimitedSwingWorker worker = new LimitedSwingWorker(inquiryTaskExecutor) {
            @Override
            protected Void doInBackground() {
                setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                Inquiry inquiry = inquiryDao.createInquiry(subjectRecordId, recordType, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), new ArrayList<>());
                if (inquiry != null) {
                    updateInquiryContents(inquiry);
                    componentResized((DefaultListModel<InquiryChatViewer>) jList1.getModel());
                }
                setLoadingChat(false);
                return null;
            }
        };
        worker.execute();
    }

    private void askContinuedQuestion(String previousInquiryChatId, String question) {
        assert inquiry != null;
        InquiryChat inquiryChat = new InquiryChat(null, inquiry.getId(), inquiry.getFilePath(), previousInquiryChatId, "User", question);
        findAlternatesForInquiryChat(inquiry.getChats(), inquiryChat);
        inquiry.getChats().add(inquiryChat);
        updateInquiryContents(inquiry);
        LimitedSwingWorker worker = new LimitedSwingWorker(inquiryTaskExecutor) {
            @Override
            protected Void doInBackground() {
                setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                Inquiry response = inquiryDao.continueInquiry(previousInquiryChatId, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                inquiry.getChats().remove(inquiryChat);
                inquiry.getChats().addAll(response.getChats());
                updateInquiryContents(inquiry);
                componentResized((DefaultListModel<InquiryChatViewer>) jList1.getModel());
                setLoadingChat(false);
                return null;
            }
        };
        worker.execute();
    }

    private void editQuestion(String inquiryChatId, String newQuestion) {
        assert inquiry != null;
        InquiryChat inquiryChat = inquiry.getChats().stream().filter(inquiryChatQuery -> inquiryChatQuery.getId().equals(inquiryChatId)).findFirst().orElse(null);
        assert inquiryChat != null;
        assert inquiryChat.getPreviousInquiryChatId() != null;
        askContinuedQuestion(inquiryChat.getPreviousInquiryChatId(), newQuestion);
    }

    public void setHistoricalModificationListViewer(HistoricalModificationListViewer historicalModificationListViewer) {
        this.historicalModificationListViewer = historicalModificationListViewer;
    }

    public void setInquiryListViewer(InquiryListViewer inquiryListViewer) {
        this.inquiryListViewer = inquiryListViewer;
    }

    public void setLoadingChat(boolean loadingChat) {
        this.jButton1.setEnabled(!loadingChat);
        this.editItem.setEnabled(!loadingChat);
        this.regenerateItem.setEnabled(!loadingChat);
        this.nextChat.setEnabled(!loadingChat);
        this.previousChat.setEnabled(!loadingChat);
    }

    public void componentResized(DefaultListModel<InquiryChatViewer> previousModel) {
        DefaultListModel<InquiryChatViewer> newModel = new DefaultListModel<>();
        int newTotalHeight = 0;
        for (int i = 0; i < previousModel.size(); i++) {
            InquiryChatViewer chatViewer = previousModel.getElementAt(i);
            //chatViewer.setSize(Integer.MAX_VALUE, chatViewer.getHeight());
            JBTextArea chatDisplay = (JBTextArea) chatViewer.getComponent(1);
            int newHeight = 0;
            int newWidth = getWidth();
            if (chatViewer.getInquiryChatType() == InquiryChatType.CODE_SNIPPET) {
                newHeight += textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth, false);
            } else {
                newHeight += textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth, true);
            }
            Dimension preferredSize = new Dimension(newWidth, newHeight);
            chatDisplay.setPreferredSize(preferredSize);
            chatDisplay.setMaximumSize(preferredSize);
            chatDisplay.setSize(preferredSize);
            newModel.addElement(chatViewer);
            newTotalHeight += newHeight + chatViewer.getComponent(0).getHeight();
        }
        jList1.setPreferredSize(new Dimension(jBScrollPane1.getWidth() - 20, newTotalHeight));
        jList1.setModel(newModel);
        jBScrollPane1.setViewportView(jList1);
        JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public void componentResized() {
        componentResized((DefaultListModel<InquiryChatViewer>) jList1.getModel());
    }
}