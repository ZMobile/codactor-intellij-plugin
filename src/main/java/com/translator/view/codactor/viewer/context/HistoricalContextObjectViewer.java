package com.translator.view.codactor.viewer.context;

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
import com.translator.dao.history.ContextQueryDao;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.history.HistoricalContextObjectType;
import com.translator.model.codactor.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.inquiry.InquiryChatType;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorServiceImpl;
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
import java.util.List;

public class HistoricalContextObjectViewer extends JPanel {
    private HistoricalContextObjectHolder historicalContextObjectHolder;
    private ContextQueryDao contextQueryDao;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private JBList<InquiryChatViewer> jList1;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JBScrollPane jBScrollPane1;
    private JBLabel viewerLabel;
    private JButton addButton;
    private HistoricalContextModificationListViewer historicalContextModificationListViewer;
    private HistoricalContextInquiryListViewer historicalContextInquiryListViewer;
    private HistoricalContextObjectListViewer historicalContextObjectListViewer;
    private int selectedChat;

    public HistoricalContextObjectViewer(ContextQueryDao contextQueryDao) {
        this.textAreaHeightCalculatorService = new TextAreaHeightCalculatorServiceImpl();
        this.contextQueryDao = contextQueryDao;
        this.historicalContextObjectHolder = null;
        this.selectedChat = -1;
        initComponents();
    }

    private void initComponents() {

        jList1 = new JBList<>();
        //jList1.setMaximumSize(new Dimension(getWidth(), Integer.MAX_VALUE));
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

        viewerLabel = new JBLabel("Viewer");
        viewerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        viewerLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        viewerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(viewerLabel);

        jToolBar3 = new JToolBar();
        jToolBar3.setFloatable(false);
        jToolBar3.setBorderPainted(false);

        addButton = new JButton("+");
        addButton.setPreferredSize(new Dimension(50, 32));
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(SwingConstants.CENTER);
        addButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (historicalContextModificationListViewer.getModificationList().getSelectedIndex() != -1) {
                    HistoricalContextObjectDataHolder selectedInquiry = historicalContextModificationListViewer.getModificationList().getSelectedValue();
                    historicalContextObjectListViewer.addContextObject(selectedInquiry);
                    historicalContextModificationListViewer.getModificationList().clearSelection();
                } else if (historicalContextInquiryListViewer.getInquiryList().getSelectedIndex() != -1) {
                    HistoricalContextObjectDataHolder selectedInquiry = historicalContextInquiryListViewer.getInquiryList().getSelectedValue();
                    historicalContextObjectListViewer.addContextObject(selectedInquiry);
                    historicalContextInquiryListViewer.getInquiryList().clearSelection();
                }
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
                HistoricalContextObjectViewer.this.componentResized(model);
            }
        };
        jList1.getParent().addComponentListener(componentListener);
        jBScrollPane1.setViewportView(jList1);
        //JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
        //vertical.setValue(vertical.getMaximum() - vertical.getVisibleAmount());
    }

    public void updateHistoricalContextObjectHolder(HistoricalContextObjectHolder historicalContextObjectHolder) {
        this.historicalContextObjectHolder = historicalContextObjectHolder;
        if (historicalContextObjectHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.INQUIRY) {
            updateChatContents(historicalContextObjectHolder.getHistoricalContextInquiryHolder().getRequestedChats());
        } else if (historicalContextObjectHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
            updateChatContents(historicalContextObjectHolder.getHistoricalCompletedModificationHolder().getRequestedChats());
        }
        JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public void updateHistoricalContextObjectHolder(HistoricalContextObjectDataHolder historicalContextObjectDataHolder) {
        HistoricalContextObjectHolder newHistoricalContextObjectHolder = new HistoricalContextObjectHolder(historicalContextObjectDataHolder);
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            HistoricalContextObjectHolder response = contextQueryDao.queryHistoricalContextObject(newHistoricalContextObjectHolder);
            if (response != null) {
                historicalContextObjectHolder = response;
                if (historicalContextObjectHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.INQUIRY) {
                    updateChatContents(historicalContextObjectHolder.getHistoricalContextInquiryHolder().getRequestedChats());
                } else if (historicalContextObjectHolder.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
                    updateChatContents(historicalContextObjectHolder.getHistoricalCompletedModificationHolder().getRequestedChats());
                }
                JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            } else {
                JOptionPane.showMessageDialog(null, "Unable to load context object", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return null;
        });
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

    public void setHistoricalContextInquiryListViewer(HistoricalContextInquiryListViewer historicalContextInquiryListViewer) {
        this.historicalContextInquiryListViewer = historicalContextInquiryListViewer;
    }

    public void setHistoricalContextModificationListViewer(HistoricalContextModificationListViewer historicalContextModificationListViewer) {
            this.historicalContextModificationListViewer = historicalContextModificationListViewer;
    }

    public void setHistoricalContextObjectListViewer(HistoricalContextObjectListViewer historicalContextObjectListViewer) {
        this.historicalContextObjectListViewer = historicalContextObjectListViewer;
    }
}
