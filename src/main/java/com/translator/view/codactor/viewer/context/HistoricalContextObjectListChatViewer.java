package com.translator.view.codactor.viewer.context;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.dao.history.ContextQueryDao;
import com.translator.model.codactor.history.HistoricalContextInquiryHolder;
import com.translator.model.codactor.history.HistoricalContextModificationHolder;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.history.HistoricalContextObjectType;
import com.translator.model.codactor.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.inquiry.InquiryChatType;
import com.translator.model.codactor.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.codactor.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.modification.RecordType;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.view.codactor.menu.TextAreaWindow;
import com.translator.view.codactor.panel.FixedHeightPanel;
import com.translator.view.codactor.renderer.InquiryChatRenderer;
import com.translator.view.codactor.viewer.InquiryChatViewer;

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

public class HistoricalContextObjectListChatViewer extends JPanel {
    private List<HistoricalContextObjectHolder> historicalContextObjectHolderList;
    private ContextQueryDao contextQueryDao;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private PromptContextService promptContextService;
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
                                                 HistoricalContextObjectListViewer historicalContextObjectListViewer) {
        this.textAreaHeightCalculatorService = new TextAreaHeightCalculatorServiceImpl();
        this.contextQueryDao = contextQueryDao;
        this.selectedChat = -1;
        this.historicalContextObjectHolderList = new ArrayList<>();
        this.promptContextService = promptContextService;
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
                    InquiryChatViewer selectedInquiryChatViewer = jList1.getModel().getElementAt(selectedIndex);
                    JBTextArea selectedJBTextArea = (JBTextArea) selectedInquiryChatViewer.getComponents()[1];
                    Color highlightColor = Color.decode("#009688");
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
        });
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

        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        contextChatViewerLabel = new JBLabel("Assembled Context Viewer");
        contextChatViewerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        contextChatViewerLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        contextChatViewerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(contextChatViewerLabel);

        jToolBar3 = new JToolBar();
        jToolBar3.setFloatable(false);
        jToolBar3.setBorderPainted(false);

        cancelButton = new JButton("Cancel");
        //cancelButton.setPreferredSize(new Dimension(cancelButton.getWidth(), 32));
        cancelButton.setEnabled(true);
        cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar3.add(cancelButton);

        saveChangesButton = new JButton("Apply Changes");
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
                        .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, jList1.getHeight(), Short.MAX_VALUE)); // Set size for jList1// Add a gap of 20 between jList1 and JBTextArea
    }

    public void updateChatContents(List<InquiryChat> inquiryChats) {
        if (inquiryChats == null) {
            jList1.setModel(new DefaultListModel<>());
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
        jList1.setPreferredSize(new Dimension(jBScrollPane1.getWidth() - 20, totalHeight));
        jList1.setModel(model);
        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                HistoricalContextObjectListChatViewer.this.componentResized(model);
            }
        };
        jList1.getParent().addComponentListener(componentListener);
        jBScrollPane1.setViewportView(jList1);
        //JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
        //vertical.setValue(vertical.getMaximum() - vertical.getVisibleAmount());
    }

    private void updateChatContentsWithContextInstalled() {
        JList<HistoricalContextObjectDataHolder> historicalContextObjectDataHolders = historicalContextObjectListViewer.getContextObjectList();
        List<InquiryChat> inquiryChats = new ArrayList<>();
        String filePath = null;
        for (int i = 0; i < historicalContextObjectDataHolders.getModel().getSize(); i++) {
            HistoricalContextObjectDataHolder historicalContextObjectDataHolder = historicalContextObjectDataHolders.getModel().getElementAt(i);
            HistoricalContextObjectHolder historicalContextObjectHolder = getHistoricalContextObjectHolder(historicalContextObjectDataHolder);
            if (historicalContextObjectHolder == null) {
                continue;
            }
            if (historicalContextObjectHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
                if (historicalContextObjectHolder.getHistoricalCompletedModificationHolder().getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
                    filePath = historicalContextObjectDataHolder.getHistoricalCompletedModificationDataHolder().getFileModificationSuggestionRecord().getFilePath();
                } else {
                    filePath = historicalContextObjectDataHolder.getHistoricalCompletedModificationDataHolder().getFileModificationSuggestionModificationRecord().getFilePath();
                }
                inquiryChats.addAll(historicalContextObjectHolder.getHistoricalCompletedModificationHolder().getRequestedChats());
            } else {
                filePath = historicalContextObjectDataHolder.getHistoricalContextInquiryDataHolder().getInquiry().getFilePath();
                inquiryChats.addAll(historicalContextObjectHolder.getHistoricalContextInquiryHolder().getRequestedChats());
            }
        }
        updateChatContents(/*filePath, */ inquiryChats);
    }

    public void updateChatContents() {
        JList<HistoricalContextObjectDataHolder> historicalContextObjectDataHolders = historicalContextObjectListViewer.getContextObjectList();
        List<HistoricalContextObjectHolder> newistoricalContextObjectHolderList = new ArrayList<>();
        for (int i = 0; i < historicalContextObjectDataHolders.getModel().getSize(); i++) {
            HistoricalContextObjectDataHolder historicalContextObjectDataHolder = historicalContextObjectDataHolders.getModel().getElementAt(i);
            HistoricalContextObjectHolder historicalContextObjectHolder = new HistoricalContextObjectHolder(historicalContextObjectDataHolder);
            newistoricalContextObjectHolderList.add(historicalContextObjectHolder);
        }
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<HistoricalContextObjectHolder> response = contextQueryDao.queryHistoricalContextObjects(newistoricalContextObjectHolderList);
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

    private HistoricalContextObjectHolder getHistoricalContextObjectHolder(HistoricalContextObjectDataHolder historicalContextObjectDataHolder) {
        HistoricalContextObjectHolder historicalContextObjectHolder = null;
        if (historicalContextObjectDataHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
            if (historicalContextObjectDataHolder.getHistoricalCompletedModificationDataHolder().getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
                FileModificationSuggestionRecord fileModificationSuggestionRecord = historicalContextObjectDataHolder.getHistoricalCompletedModificationDataHolder().getFileModificationSuggestionRecord();
                historicalContextObjectHolder = historicalContextObjectHolderList.stream()
                        .filter(historicalContextObjectHolderQuery -> {
                            HistoricalContextModificationHolder historicalCompletedModificationHolder = historicalContextObjectHolderQuery.getHistoricalCompletedModificationHolder();
                            if (historicalCompletedModificationHolder == null) {
                                return false;
                            }
                            return historicalContextObjectHolderQuery
                                    .getHistoricalCompletedModificationHolder()
                                    .getSubjectRecordId()
                                    .equals(fileModificationSuggestionRecord.getId());
                        })
                        .findFirst()
                        .orElse(null);
            } else {
                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = historicalContextObjectDataHolder.getHistoricalCompletedModificationDataHolder().getFileModificationSuggestionModificationRecord();
                historicalContextObjectHolder = historicalContextObjectHolderList.stream()
                        .filter(historicalContextObjectHolderQuery -> {
                            HistoricalContextModificationHolder historicalCompletedModificationHolder = historicalContextObjectHolderQuery.getHistoricalCompletedModificationHolder();
                            if (historicalCompletedModificationHolder == null) {
                                return false;
                            }
                            return historicalContextObjectHolderQuery
                                    .getHistoricalCompletedModificationHolder()
                                    .getSubjectRecordId()
                                    .equals(fileModificationSuggestionModificationRecord.getId());
                        })
                        .findFirst()
                        .orElse(null);
            }
        } else {
            Inquiry inquiry = historicalContextObjectDataHolder.getHistoricalContextInquiryDataHolder().getInquiry();
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
    }

    public void addContextObject(HistoricalContextObjectDataHolder historicalContextObjectDataHolder) {
        HistoricalContextObjectHolder historicalContextObjectHolder = new HistoricalContextObjectHolder(historicalContextObjectDataHolder);

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

    public void removeContextObject(HistoricalContextObjectDataHolder historicalContextObjectDataHolder) {
        historicalContextObjectHolderList.remove(getHistoricalContextObjectHolder(historicalContextObjectDataHolder));
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
