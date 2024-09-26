package com.translator.view.codactor.viewer.inquiry;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.chat.InquiryChatType;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.ai.chat.functions.InquiryChatListFunctionCallCompressorService;
import com.translator.service.codactor.ai.chat.functions.InquiryFunctionCallProcessorService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import com.translator.view.codactor.menu.TextAreaWindow;
import com.translator.view.codactor.panel.FixedHeightPanel;
import com.translator.view.codactor.renderer.InquiryChatRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class InquiryChatListViewer extends JPanel {
    private Gson gson;
    private Inquiry inquiry;
    private InquiryViewer inquiryViewer;
    private JPanel inquiryChatPanel;
    private List<InquiryChatViewer> inquiryChatViewers;
    private InquiryChatViewer selectedChatViewer;
    private JBScrollPane jBScrollPane;
    private JBPopupMenu jBPopupMenu;
    private ListSelectionListener listSelectionListener;
    private JBMenuItem editItem;
    private JBMenuItem regenerateItem;
    private JBMenuItem previousChat;
    private JBMenuItem nextChat;
    private JBMenuItem autoGenerate;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private OpenAiModelService openAiModelService;
    private InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService;
    private InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService;
    private PromptContextServiceFactory promptContextServiceFactory;
    private MultiFileCreateDialogFactory multiFileCreateDialogFactory;
    private boolean debugView;

    public InquiryChatListViewer(Gson gson,
                                 InquiryViewer inquiryViewer,
                                 TextAreaHeightCalculatorService textAreaHeightCalculatorService,
                                 PromptContextServiceFactory promptContextServiceFactory,
                                 OpenAiModelService openAiModelService,
                                 CodactorToolWindowService codactorToolWindowService,
                                 InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService,
                                 InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService,
                                 MultiFileCreateDialogFactory multiFileCreateDialogFactory) {
        this.gson = gson;
        this.inquiryViewer = inquiryViewer;
        this.textAreaHeightCalculatorService = textAreaHeightCalculatorService;
        this.inquiryChatListFunctionCallCompressorService = inquiryChatListFunctionCallCompressorService;
        this.promptContextServiceFactory = promptContextServiceFactory;
        this.openAiModelService = openAiModelService;
        this.inquiryFunctionCallProcessorService = inquiryFunctionCallProcessorService;
        this.multiFileCreateDialogFactory = multiFileCreateDialogFactory;
        this.debugView = false;
        initComponents();
    }

    private void initComponents() {
        //inquiryChatList = new JList<>();
        inquiryChatPanel = new JPanel();
        inquiryChatPanel.setLayout(new GridBagLayout());
        inquiryChatPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        inquiryChatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        jBScrollPane = new JBScrollPane(inquiryChatPanel);
        jBScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        initContextMenu();
        add(jBScrollPane, BorderLayout.CENTER);
    }

    private void initContextMenu() {
        jBPopupMenu = new JBPopupMenu();

        editItem = new JBMenuItem("Edit");
        regenerateItem = new JBMenuItem("Regenerate");
        previousChat = new JBMenuItem("Show Previous Chat");
        nextChat = new JBMenuItem("Show Next Chat");
        autoGenerate = new JBMenuItem("(Experimental) Auto-Generate");

        // Add action listeners as before...
        editItem.addActionListener(e -> {
            if (selectedChatViewer == null && !inquiryChatViewers.isEmpty()) {
                selectedChatViewer = inquiryChatViewers.get(0);
            }
            InquiryChat inquiryChat = selectedChatViewer.getInquiryChat();
            TextAreaWindow.TextAreaWindowActionListener textAreaWindowActionListener = new TextAreaWindow.TextAreaWindowActionListener() {
                @Override
                public void onOk(String text) {
                    editQuestion(inquiryChat.getId(), text);
                }
            };
            new TextAreaWindow("Edit Message", inquiryChat.getMessage(), true, "Cancel", "Ok", textAreaWindowActionListener);
        });
        regenerateItem.addActionListener(e -> {
            InquiryChat inquiryChat = selectedChatViewer.getInquiryChat();
            editQuestion(inquiryChat.getId(), inquiryChat.getMessage());
        });

        previousChat.addActionListener(e -> {
            if (inquiryChatViewers.indexOf(selectedChatViewer) > 0){
                InquiryChat inquiryChat = selectedChatViewer.getInquiryChat();
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
        });
        nextChat.addActionListener(e -> {
            int indexOfSelectedChat = inquiryChatViewers.indexOf(selectedChatViewer);
            if (indexOfSelectedChat != -1 && indexOfSelectedChat < inquiryChatViewers.size() - 1){
                InquiryChat inquiryChat = selectedChatViewer.getInquiryChat();
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
        });
        autoGenerate.addActionListener(e -> {
            if (inquiryChatViewers.indexOf(selectedChatViewer) > 0){
                InquiryChat inquiryChat = selectedChatViewer.getInquiryChat();
                PromptContextService promptContextService = promptContextServiceFactory.create();
                MultiFileCreateDialog multiFileCreateDialog = multiFileCreateDialogFactory.create(null, inquiryChat.getMessage(), promptContextService, openAiModelService);
                multiFileCreateDialog.setVisible(true);
            }
        });
        jBPopupMenu.add(editItem);
        jBPopupMenu.add(regenerateItem);
        jBPopupMenu.addSeparator();
        jBPopupMenu.add(previousChat);
        jBPopupMenu.add(nextChat);
        jBPopupMenu.addSeparator();
        jBPopupMenu.add(autoGenerate);

    }

    private void handleSelection(InquiryChatViewer chatViewer, MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            // Handle right-click for context menu
            showContextMenu(chatViewer, e);
        } else {
            // Handle selection
            selectChatViewer(chatViewer);
            if (e.getClickCount() == 2) {
                // Handle double-click action
                String text = convertInquiryChatViewerToText(chatViewer);
                new TextAreaWindow(text);
            }
        }
    }

    private void showContextMenu(InquiryChatViewer chatViewer, MouseEvent e) {
        selectChatViewer(chatViewer);

        InquiryChat inquiryChat = chatViewer.getInquiryChat();
        // Update menu items' enabled state based on inquiryChat...

        jBPopupMenu.show(chatViewer, e.getX(), e.getY());
    }

    private void selectChatViewer(InquiryChatViewer chatViewer) {
        if (selectedChatViewer != null) {
            selectedChatViewer = null;
        }
        //chatViewer.setSelected(true);
        chatViewer.requestFocusInWindow();
        selectedChatViewer = chatViewer;
    }

    private void deselectChatViewer() {
        if (selectedChatViewer != null) {
            //selectedChatViewer.setSelected(false);
            selectedChatViewer = null;
        }
    }

    private String convertInquiryChatViewerToText(InquiryChatViewer inquiryChatViewer) {
        StringBuilder text = new StringBuilder();
        boolean firstComponentCopied = false;
        for (int i = 0; i < inquiryChatViewer.getComponents().length; i++) {
            Component component1 = inquiryChatViewer.getComponents()[i];
            if (firstComponentCopied) {
                text.append("\n");
                text.append("\n");
            }
            if (component1 instanceof JTextPane) {
                JTextPane jTextPane = (JTextPane) component1;
                HTMLDocument doc = (HTMLDocument)jTextPane.getDocument();
                int length = doc.getLength();
                try {
                    text.append(doc.getText(0, length));
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
                firstComponentCopied = true;
            } else if (component1 instanceof FixedHeightPanel) {
                FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component1;
                for (Component component2 : fixedHeightPanel.getComponents()) {
                    if (component2 instanceof JLabel) {
                        JLabel jLabel = (JLabel) component2;
                        text.append(jLabel.getText());
                        text.append("\n");
                    }
                }
                Editor editor = fixedHeightPanel.getEditor();
                if (editor != null) {
                    text.append(editor.getDocument().getText());
                    firstComponentCopied = true;
                }
            } else if (component1 instanceof JToolBar) {
                JToolBar jToolBar = (JToolBar) component1;
                for (Component component2 : jToolBar.getComponents()) {
                    if (component2 instanceof JLabel) {
                        JLabel jLabel = (JLabel) component2;
                        text.append(jLabel.getText());
                        text.append("\n");
                    }
                }
            }
        }
        return text.toString();
    }

    private void updateSelectionHighlighting() {
        Color highlightColor = Color.decode("#009688");
        for (InquiryChatViewer inquiryChatViewer : inquiryChatViewers) {
            //InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(i);
            if (inquiryChatViewer.equals(selectedChatViewer)) {
                for (Component component : inquiryChatViewer.getComponents()) {
                    if (component instanceof JTextPane) {
                        JTextPane selectedJTextPane = (JTextPane) component;
                        //Highlight the whole text area
                        try {
                            selectedJTextPane.getHighlighter().addHighlight(0, selectedJTextPane.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
                        } catch (BadLocationException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (component instanceof FixedHeightPanel) {
                        FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                        Editor editor = fixedHeightPanel.getEditor();
                        if (editor != null) {
                            editor.getMarkupModel().addRangeHighlighter(0, editor.getDocument().getText().length(), HighlighterLayer.SELECTION - 1, new TextAttributes(null, highlightColor, null, EffectType.BOXED, Font.PLAIN), HighlighterTargetArea.EXACT_RANGE);
                        }
                    }
                }
                continue;
            }
            for (Component component : inquiryChatViewer.getComponents()) {
                if (component instanceof JTextPane) {
                    JTextPane jBTextArea = (JTextPane) component;
                    jBTextArea.getHighlighter().removeAllHighlights();
                } else if (component instanceof FixedHeightPanel) {
                    FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                    Editor editor = fixedHeightPanel.getEditor();
                    if (editor != null) {
                        editor.getMarkupModel().removeAllHighlighters();
                    }
                }
            }
        }
    }

    public void updateInquiryContents(Inquiry inquiry) {
        if (inquiry == null) {
            // Clear the panel
            inquiryChatPanel.removeAll();
            inquiryChatPanel.revalidate();
            inquiryChatPanel.repaint();
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
        this.inquiryViewer.setInquiry(inquiry);
        if (this.inquiryViewer.getInquiryChatBoxViewer() != null) {
            this.inquiryViewer.getInquiryChatBoxViewer().setInquiry(inquiry);
        }
        if (inquiry == null) {
            inquiryChatPanel.removeAll();
            inquiryChatPanel.revalidate();
            inquiryChatPanel.repaint();
            return;
        }

        inquiryChatPanel.removeAll();

        // Create GridBagConstraints instance
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0; // Start at the top
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // Components should not grow vertically
        // Initialize the list to hold InquiryChatViewer instances
        inquiryChatViewers = new ArrayList<>();

        // Build your InquiryChatViewer instances and add them to the list
        if (inquiry.getBeforeCode() == null && inquiry.getSubjectRecordId() != null) {
            inquiry.setBeforeCode("");
        }

        if (inquiry.getDescription() != null) {
            String text = inquiry.getModificationType() + ": " + inquiry.getDescription().trim();
            InquiryChatViewer descriptionViewer = new InquiryChatViewer.Builder()
                    .withFilePath(inquiry.getFilePath())
                    .withMessage(text)
                    .withHeaderString("User")
                    .withInquiryChatType(InquiryChatType.INSTIGATOR_PROMPT)
                    .build();
            inquiryChatViewers.add(descriptionViewer);

            if (inquiry.getBeforeCode() != null) {
                String beforeCodeText = "```" + inquiry.getBeforeCode() + "```";
                InquiryChatViewer beforeViewer = new InquiryChatViewer.Builder()
                        .withFilePath(inquiry.getFilePath())
                        .withMessage(beforeCodeText)
                        .withHeaderString("Before")
                        .withInquiryChatType(InquiryChatType.CODE_SNIPPET)
                        .build();
                inquiryChatViewers.add(beforeViewer);
            }

            if (inquiry.getAfterCode() != null) {
                String afterCodeText = "```" + inquiry.getAfterCode() + "```";
                InquiryChatViewer afterViewer = new InquiryChatViewer.Builder()
                        .withFilePath(inquiry.getFilePath())
                        .withMessage(afterCodeText)
                        .withHeaderString("After")
                        .withInquiryChatType(InquiryChatType.CODE_SNIPPET)
                        .build();
                inquiryChatViewers.add(afterViewer);
            }
        } else if (inquiry.getSubjectCode() != null) {
            String subjectCodeText = "```" + inquiry.getSubjectCode() + "```";
            InquiryChatViewer subjectCodeViewer = new InquiryChatViewer.Builder()
                    .withFilePath(inquiry.getFilePath())
                    .withMessage(subjectCodeText)
                    .withHeaderString("Code")
                    .withInquiryChatType(InquiryChatType.CODE_SNIPPET)
                    .build();
            inquiryChatViewers.add(subjectCodeViewer);

            String text = inquiry.getInitialQuestion().trim();
            InquiryChatViewer descriptionViewer = new InquiryChatViewer.Builder()
                    .withFilePath(inquiry.getFilePath())
                    .withMessage(text)
                    .withHeaderString("User")
                    .withInquiryChatType(InquiryChatType.INSTIGATOR_PROMPT)
                    .build();
            inquiryChatViewers.add(descriptionViewer);
        }

        // Process the finalized chat list
        List<InquiryChat> finalizedChatList = new ArrayList<>();
        List<InquiryChat> chatList = inquiry.getChats();
        if (!chatList.isEmpty()) {
            finalizedChatList.add(previousInquiryChat);
            String previousInquiryChatId = previousInquiryChat.getPreviousInquiryChatId();
            while (previousInquiryChatId != null) {
                previousInquiryChat = findPreviousInquiryChat(chatList, previousInquiryChat);
                if (previousInquiryChat != null) {
                    findAlternatesForInquiryChat(chatList, previousInquiryChat);
                    finalizedChatList.add(previousInquiryChat);
                    previousInquiryChatId = previousInquiryChat.getPreviousInquiryChatId();
                } else {
                    break;
                }
            }
        } else if (inquiry.getDescription() != null) {
            inquiryViewer.getInquiryChatBoxViewer().getWhatWasChangedButton().setVisible(true);
            inquiryViewer.getInquiryChatBoxViewer().getWhatDoesThisDoButton().setEnabled(true);
        } else {
            inquiryViewer.getInquiryChatBoxViewer().getWhatWasChangedButton().setVisible(false);
            inquiryViewer.getInquiryChatBoxViewer().getWhatDoesThisDoButton().setEnabled(false);
        }

        Collections.reverse(finalizedChatList);

        // Compress the chat list if necessary
        List<InquiryChatViewer> compressedInquiryChatViewers;
        if (!debugView) {
            compressedInquiryChatViewers = inquiryChatListFunctionCallCompressorService.compress(finalizedChatList);
        } else {
            compressedInquiryChatViewers = new ArrayList<>();
            for (InquiryChat inquiryChat : finalizedChatList) {
                InquiryChatViewer inquiryChatViewer = new InquiryChatViewer.Builder()
                        .withInquiryChat(inquiryChat)
                        .build();
                compressedInquiryChatViewers.add(inquiryChatViewer);
            }
        }

        // Add the compressed viewers to your list
        inquiryChatViewers.addAll(compressedInquiryChatViewers);

        // Clear the panel and add the viewers
        inquiryChatPanel.removeAll();

        for (InquiryChatViewer inquiryChatViewer : inquiryChatViewers) {
            // Add mouse listener for selection
            inquiryChatViewer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleSelection(inquiryChatViewer, e);
                }
            });
            inquiryChatPanel.add(inquiryChatViewer, gbc);
            gbc.gridy++;
        }
        // Add a filler component to push everything up
        gbc.gridy++;
        gbc.weighty = 1.0; // This component will absorb extra vertical space
        gbc.fill = GridBagConstraints.BOTH;
        JPanel filler = new JPanel();
        filler.setOpaque(false); // Make sure it's transparent
        inquiryChatPanel.add(filler, gbc);
        // Revalidate and repaint
        inquiryChatPanel.revalidate();
        inquiryChatPanel.repaint();

        // Scroll to the bottom
        SwingUtilities.invokeLater(() -> jBScrollPane.getVerticalScrollBar().setValue(jBScrollPane.getVerticalScrollBar().getMaximum()));
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

    public void componentResized(Boolean bigger) {
        boolean canScrollFurther = canScrollFurther(jBScrollPane);
        ApplicationManager.getApplication().invokeLater(() -> {
            // Get the current scroll percentage
            double scrollPercentage = 1.0;
            if (jBScrollPane.getVerticalScrollBar().getMaximum() != 0) {
                scrollPercentage = (((double) jBScrollPane.getVerticalScrollBar().getValue()) / jBScrollPane.getVerticalScrollBar().getMaximum());
                /*scrollPercentage = scrollPercentage * 100;
                scrollPercentage = Math.ceil(scrollPercentage);
                scrollPercentage = scrollPercentage / 100;*/
            }

             int newTotalHeight = 0;
            if (inquiryChatViewers != null) {
                for (int i = 0; i < inquiryChatViewers.size(); i++) {
                    InquiryChatViewer chatViewer = inquiryChatViewers.get(i);
                    int chatViewerHeight = 0;
                    for (Component component : chatViewer.getComponents()) {
                        if (component instanceof JBTextArea) {
                            JBTextArea chatDisplay = (JBTextArea) component;
                            int newHeight = 0;
                            int newWidth = inquiryViewer.getWidth();
                            if (chatViewer.getInquiryChat().getInquiryChatType() == InquiryChatType.CODE_SNIPPET) {
                                chatViewerHeight += textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth, false);
                            } else {
                                chatViewerHeight += textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth, true);
                            }
                            Dimension preferredSize = new Dimension(newWidth, newHeight);
                            chatDisplay.setPreferredSize(preferredSize);
                            chatDisplay.setMaximumSize(preferredSize);
                            chatDisplay.setSize(preferredSize);
                            chatViewerHeight += newHeight;
                        } else if (component instanceof FixedHeightPanel) {
                            FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                            chatViewerHeight += fixedHeightPanel.getPreferredSize().height;
                        } else if (component instanceof JToolBar) {
                            JToolBar jToolBar = (JToolBar) component;
                            chatViewerHeight += jToolBar.getPreferredSize().height;
                        } else if (component instanceof JTextPane) {
                            JTextPane chatDisplay = (JTextPane) component;
                            chatViewerHeight += chatDisplay.getPreferredSize().height;
                        }
                        System.out.println("Chat viewer height for i: " + i + " is " + chatViewerHeight);
                        newTotalHeight += chatViewerHeight;
                    }
                }
            }

            newTotalHeight += 10;
            jBScrollPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
            inquiryChatPanel.setPreferredSize(new Dimension(jBScrollPane.getWidth(), newTotalHeight));
            inquiryChatPanel.revalidate();
            inquiryChatPanel.repaint();

            // Set the scroll bar to the previous spot
            if (bigger != null && !bigger && !canScrollFurther) {
                jBScrollPane.getVerticalScrollBar().setValue(jBScrollPane.getVerticalScrollBar().getMaximum());
            } else {
                jBScrollPane.getVerticalScrollBar().setValue((int) Math.ceil(scrollPercentage * jBScrollPane.getVerticalScrollBar().getMaximum()));
            }
        });
    }

    public boolean canScrollFurther(JBScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        int min = verticalScrollBar.getMinimum();
        int max = verticalScrollBar.getMaximum();
        int extent = verticalScrollBar.getModel().getExtent();
        int currentScrollValue = verticalScrollBar.getValue();

        return max - (currentScrollValue + extent) > 0;
    }

    public void componentResized() {
        componentResized(null);
    }

    public JBMenuItem getEditItem() {
        return editItem;
    }

    public JBMenuItem getRegenerateItem() {
        return regenerateItem;
    }

    public JBMenuItem getNextChat() {
        return nextChat;
    }

    public JBMenuItem getPreviousChat() {
        return previousChat;
    }

    public OpenAiModelService getOpenAiModelService() {
        return openAiModelService;
    }

    public void setDebugView(boolean debugView) {
        this.debugView = debugView;
        updateInquiryContents(inquiry);
    }
}