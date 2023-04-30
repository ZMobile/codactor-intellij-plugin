package com.translator.view.viewer;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;
import com.translator.model.inquiry.InquiryChatType;
import com.translator.model.modification.RecordType;
import com.translator.service.context.PromptContextServiceImpl;
import com.translator.service.file.CodeFileGeneratorService;
import com.translator.service.inquiry.InquiryService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.dialog.MultiFileCreateDialog;
import com.translator.view.factory.PromptContextBuilderFactory;
import com.translator.view.menu.TextAreaWindow;
import com.translator.view.panel.FixedHeightPanel;
import com.translator.view.renderer.InquiryChatRenderer;

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
    private Project project;
    private Inquiry inquiry;
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
    private JBPopupMenu JBPopupMenu1;
    private JBMenuItem editItem;
    private JBMenuItem regenerateItem;
    private JBMenuItem previousChat;
    private JBMenuItem nextChat;
    private JBMenuItem autoGenerate;
    private CodactorToolWindowService codactorToolWindowService;
    private CodeFileGeneratorService codeFileGeneratorService;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private InquiryService inquiryService;
    private OpenAiModelService openAiModelService;
    private PromptContextBuilderFactory promptContextBuilderFactory;
    private HistoricalModificationListViewer historicalModificationListViewer;
    private InquiryListViewer inquiryListViewer;
    private JBTextArea promptInput;
    private int selectedChat;

    public InquiryViewer(Project project,
                         CodactorToolWindowService codactorToolWindowService,
                         CodeFileGeneratorService codeFileGeneratorService,
                         InquiryService inquiryService,
                         OpenAiModelService openAiModelService,
                         PromptContextBuilderFactory promptContextBuilderFactory) {
        this.project = project;
        this.codactorToolWindowService = codactorToolWindowService;
        this.codeFileGeneratorService = codeFileGeneratorService;
        this.textAreaHeightCalculatorService = new TextAreaHeightCalculatorServiceImpl();
        this.inquiryService = inquiryService;
        this.openAiModelService = openAiModelService;
        this.promptContextBuilderFactory = promptContextBuilderFactory;
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
                    Color highlightColor = Color.decode("#009688");
                        for (int i = 0; i < jList1.getModel().getSize(); i++) {
                            InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(i);
                            if (i == selectedIndex) {
                                for (Component component : inquiryChatViewer.getComponents()) {
                                    if (component instanceof JBTextArea) {
                                        JBTextArea selectedJBTextArea = (JBTextArea) component;
                                        //Highlight the whole text area
                                        try {
                                            selectedJBTextArea.getHighlighter().addHighlight(0, selectedJBTextArea.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
                                        } catch (BadLocationException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    } else if (component instanceof FixedHeightPanel) {
                                        FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                                        Editor editor = fixedHeightPanel.getEditor();
                                        editor.getMarkupModel().addRangeHighlighter(0, editor.getDocument().getTextLength(), HighlighterLayer.SELECTION - 1, new TextAttributes(null, highlightColor, null, EffectType.BOXED, Font.PLAIN), HighlighterTargetArea.EXACT_RANGE);
                                    }
                                }
                                continue;
                            }
                            for (Component component : inquiryChatViewer.getComponents()) {
                                if (component instanceof JBTextArea) {
                                    JBTextArea jBTextArea = (JBTextArea) component;
                                    jBTextArea.getHighlighter().removeAllHighlights();
                                } else if (component instanceof FixedHeightPanel) {
                                    FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                                    Editor editor = fixedHeightPanel.getEditor();
                                    editor.getMarkupModel().removeAllHighlighters();
                                }
                            }
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
                    int selectedIndex = jList1.locationToIndex(e.getPoint());
                    jList1.setSelectedIndex(selectedIndex);
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
                    JBPopupMenu1.show(jList1, e.getX(), e.getY());
                }
                if (selectedChat == -1) {
                    return;
                }
                if (inquiryChatViewer == null) {
                    inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
                }
                if (e.getClickCount() == 2) {
                    //Component component = inquiryChatViewer.getComponentAt(e.getPoint());
                    StringBuilder text = new StringBuilder();
                    boolean firstComponentCopied = false;
                    for (int i = 0; i < inquiryChatViewer.getComponents().length; i++) {
                        Component component1 = inquiryChatViewer.getComponents()[i];
                        if (firstComponentCopied) {
                            text.append("\n");
                            text.append("\n");
                        }
                        if (component1 instanceof JBTextArea) {
                            JBTextArea jBTextArea = (JBTextArea) component1;
                            text.append(jBTextArea.getText());
                            firstComponentCopied = true;
                        } else if (component1 instanceof FixedHeightPanel) {
                            FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component1;
                            Editor editor = fixedHeightPanel.getEditor();
                            text.append(editor.getDocument().getText());
                            firstComponentCopied = true;
                        }
                    }
                    new TextAreaWindow(text.toString());
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
                askInquiryQuestion(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), "What was changed in this modification?", inquiry.getFilePath());
            }
        });

        whatDoesThisDoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                askInquiryQuestion(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), "What does this code do?", inquiry.getFilePath());
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
                    askInquiryQuestion(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), promptInput.getText(), inquiry.getFilePath());
                } else {
                    askNewGeneralInquiryQuestion(promptInput.getText());
                }
                componentResized((DefaultListModel<InquiryChatViewer>) jList1.getModel());
                promptInput.setText("");
            }
        });
        JBPopupMenu1 = new JBPopupMenu();

        editItem = new JBMenuItem("Edit");
        regenerateItem = new JBMenuItem("Regenerate");
        previousChat = new JBMenuItem("Show Previous Chat");
        nextChat = new JBMenuItem("Show Next Chat");
        autoGenerate = new JBMenuItem("(Experimental) Auto-Generate");

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
                    MultiFileCreateDialog multiFileCreateDialog = new MultiFileCreateDialog(null, inquiryChat.getMessage(), openAiModelService, codactorToolWindowService, codeFileGeneratorService, new PromptContextServiceImpl(), promptContextBuilderFactory);
                    multiFileCreateDialog.setVisible(true);
                }
            }
        });

        JBPopupMenu1.add(editItem);
        JBPopupMenu1.add(regenerateItem);
        JBPopupMenu1.addSeparator();
        JBPopupMenu1.add(previousChat);
        JBPopupMenu1.add(nextChat);
        JBPopupMenu1.addSeparator();
        JBPopupMenu1.add(autoGenerate);
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
        int totalHeight = 0;
        if (inquiry.getDescription() != null) {
            String text = inquiry.getModificationType() + ": " + inquiry.getDescription().trim();
            InquiryChatViewer descriptionViewer = new InquiryChatViewer(inquiry.getFilePath(), text, "User", InquiryChatType.INSTIGATOR_PROMPT);
            model.addElement(descriptionViewer);
            if (inquiry.getBeforeCode() != null) {
                String beforeCodeText = "```" + inquiry.getBeforeCode().trim() + "```";
                InquiryChatViewer beforeViewer = new InquiryChatViewer(inquiry.getFilePath(), beforeCodeText, "Before", InquiryChatType.CODE_SNIPPET);
                model.addElement(beforeViewer);
            }
            if (inquiry.getAfterCode() != null) {
                String afterCodeText = "```" + inquiry.getAfterCode().trim() + "```";
                InquiryChatViewer afterViewer = new InquiryChatViewer(inquiry.getFilePath(), afterCodeText, "After", InquiryChatType.CODE_SNIPPET);
                model.addElement(afterViewer);
            }
        } else if (inquiry.getSubjectCode() != null) {
            String subjectCodeText = "```" + inquiry.getSubjectCode().trim() + "```";
            InquiryChatViewer subjectCodeViewer = new InquiryChatViewer(inquiry.getFilePath(), subjectCodeText, "Code", InquiryChatType.CODE_SNIPPET);
            model.addElement(subjectCodeViewer);
            String text = inquiry.getInitialQuestion().trim();
            InquiryChatViewer descriptionViewer = new InquiryChatViewer(inquiry.getFilePath(), text, "User", InquiryChatType.INSTIGATOR_PROMPT);
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
        for (int i = 0; i < model.size(); i++) {
            InquiryChatViewer chatViewer = model.getElementAt(i);
            //chatViewer.setSize(Integer.MAX_VALUE, chatViewer.getHeight());
            for (Component component : chatViewer.getComponents()) {
                if (component instanceof JBTextArea) {
                    JBTextArea chatDisplay = (JBTextArea) component;
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
                    totalHeight += newHeight + chatViewer.getComponent(0).getHeight();
                } else if (component instanceof FixedHeightPanel) {
                    FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                    totalHeight += fixedHeightPanel.getHeight();
                }
                totalHeight += chatViewer.getComponent(0).getHeight();
            }
        }
        jList1.setPreferredSize(new Dimension(jBScrollPane1.getWidth() - 20, totalHeight));
        jList1.getParent().addComponentListener(componentListener);
        jList1.setModel(model);
        jBScrollPane1.setViewportView(jList1);
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

    public void findAlternatesForInquiryChat(List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
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
        inquiryService.createGeneralInquiry(question);
    }


    private void askInquiryQuestion(String subjectRecordId, RecordType recordType, String question, String filePath) {
        jToolBar3.setVisible(false);
        inquiryService.createInquiry(subjectRecordId, recordType, question, filePath);
    }

    private void askContinuedQuestion(String previousInquiryChatId, String question) {
        assert inquiry != null;
        inquiryService.continueInquiry(previousInquiryChatId, question);
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
        SwingUtilities.invokeLater(() -> {
            DefaultListModel<InquiryChatViewer> newModel = new DefaultListModel<>();
            int newTotalHeight = 0;
            for (int i = 0; i < previousModel.size(); i++) {
                InquiryChatViewer chatViewer = previousModel.getElementAt(i);
                for (Component component : chatViewer.getComponents()) {
                    if (component instanceof JBTextArea) {
                        JBTextArea chatDisplay = (JBTextArea) component;
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
                        newTotalHeight += newHeight + chatViewer.getComponent(0).getHeight();
                    } else if (component instanceof FixedHeightPanel) {
                        FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                        newTotalHeight += fixedHeightPanel.getHeight();
                    }
                    newTotalHeight += chatViewer.getComponent(0).getHeight();
                }
                newModel.addElement(chatViewer);
            }
            jList1.setPreferredSize(new Dimension(jBScrollPane1.getWidth() - 20, newTotalHeight));
            jList1.setModel(newModel);
            jBScrollPane1.setViewportView(jList1);
        });
    }

    public void componentResized() {
        componentResized((DefaultListModel<InquiryChatViewer>) jList1.getModel());
    }

    public Inquiry getInquiry() {
        return inquiry;
    }
}