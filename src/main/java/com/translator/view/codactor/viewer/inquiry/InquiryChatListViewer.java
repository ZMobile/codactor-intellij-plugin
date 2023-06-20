package com.translator.view.codactor.viewer.inquiry;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.inquiry.InquiryChatType;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import com.translator.view.codactor.menu.TextAreaWindow;
import com.translator.view.codactor.panel.FixedHeightPanel;
import com.translator.view.codactor.renderer.InquiryChatRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InquiryChatListViewer extends JPanel {
    private Inquiry inquiry;
    private InquiryViewer inquiryViewer;
    private JList<InquiryChatViewer> inquiryChatList;
    private JBScrollPane jBScrollPane;
    private ListSelectionListener listSelectionListener;
    private int selectedChat = -1;
    private int lastSelectedChat = -1;
    private JToolBar jToolBar;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private PromptContextServiceFactory promptContextServiceFactory;
    private MultiFileCreateDialogFactory multiFileCreateDialogFactory;

    public InquiryChatListViewer(Inquiry inquiry,
                                 InquiryViewer inquiryViewer,
                                 TextAreaHeightCalculatorService textAreaHeightCalculatorService,
                                 PromptContextServiceFactory promptContextServiceFactory,
                                 MultiFileCreateDialogFactory multiFileCreateDialogFactory) {
        this.inquiry = inquiry;
        this.inquiryViewer = inquiryViewer;
        this.textAreaHeightCalculatorService = textAreaHeightCalculatorService;
        this.promptContextServiceFactory = promptContextServiceFactory;
        this.multiFileCreateDialogFactory = multiFileCreateDialogFactory;
        initComponents();
        addComponents();
    }

    private void initComponents() {
        inquiryChatList = new JList<>();
        inquiryChatList.setModel(new DefaultListModel<>());
        inquiryChatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inquiryChatList.setCellRenderer(new InquiryChatRenderer());

        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = inquiryChatList.getSelectedIndex();
                    if (selectedIndex == -1) {
                        return;
                    }
                    selectedChat = selectedIndex;
                    updateSelectionHighlighting();
                }
            }
        };

        inquiryChatList.addListSelectionListener(listSelectionListener);

        inquiryChatList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C && (e.isControlDown() || e.isMetaDown())) {
                    if (selectedChat != -1) {
                        InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
                        JBTextArea jBTextArea = (JBTextArea) inquiryChatViewer.getComponents()[1];
                        StringSelection selection = new StringSelection(jBTextArea.getText());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, null);
                    }
                }
            }
        });
        JBPopupMenu jBPopupMenu1 = new JBPopupMenu();

        JBMenuItem editItem = new JBMenuItem("Edit");
        JBMenuItem regenerateItem = new JBMenuItem("Regenerate");
        JBMenuItem previousChat = new JBMenuItem("Show Previous Chat");
        JBMenuItem nextChat = new JBMenuItem("Show Next Chat");
        JBMenuItem autoGenerate = new JBMenuItem("(Experimental) Auto-Generate");

        editItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
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
                InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
                InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                editQuestion(inquiryChat.getId(), inquiryChat.getMessage());
            }
        });

        previousChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedChat > 0){
                    InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
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
                if (selectedChat < inquiryChatList.getModel().getSize() - 1){
                    InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
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
                    InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
                    InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                    PromptContextService promptContextService = promptContextServiceFactory.create();
                    MultiFileCreateDialog multiFileCreateDialog = multiFileCreateDialogFactory.create(null, inquiryChat.getMessage(), promptContextService);
                    multiFileCreateDialog.setVisible(true);
                }
            }
        });

        jBPopupMenu1.add(editItem);
        jBPopupMenu1.add(regenerateItem);
        jBPopupMenu1.addSeparator();
        jBPopupMenu1.add(previousChat);
        jBPopupMenu1.add(nextChat);
        jBPopupMenu1.addSeparator();
        jBPopupMenu1.add(autoGenerate);

        inquiryChatList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                InquiryChatViewer inquiryChatViewer = null;
                if (e.getButton() == MouseEvent.BUTTON3) {
                    inquiryChatViewer = inquiryChatList.getModel().getElementAt(inquiryChatList.locationToIndex(e.getPoint()));
                    int selectedIndex = inquiryChatList.locationToIndex(e.getPoint());
                    inquiryChatList.setSelectedIndex(selectedIndex);
                    InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                    if (inquiryChat == null || inquiryChat.getFrom().equalsIgnoreCase("assistant") || inquiryChatList.locationToIndex(e.getPoint()) == 0) {
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
                    jBPopupMenu1.show(inquiryChatList, e.getX(), e.getY());
                }
                if (selectedChat == -1) {
                    return;
                }
                if (inquiryChatViewer == null) {
                    inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
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
                } else if (selectedChat == lastSelectedChat) {
                    inquiryChatList.clearSelection();
                    inquiryChatList.setSelectedIndex(-1);
                    selectedChat = -1;
                    updateSelectionHighlighting();
                }
                lastSelectedChat = selectedChat;
            }
        });
        jToolBar = new JToolBar();
        jToolBar.setFloatable(false);
        jToolBar.setBorderPainted(false);

        JButton whatWasChangedButton = new JButton("\"What was changed?\"");
        whatWasChangedButton.setFocusable(false);
        whatWasChangedButton.setHorizontalTextPosition(SwingConstants.CENTER);
        whatWasChangedButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        whatWasChangedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar.add(whatWasChangedButton);
        jToolBar.addSeparator();

        JButton whatDoesThisDoButton = new JButton("\"What does this do?\"");
        whatDoesThisDoButton.setFocusable(false);
        whatDoesThisDoButton.setHorizontalTextPosition(SwingConstants.CENTER);
        whatDoesThisDoButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        whatDoesThisDoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar.add(whatDoesThisDoButton);
        jToolBar.setVisible(false);
        jBScrollPane = new JBScrollPane(inquiryChatList);
        jBScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(jBScrollPane, BorderLayout.CENTER);
    }

    private void addComponents() {
        setLayout(new BorderLayout());
        add(new JBScrollPane(inquiryChatList), BorderLayout.CENTER);
    }

    private void updateSelectionHighlighting() {
        // Update selection highlighting logic
        // ...
    }

    public void updateInquiryContents(Inquiry inquiry) {
        if (inquiry == null) {
            inquiryChatList.setModel(new DefaultListModel<>());
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
            inquiryChatList.setModel(new DefaultListModel<>());
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
            jToolBar.setVisible(false);
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
            jToolBar.setVisible(true);
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
        inquiryChatList.setPreferredSize(new Dimension(jBScrollPane.getWidth() - 20, totalHeight));
        inquiryChatList.getParent().addComponentListener(componentListener);
        inquiryChatList.setModel(model);
        jBScrollPane.setViewportView(inquiryChatList);
    }

    private void editQuestion(String inquiryChatId, String newQuestion) {
        assert inquiry != null;
        InquiryChat inquiryChat = inquiry.getChats().stream().filter(inquiryChatQuery -> inquiryChatQuery.getId().equals(inquiryChatId)).findFirst().orElse(null);
        assert inquiryChat != null;
        assert inquiryChat.getPreviousInquiryChatId() != null;
        inquiryViewer.askContinuedQuestion(inquiryChat.getPreviousInquiryChatId(), newQuestion);
    }

    private InquiryChat findPreviousInquiryChat(java.util.List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        return inquiryChats.stream()
                .filter(inquiryChatQuery ->  (inquiryChatQuery.getId() != null && inquiryChatQuery.getId().equals(inquiryChat.getPreviousInquiryChatId())))
                .findFirst()
                .orElse(null);
    }

    private InquiryChat findNextInquiryChat(java.util.List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        return inquiryChats.stream()
                .filter(inquiryChatQuery -> inquiryChatQuery.getPreviousInquiryChatId() != null && inquiryChatQuery.getPreviousInquiryChatId().equals(inquiryChat.getId()))
                .findFirst()
                .orElse(null);
    }

    public void findAlternatesForInquiryChat(java.util.List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        java.util.List<InquiryChat> alternateInquiryChats = inquiryChats.stream()
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
}