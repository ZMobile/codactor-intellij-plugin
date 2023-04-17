package com.translator.view.viewer.context;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBScrollPane;
import com.translator.dao.history.ContextQueryDao;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.history.HistoricalContextObjectType;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.inquiry.InquiryChat;
import com.translator.model.inquiry.InquiryChatType;
import com.translator.view.menu.TextAreaWindow;
import com.translator.view.renderer.InquiryChatRenderer;
import com.translator.view.viewer.InquiryChatViewer;
import com.translator.service.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.ui.measure.TextAreaHeightCalculatorServiceImpl;
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
import java.util.Map;

public class HistoricalContextObjectViewer extends JPanel {
    private HistoricalContextObjectHolder historicalContextObjectHolder;
    private ContextQueryDao contextQueryDao;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private JList<InquiryChatViewer> jList1;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JBScrollPane jBScrollPane1;
    private JButton viewerLabel;
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

        jList1 = new JList<>();
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
                    InquiryChatViewer selectedInquiryChatViewer = jList1.getModel().getElementAt(selectedIndex);
                    selectedInquiryChatViewer.setBackground(Color.decode("#228B22"));
                    JBTextArea selectedJBTextArea = (JBTextArea) selectedInquiryChatViewer.getComponents()[1];
                    Color highlightColor = Color.decode("#009688");
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
                        //inquiryChatViewer.revertToDefaultColor();
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
                    JBTextArea jBTextArea = (JBTextArea) inquiryChatViewer.getComponents()[1];
                    TextAreaWindow textAreaWindow = new TextAreaWindow(jBTextArea.getText());
                }
            }
        });
        jBScrollPane1 = new JBScrollPane(jList1);
        jBScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        viewerLabel = new JButton("Viewer");
        viewerLabel.setEnabled(false);
        viewerLabel.setFocusable(false);
        viewerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        viewerLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        viewerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(viewerLabel);

        jToolBar3 = new JToolBar();
        jToolBar3.setFloatable(false);
        jToolBar3.setBorderPainted(false);

        addButton = new JButton("+");
        addButton.setPreferredSize(new Dimension(50, 22));
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
            if (inquiryChat.getInquiryChatType() == InquiryChatType.CODE_SNIPPET) {
                JBTextArea codeDisplay = new JBTextArea(inquiryChat.getMessage().trim());
                codeDisplay.setEditable(false);
                //codeDisplay.setSyntaxEditingStyle(extensionToSyntaxMap.get(getFileExtension(inquiryChat.getFilePath())));
                int height = codeDisplay.getHeight();
                totalHeight += height;
                Dimension preferredSize = new Dimension(getPreferredSize().width, height);
                codeDisplay.setPreferredSize(preferredSize);
                codeDisplay.setMaximumSize(preferredSize);
                codeDisplay.setSize(preferredSize);
                InquiryChatViewer codeViewer = new InquiryChatViewer(inquiryChat);
                model.addElement(codeViewer);
            } else if (inquiryChat.getInquiryChatType() == InquiryChatType.INSTIGATOR_PROMPT) {
                JBTextArea descriptionDisplay = new JBTextArea(inquiryChat.getMessage().trim());
                descriptionDisplay.setLineWrap(true); // Wrap text to next line
                descriptionDisplay.setWrapStyleWord(true);
                descriptionDisplay.setEditable(false);
                int descriptionHeight = descriptionDisplay.getHeight();//textAreaHeightCalculatorService.calculateDesiredHeight(descriptionDisplay, jBScrollPane1.getWidth() - 20);
                totalHeight += descriptionHeight;
                Dimension descriptionPreferredSize = new Dimension(getPreferredSize().width, descriptionHeight);
                descriptionDisplay.setPreferredSize(descriptionPreferredSize);
                descriptionDisplay.setMaximumSize(descriptionPreferredSize);
                descriptionDisplay.setSize(descriptionPreferredSize);
                InquiryChatViewer descriptionViewer = new InquiryChatViewer(inquiryChat);
                model.addElement(descriptionViewer);
            } else {
                JBTextArea chatDisplay = new JBTextArea(inquiryChat.getMessage().trim());
                chatDisplay.setLineWrap(true); // Wrap text to next line
                chatDisplay.setWrapStyleWord(true);
                chatDisplay.setEditable(false);
                int height = chatDisplay.getHeight();//textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, jBScrollPane1.getWidth() - 20);
                totalHeight += height;
                Dimension preferredSize = new Dimension(getPreferredSize().width, height);
                chatDisplay.setPreferredSize(preferredSize);
                chatDisplay.setMaximumSize(preferredSize);
                chatDisplay.setSize(preferredSize);
                InquiryChatViewer chatViewer = new InquiryChatViewer(inquiryChat);
                model.addElement(chatViewer);
            }
        }
        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                HistoricalContextObjectViewer.this.componentResized(model);
            }
        };
        jList1.getParent().addComponentListener(componentListener);
        jList1.setPreferredSize(new Dimension(jBScrollPane1.getWidth() - 20, totalHeight));
        jList1.setModel(model);
        jBScrollPane1.setPreferredSize(new Dimension((int)jBScrollPane1.getPreferredSize().getWidth(), jList1.getHeight()));
        JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
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
            JBTextArea chatDisplay = (JBTextArea) chatViewer.getComponent(1);
            int newHeight = 0;
            int newWidth = jBScrollPane1.getWidth() - 20;
            if (chatViewer.getInquiryChat().getInquiryChatType() == InquiryChatType.CODE_SNIPPET) {
                newHeight += chatDisplay.getHeight() + chatViewer.getComponent(0).getHeight() + 2;
            } else {
                newHeight += chatDisplay.getHeight();//textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth);
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
        jBScrollPane1.setPreferredSize(new Dimension((int)jBScrollPane1.getPreferredSize().getWidth(), jList1.getHeight()));
        JScrollBar vertical = jBScrollPane1.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
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
