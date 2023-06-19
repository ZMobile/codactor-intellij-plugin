package com.translator.view.uml.node.dialog.prompt;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.inquiry.InquiryChatType;
import com.translator.model.uml.node.PromptNode;
import com.translator.model.uml.prompt.Prompt;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.service.uml.node.PromptHighlighterService;
import com.translator.view.codactor.menu.TextAreaWindow;
import com.translator.view.codactor.panel.FixedHeightPanel;
import com.translator.view.codactor.renderer.InquiryChatRenderer;
import com.translator.view.codactor.viewer.inquiry.InquiryChatViewer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PromptViewer extends JPanel {
    private PromptNodeDialog promptNodeDialog;
    private PromptNode promptNode;
    private BackgroundTaskMapperService backgroundTaskMapperService;
    private List<InquiryChat> inquiryChats;
    private HistoricalContextObjectHolder historicalContextObjectHolder;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private PromptHighlighterService promptHighlighterService;
    private JBList<InquiryChatViewer> promptList;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JBScrollPane jBScrollPane1;
    private JBLabel viewerLabel;
    private JButton addButton;
    private JButton removeButton;
    private int selectedChat;
    private int lastSelectedChat;
    private Gson gson;

    public PromptViewer(PromptNodeDialog promptNodeDialog,
                        PromptHighlighterService promptHighlighterService,
                        Gson gson) {
        this.promptNodeDialog = promptNodeDialog;
        this.promptHighlighterService = promptHighlighterService;
        this.promptNode = promptNodeDialog.getPromptNode();
        this.inquiryChats = new ArrayList<>();
        this.textAreaHeightCalculatorService = new TextAreaHeightCalculatorServiceImpl();
        this.historicalContextObjectHolder = null;
        this.selectedChat = -1;
        this.lastSelectedChat = -1;
        this.gson = gson;
        initComponents();
    }

    private void initComponents() {
        promptList = new JBList<>();
        //promptList.setMaximumSize(new Dimension(getWidth(), Integer.MAX_VALUE));
        promptList.setModel(new DefaultListModel<>());
        promptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        promptList.setCellRenderer(new InquiryChatRenderer());
        promptList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = promptList.getSelectedIndex();
                    if (selectedIndex == -1) {
                        return;
                    }
                    selectedChat = selectedIndex;
                    updateSelectionHighlighting();
                }
            }
        });
        promptList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //If Cntrl + C is pressed (or command for mac users)
                if (e.getKeyCode() == KeyEvent.VK_C && (e.isControlDown() || e.isMetaDown())) {
                    if (selectedChat != -1) {
                        //Copy the selected text to the clipboard
                        InquiryChatViewer inquiryChatViewer = promptList.getModel().getElementAt(selectedChat);
                        JBTextArea jBTextArea = (JBTextArea) inquiryChatViewer.getComponents()[1];
                        StringSelection selection = new StringSelection(jBTextArea.getText());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, null);
                    }
                }
            }
        });
        promptList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (selectedChat == -1) {
                        return;
                    }
                    InquiryChatViewer inquiryChatViewer = promptList.getModel().getElementAt(selectedChat);
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
                    if (promptNode.isProcessed()) {
                        new TextAreaWindow(text.toString());
                    } else {
                        TextAreaWindow.TextAreaWindowActionListener textAreaWindowActionListener = new TextAreaWindow.TextAreaWindowActionListener() {
                            @Override
                            public void onOk(String text) {
                                promptNode.getPromptList().get(selectedChat).setPrompt(text);
                                promptNodeDialog.getPromptNodeFigure().setMetadata(gson.toJson(promptNode));
                                updatePromptChatContents(promptNode.getPromptList());
                                promptHighlighterService.highlightPrompts(promptNodeDialog);
                            }
                        };
                        new TextAreaWindow("Edit Prompt", text.toString(), true, "Cancel", "Ok", textAreaWindowActionListener);
                    }
                } else if (selectedChat == lastSelectedChat) {
                    promptList.clearSelection();
                    promptList.setSelectedIndex(-1);
                    selectedChat = -1;
                    updateSelectionHighlighting();
                }
                lastSelectedChat = selectedChat;
            }
        });
        jBScrollPane1 = new JBScrollPane(promptList);
        jBScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        viewerLabel = new JBLabel("Prompts");
        viewerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        viewerLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        viewerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        jToolBar2.add(viewerLabel);

        jToolBar3 = new JToolBar();
        jToolBar3.setFloatable(false);
        jToolBar3.setBorderPainted(false);

        removeButton = new JButton("-");
        removeButton.setPreferredSize(new Dimension(50, 32));
        removeButton.setFocusable(false);
        removeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        removeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = promptList.getSelectedIndex();
                if (selectedIndex == -1) {
                    selectedIndex = 0;
                }
                promptNode.getPromptList().remove(selectedIndex);
                promptNodeDialog.getPromptNodeFigure().setMetadata(gson.toJson(promptNode));
                updatePromptChatContents(promptNode.getPromptList());
            }
        });
        jToolBar3.add(removeButton);
        addButton = new JButton("+");
        addButton.setPreferredSize(new Dimension(50, 32));
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(SwingConstants.CENTER);
        addButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Prompt prompt = new Prompt(promptNode.getId(), "Insert prompt here");
                promptNode.getPromptList().add(prompt);
                promptNodeDialog.getPromptNodeFigure().setMetadata(gson.toJson(promptNode));
                updatePromptChatContents(promptNode.getPromptList());
                //promptHighlighterService.highlightPrompts(promptNodeDialog);
            }
        });
        jToolBar3.add(addButton);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, promptList.getHeight(), Short.MAX_VALUE)); // Set size for promptList// Add a gap of 20 between promptList and JBTextArea
    }

    public void updateSelectionHighlighting() {
        Color highlightColor = Color.decode("#009688");
        for (int i = 0; i < promptList.getModel().getSize(); i++) {
            InquiryChatViewer inquiryChatViewer = promptList.getModel().getElementAt(i);
            if (i == selectedChat) {
                for (Component component : inquiryChatViewer.getComponents()) {
                    if (component instanceof JBTextArea) {
                        JBTextArea selectedJBTextArea = (JBTextArea) component;
                        selectedJBTextArea.getHighlighter().removeAllHighlights();
                        //Highlight the whole text area
                        try {
                            selectedJBTextArea.getHighlighter().addHighlight(0, selectedJBTextArea.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
                        } catch (BadLocationException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (component instanceof FixedHeightPanel) {
                        FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                        Editor editor = fixedHeightPanel.getEditor();
                        editor.getMarkupModel().removeAllHighlighters();
                        editor.getMarkupModel().addRangeHighlighter(0, editor.getDocument().getTextLength(), HighlighterLayer.SELECTION - 1, new TextAttributes(null, highlightColor, null, EffectType.BOXED, Font.PLAIN), HighlighterTargetArea.EXACT_RANGE);
                    }
                }
                promptHighlighterService.highlightPromptsWithoutRemoval(promptNodeDialog);
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
            promptHighlighterService.highlightPromptsWithoutRemoval(promptNodeDialog);
        }
    }

    public void updateInquiryChatContents(java.util.List<InquiryChat> inquiryChats) {
        this.inquiryChats = inquiryChats;
        ApplicationManager.getApplication().invokeLater(() -> {
                if (inquiryChats == null) {
                    promptList.setModel(new DefaultListModel<>());
                    return;
                }
                int totalHeight = 0;
                DefaultListModel<InquiryChatViewer> model = new DefaultListModel<>();
                for (InquiryChat inquiryChat : inquiryChats) {
                    InquiryChatViewer chatViewer = new InquiryChatViewer(inquiryChat);
                    model.addElement(chatViewer);
                    for (Component component : chatViewer.getComponents()) {
                        if (component instanceof JBTextArea) {
                            JBTextArea chatDisplay = (JBTextArea) component;
                            int newHeight = 0;
                            int newWidth = getWidth();
                            if (inquiryChat.getInquiryChatType() == InquiryChatType.CODE_SNIPPET) {
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
                promptList.setPreferredSize(new Dimension(jBScrollPane1.getWidth() - 20, totalHeight));
                promptList.setModel(model);
                ComponentListener componentListener = new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        PromptViewer.this.componentResized(model);
                    }
                };
                promptList.getParent().addComponentListener(componentListener);
                jBScrollPane1.setViewportView(promptList);
                promptHighlighterService.highlightPrompts(promptNodeDialog);
            //JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
                //vertical.setValue(vertical.getMaximum() - vertical.getVisibleAmount());
            }
        );
    }

    public void updatePromptChatContents(List<Prompt> prompts) {
        inquiryChats.clear();
        for (Prompt prompt : prompts) {
            inquiryChats.add(new InquiryChat(null, null, null, null, "User", prompt.getPrompt(), null));
        }
        updateInquiryChatContents(inquiryChats);
    }

    public void updateChatContents(List<Prompt> prompts, Map<Prompt, InquiryChat> promptAnswerMap) {
        inquiryChats.clear();
        for (Prompt prompt : prompts) {
            inquiryChats.add(new InquiryChat(null, null, null, null, "User", prompt.getPrompt(), null));
            if (promptAnswerMap.containsKey(prompt)) {
                InquiryChat answer = promptAnswerMap.get(prompt);
                inquiryChats.add(answer);
            }
        }
        updateInquiryChatContents(inquiryChats);
    }

    public void updateChatContents(PromptNode promptNode) {
        inquiryChats.clear();
        Inquiry inquiry = promptNode.getActiveInquiryList().get(0);
        //inquiry.getChats().get(0).setAlternateInquiryChatIds();
        updateInquiryChatContents(inquiry.getChats());
    }


    private InquiryChat findPreviousInquiryChat(List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        return inquiryChats.stream()
                .filter(inquiryChatQuery -> inquiryChatQuery.getId().equals(inquiryChat.getPreviousInquiryChatId()))
                .findFirst()
                .orElse(null);
    }

    public void componentResized(DefaultListModel<InquiryChatViewer> previousModel) {
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
        promptList.setPreferredSize(new Dimension(jBScrollPane1.getWidth() - 20, newTotalHeight));
        promptList.setModel(newModel);
        jBScrollPane1.setViewportView(promptList);
    }

    public List<InquiryChatViewer> getPrompts() {
        List<InquiryChatViewer> prompts = new ArrayList<>();
        for (int i = 0; i < promptList.getModel().getSize(); i++) {
            prompts.add(promptList.getModel().getElementAt(i));
        }
        return prompts;
    }

    public JBList<InquiryChatViewer> getPromptJList() {
        return promptList;
    }
}
