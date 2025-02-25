package com.translator.view.codactor.viewer.context;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.dao.history.ContextQueryDao;
import com.translator.model.codactor.ai.history.HistoricalContextFileModificationHolder;
import com.translator.model.codactor.ai.history.HistoricalContextInquiryHolder;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.history.HistoricalContextObjectType;
import com.translator.model.codactor.ai.history.data.HistoricalObjectDataHolder;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.chat.InquiryChatType;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.ai.modification.RecordType;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.ai.chat.functions.InquiryChatListFunctionCallCompressorService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.view.codactor.menu.TextAreaWindow;
import com.translator.viewmodel.FixedHeightPanel;
import com.translator.view.codactor.renderer.InquiryChatRenderer;
import com.translator.view.codactor.viewer.inquiry.InquiryChatViewer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class HistoricalContextObjectListChatViewer extends JPanel {
    private List<HistoricalContextObjectHolder> historicalContextObjectHolderList;
    private ContextQueryDao contextQueryDao;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private PromptContextService promptContextService;
    private InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService;
    private HistoricalContextObjectListViewer historicalContextObjectListViewer;
    private JList<InquiryChatViewer> jList1;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JBScrollPane jBScrollPane1;
    private JBLabel contextChatViewerLabel;
    private JButton cancelButton;
    private JButton saveChangesButton;
    private int selectedChat;

    public HistoricalContextObjectListChatViewer(ContextQueryDao contextQueryDao,
                                                 PromptContextService promptContextService,
                                                 InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService,
                                                 HistoricalContextObjectListViewer historicalContextObjectListViewer) {
        this.textAreaHeightCalculatorService = new TextAreaHeightCalculatorServiceImpl();
        this.contextQueryDao = contextQueryDao;
        this.selectedChat = -1;
        this.historicalContextObjectHolderList = new ArrayList<>();
        this.promptContextService = promptContextService;
        this.inquiryChatListFunctionCallCompressorService = inquiryChatListFunctionCallCompressorService;
        this.historicalContextObjectListViewer = historicalContextObjectListViewer;
        initComponents();
    }

    private void initComponents() {
        jList1 = new JList<>();
        jList1.setModel(new DefaultListModel<>());
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList1.setCellRenderer(new InquiryChatRenderer());
        jList1.addListSelectionListener(new ListSelectionListener() {
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
                                    if (editor != null) {
                                        editor.getMarkupModel().addRangeHighlighter(0, editor.getDocument().getText().length(), HighlighterLayer.SELECTION - 1, new TextAttributes(null, highlightColor, null, EffectType.BOXED, Font.PLAIN), HighlighterTargetArea.EXACT_RANGE);
                                    }
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
                                if (fixedHeightPanel.getEditor() != null) {
                                    Editor editor = fixedHeightPanel.getEditor();
                                    editor.getMarkupModel().removeAllHighlighters();
                                }
                            }
                        }
                    }
                }
            }
        });
        jList1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //If Ctrl + C is pressed (or command for mac users)
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
                if (e.getClickCount() == 2) {
                    if (selectedChat == -1) {
                        return;
                    }
                    InquiryChatViewer inquiryChatViewer = jList1.getModel().getElementAt(selectedChat);
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
                            if (fixedHeightPanel.getEditor() != null) {
                                Editor editor = fixedHeightPanel.getEditor();
                                text.append(editor.getDocument().getText());
                                firstComponentCopied = true;
                            }
                        } else if (component1 instanceof JTextPane) {
                            JTextPane jTextPane = (JTextPane) component1;
                            HTMLDocument doc = (HTMLDocument)jTextPane.getDocument();
                            int length = doc.getLength();
                            try {
                                text.append(doc.getText(0, length));
                            } catch (BadLocationException ex) {
                                throw new RuntimeException(ex);
                            }
                            firstComponentCopied = true;
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
                    new TextAreaWindow(text.toString());
                }
            }
        });
        jBScrollPane1 = new JBScrollPane(jList1);
        jBScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jToolBar2 = new JToolBar();
        jToolBar2.setBackground(Color.darkGray);
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        contextChatViewerLabel = new JBLabel("Assembled Context Viewer");
        contextChatViewerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        contextChatViewerLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        contextChatViewerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(contextChatViewerLabel);

        jToolBar3 = new JToolBar();
        jToolBar3.setBackground(Color.darkGray);
        jToolBar3.setFloatable(false);
        jToolBar3.setBorderPainted(false);

        cancelButton = new JButton("Cancel");
        Border emptyBorder = BorderFactory.createEmptyBorder();
        cancelButton.setBorder(emptyBorder);
        //cancelButton.setPreferredSize(new Dimension(cancelButton.getWidth(), 32));
        cancelButton.setEnabled(true);
        cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar3.add(cancelButton);

        saveChangesButton = new JButton("Apply Changes");
        saveChangesButton.setBorder(emptyBorder);
        //saveChangesButton.setPreferredSize(new Dimension(saveChangesButton.getWidth(), 32));
        saveChangesButton.setEnabled(true);
        saveChangesButton.setHorizontalTextPosition(SwingConstants.CENTER);
        saveChangesButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        saveChangesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar3.add(saveChangesButton);

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
                                .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)); // Set size for jList1// Add a gap of 20 between jList1 and JBTextArea
    }

    public void updateChatContents(List<InquiryChat> inquiryChats) {
        if (inquiryChats == null) {
            jList1.setModel(new DefaultListModel<>());
            return;
        }
        DefaultListModel<InquiryChatViewer> model = new DefaultListModel<>();
        List<InquiryChatViewer> chatViewers = inquiryChatListFunctionCallCompressorService.compress(inquiryChats);
        for (InquiryChatViewer chatViewer : chatViewers) {
            model.addElement(chatViewer);
        }
        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                HistoricalContextObjectListChatViewer.this.componentResized(model);
            }
        };
        jList1.getParent().addComponentListener(componentListener);
        this.componentResized(model);
        jBScrollPane1.getVerticalScrollBar().setValue(jBScrollPane1.getVerticalScrollBar().getMaximum());
    }

    private void updateChatContentsWithContextInstalled() {
        JList<HistoricalObjectDataHolder> historicalContextObjectDataHolders = historicalContextObjectListViewer.getContextObjectList();
        List<InquiryChat> inquiryChats = new ArrayList<>();
        String filePath = null;
        for (int i = 0; i < historicalContextObjectDataHolders.getModel().getSize(); i++) {
            HistoricalObjectDataHolder historicalObjectDataHolder = historicalContextObjectDataHolders.getModel().getElementAt(i);
            HistoricalContextObjectHolder historicalContextObjectHolder = getHistoricalContextObjectHolder(historicalObjectDataHolder);
            if (historicalContextObjectHolder == null) {
                continue;
            }
            if (historicalContextObjectHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
                if (historicalContextObjectHolder.getHistoricalModificationHolder().getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
                    filePath = historicalObjectDataHolder.getHistoricalModificationDataHolder().getFileModificationSuggestionRecord().getFilePath();
                } else {
                    filePath = historicalObjectDataHolder.getHistoricalModificationDataHolder().getFileModificationSuggestionModificationRecord().getFilePath();
                }
                inquiryChats.addAll(historicalContextObjectHolder.getHistoricalModificationHolder().getRequestedChats());
            } else {
                filePath = historicalObjectDataHolder.getHistoricalContextInquiryDataHolder().getInquiry().getFilePath();
                inquiryChats.addAll(historicalContextObjectHolder.getHistoricalContextInquiryHolder().getRequestedChats());
            }
        }

        updateChatContents(/*filePath, */ inquiryChats);
    }

    public void updateChatContents() {
        JList<HistoricalObjectDataHolder> historicalContextObjectDataHolders = historicalContextObjectListViewer.getContextObjectList();
        List<HistoricalContextObjectHolder> newHistoricalContextObjectHolderList = new ArrayList<>();
        for (int i = 0; i < historicalContextObjectDataHolders.getModel().getSize(); i++) {
            HistoricalObjectDataHolder historicalObjectDataHolder = historicalContextObjectDataHolders.getModel().getElementAt(i);
            HistoricalContextObjectHolder historicalContextObjectHolder = new HistoricalContextObjectHolder(historicalObjectDataHolder);
            newHistoricalContextObjectHolderList.add(historicalContextObjectHolder);
        }
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<HistoricalContextObjectHolder> response = contextQueryDao.queryHistoricalContextObjects(newHistoricalContextObjectHolderList);
            if (response != null) {
                historicalContextObjectHolderList.addAll(response);
                updateChatContentsWithContextInstalled();
            } else {
                JOptionPane.showMessageDialog(null, "Unable to load context objects", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return null;
        });
    }

    private HistoricalContextObjectHolder getHistoricalContextObjectHolder(HistoricalObjectDataHolder historicalObjectDataHolder) {
        HistoricalContextObjectHolder historicalContextObjectHolder = null;
        if (historicalObjectDataHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
            if (historicalObjectDataHolder.getHistoricalModificationDataHolder().getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
                FileModificationSuggestionRecord fileModificationSuggestionRecord = historicalObjectDataHolder.getHistoricalModificationDataHolder().getFileModificationSuggestionRecord();
                historicalContextObjectHolder = historicalContextObjectHolderList.stream()
                        .filter(historicalContextObjectHolderQuery -> {
                            HistoricalContextFileModificationHolder historicalModificationHolder = historicalContextObjectHolderQuery.getHistoricalModificationHolder();
                            if (historicalModificationHolder == null) {
                                return false;
                            }
                            return historicalContextObjectHolderQuery
                                    .getHistoricalModificationHolder()
                                    .getSubjectRecordId()
                                    .equals(fileModificationSuggestionRecord.getId());
                        })
                        .findFirst()
                        .orElse(null);
            } else {
                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = historicalObjectDataHolder.getHistoricalModificationDataHolder().getFileModificationSuggestionModificationRecord();
                historicalContextObjectHolder = historicalContextObjectHolderList.stream()
                        .filter(historicalContextObjectHolderQuery -> {
                            HistoricalContextFileModificationHolder historicalModificationHolder = historicalContextObjectHolderQuery.getHistoricalModificationHolder();
                            if (historicalModificationHolder == null) {
                                return false;
                            }
                            return historicalContextObjectHolderQuery
                                    .getHistoricalModificationHolder()
                                    .getSubjectRecordId()
                                    .equals(fileModificationSuggestionModificationRecord.getId());
                        })
                        .findFirst()
                        .orElse(null);
            }
        } else {
            Inquiry inquiry = historicalObjectDataHolder.getHistoricalContextInquiryDataHolder().getInquiry();
            historicalContextObjectHolder = historicalContextObjectHolderList.stream()
                    .filter(historicalContextObjectHolderQuery -> {
                        HistoricalContextInquiryHolder historicalContextInquiryHolder = historicalContextObjectHolderQuery.getHistoricalContextInquiryHolder();
                        if (historicalContextInquiryHolder == null) {
                            return false;
                        }
                        return historicalContextObjectHolderQuery
                                .getHistoricalContextInquiryHolder()
                                .getInquiryId()
                                .equals(inquiry.getId());
                    })
                    .findFirst()
                    .orElse(null);
        }
        return historicalContextObjectHolder;
    }

    public String getFileExtension(String filePath) {
        if (filePath == null) {
            return null;
        }
        return filePath.substring(filePath.lastIndexOf(".") + 1).trim();
    }

    private InquiryChat findPreviousInquiryChat(List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        return inquiryChats.stream()
                .filter(inquiryChatQuery -> inquiryChatQuery.getId().equals(inquiryChat.getPreviousInquiryChatId()))
                .findFirst()
                .orElse(null);
    }

    public void componentResized(DefaultListModel<InquiryChatViewer> previousModel) {
        if (previousModel == null || previousModel.isEmpty()) {
            return;
        }
        DefaultListModel<InquiryChatViewer> newModel = new DefaultListModel<>();
        int newTotalHeight = 0;
        for (int i = 0; i < previousModel.size(); i++) {
            InquiryChatViewer chatViewer = previousModel.getElementAt(i);
            for (Component component : chatViewer.getComponents()) {
                if (component instanceof JBTextArea) {
                    JBTextArea chatDisplay = (JBTextArea) component;
                    int newHeight = 0;
                    int newWidth = getWidth();
                    if (chatViewer.getInquiryChat().getInquiryChatType() == InquiryChatType.CODE_SNIPPET) {
                        newHeight += textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth, false);
                    } else {
                        newHeight += textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth, true);
                    }
                    Dimension preferredSize = new Dimension(newWidth, newHeight);
                    chatDisplay.setPreferredSize(preferredSize);
                    chatDisplay.setMaximumSize(preferredSize);
                    chatDisplay.setSize(preferredSize);
                    newTotalHeight += newHeight;
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
        ApplicationManager.getApplication().invokeLater(() -> {
            jBScrollPane1.setViewportView(jList1);
        });
    }

    public void addContextObject(HistoricalObjectDataHolder historicalObjectDataHolder) {
        HistoricalContextObjectHolder historicalContextObjectHolder = new HistoricalContextObjectHolder(historicalObjectDataHolder);

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            HistoricalContextObjectHolder response = contextQueryDao.queryHistoricalContextObject(historicalContextObjectHolder);
            if (response != null) {
                historicalContextObjectHolderList.add(response);
                updateChatContentsWithContextInstalled();
            } else {
                JOptionPane.showMessageDialog(null, "Unable to load context object", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return null;
        });
    }

    public void removeContextObject(HistoricalObjectDataHolder historicalObjectDataHolder) {
        historicalContextObjectHolderList.remove(getHistoricalContextObjectHolder(historicalObjectDataHolder));
        updateChatContentsWithContextInstalled();
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getSaveChangesButton() {
        return saveChangesButton;
    }

    public List<HistoricalContextObjectHolder> getHistoricalContextObjectHolderList() {
        return historicalContextObjectHolderList;
    }
}
