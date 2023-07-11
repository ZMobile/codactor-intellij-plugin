/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.translator.view.codactor.dialog;

import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.dao.history.ContextQueryDao;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.view.codactor.viewer.context.*;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author zantehays
 */
public class PromptContextBuilderDialog extends JDialog {
    private ContextQueryDao contextQueryDao;
    private InquiryDao inquiryDao;
    private PromptContextService promptContextService;
    private HistoricalContextObjectViewer historicalContextObjectViewer;
    private HistoricalContextModificationListViewer historicalContextModificationListViewer;
    private HistoricalContextInquiryListViewer historicalContextInquiryListViewer;
    private HistoricalContextObjectListViewer historicalContextObjectListViewer;
    private HistoricalContextObjectListChatViewer historicalContextObjectListChatViewer;

    /**
     * Creates new form PromptContextBuilder
     */
    @Inject
    public PromptContextBuilderDialog(ContextQueryDao contextQueryDao,
                                      InquiryDao inquiryDao,
                                      CodeModificationHistoryDao codeModificationHistoryDao,
                                      @Assisted PromptContextService promptContextService) {
        setModal(false);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.contextQueryDao = contextQueryDao;
        this.inquiryDao = inquiryDao;
        this.promptContextService = promptContextService;
        this.historicalContextObjectViewer = new HistoricalContextObjectViewer(contextQueryDao);
        this.historicalContextObjectListViewer = new HistoricalContextObjectListViewer(historicalContextObjectViewer);
        this.historicalContextModificationListViewer = new HistoricalContextModificationListViewer(historicalContextObjectViewer, historicalContextObjectListViewer, codeModificationHistoryDao);
        this.historicalContextInquiryListViewer = new HistoricalContextInquiryListViewer(inquiryDao, historicalContextObjectViewer, historicalContextObjectListViewer);

        // Set dependencies between the components
        setDependencies();

        if (promptContextService.getPromptContext() != null) {
            this.historicalContextObjectListViewer.updateContextObjectList(promptContextService.getPromptContextData());
        }
        setTitle("Prompt Context Builder");
        initComponents();
    }

    private void setDependencies() {
        historicalContextModificationListViewer.setHistoricalContextInquiryListViewer(historicalContextInquiryListViewer);
        historicalContextInquiryListViewer.setHistoricalContextModificationListViewer(historicalContextModificationListViewer);
        historicalContextObjectListViewer.setHistoricalContextModificationListViewer(historicalContextModificationListViewer);
        historicalContextObjectListViewer.setHistoricalContextInquiryListViewer(historicalContextInquiryListViewer);
        historicalContextObjectViewer.setHistoricalContextModificationListViewer(historicalContextModificationListViewer);
        historicalContextObjectViewer.setHistoricalContextInquiryListViewer(historicalContextInquiryListViewer);
        historicalContextObjectViewer.setHistoricalContextObjectListViewer(historicalContextObjectListViewer);
        historicalContextObjectListChatViewer = new HistoricalContextObjectListChatViewer(contextQueryDao, promptContextService, historicalContextObjectListViewer);
        historicalContextObjectListViewer.setHistoricalContextObjectListChatViewer(historicalContextObjectListChatViewer);
    }

    public void initComponents() {
        jBScrollPane3 = new JBScrollPane();
        jBList3 = new JBList<>();
        mainPanel = new JBPanel();
        mainPanel.setBackground(JBColor.background());
        mainPanel.setBorder(JBUI.Borders.empty());
        mainSplitter = new Splitter();
        leftViewerAndHistoryHolderPanel = new JBPanel();
        secondarySplitterSeparatingLeftViewerFromHistoryHolders = new Splitter();
        leftViewerPanel = new JBPanel();
        jToolBar3 = new JToolBar();
        jBScrollPane4 = new JBScrollPane();
        jBList4 = new JBList<>();
        historyHolderPanel = new JBPanel();
        inquiryFileModificationSplitter = new Splitter();
        inquiryHistoryPanel = new JBPanel();
        jToolBar7 = new JToolBar();
        jBScrollPane8 = new JBScrollPane();
        jBList8 = new JBList<>();
        fileModificationHistoryPanel = new JBPanel();
        jToolBar8 = new JToolBar();
        jBScrollPane9 = new JBScrollPane();
        jBList9 = new JBList<>();
        rightViewerAndCompiledContextHolderPanel = new JBPanel();
        secondarySplitterSeparatingRightViewerFromCompiledContextHolder = new Splitter();
        compiledContextHolderPanel = new JBPanel();
        jToolBar9 = new JToolBar();
        jBScrollPane10 = new JBScrollPane();
        jBList10 = new JBList<>();
        rightViewerPanel = new JBPanel();
        jToolBar10 = new JToolBar();
        jBScrollPane11 = new JBScrollPane();
        jBList11 = new JBList<>();
        // Create a GroupLayout for the spliter2 panel
        GroupLayout secondarySplitterSeparatingLeftViewerFromHistoryHoldersLayout = new GroupLayout(secondarySplitterSeparatingLeftViewerFromHistoryHolders);
        jBList3.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane3.setViewportView(jBList3);


        jToolBar3.setRollover(true);

        jBList4.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane4.setViewportView(jBList4);

        GroupLayout leftViewerPanelLayout = new GroupLayout(leftViewerPanel);
        leftViewerPanel.setLayout(leftViewerPanelLayout);
        leftViewerPanelLayout.setHorizontalGroup(
                leftViewerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(leftViewerPanelLayout.createSequentialGroup()
                                .addGroup(leftViewerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jBScrollPane4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Integer.MAX_VALUE)
                                        .addComponent(jToolBar3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Integer.MAX_VALUE)))
        );
        leftViewerPanelLayout.setVerticalGroup(
                leftViewerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(leftViewerPanelLayout.createSequentialGroup()
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jBScrollPane4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        secondarySplitterSeparatingLeftViewerFromHistoryHolders.setFirstComponent(leftViewerPanel);

        jToolBar7.setRollover(true);

        jBList8.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane8.setViewportView(jBList8);

        GroupLayout inquiryHistoryPanelLayout = new GroupLayout(inquiryHistoryPanel);
        inquiryHistoryPanel.setLayout(inquiryHistoryPanelLayout);
        inquiryHistoryPanelLayout.setHorizontalGroup(
                inquiryHistoryPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jBScrollPane8, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                        .addComponent(jToolBar7, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        inquiryHistoryPanelLayout.setVerticalGroup(
                inquiryHistoryPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(inquiryHistoryPanelLayout.createSequentialGroup()
                                .addComponent(jToolBar7, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jBScrollPane8, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
        );

        inquiryFileModificationSplitter.setFirstComponent(inquiryHistoryPanel);

        jToolBar8.setRollover(true);

        jBList9.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane9.setViewportView(jBList9);

        GroupLayout fileModificationHistoryPanelLayout = new GroupLayout(fileModificationHistoryPanel);
        fileModificationHistoryPanel.setLayout(fileModificationHistoryPanelLayout);
        fileModificationHistoryPanelLayout.setHorizontalGroup(
                fileModificationHistoryPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar8, GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                        .addComponent(jBScrollPane9)
        );
        fileModificationHistoryPanelLayout.setVerticalGroup(
                fileModificationHistoryPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(fileModificationHistoryPanelLayout.createSequentialGroup()
                                .addComponent(jToolBar8, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jBScrollPane9, GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE))
        );

        inquiryFileModificationSplitter.setSecondComponent(fileModificationHistoryPanel);

        GroupLayout historyHolderPanelLayout = new GroupLayout(historyHolderPanel);
        historyHolderPanel.setLayout(historyHolderPanelLayout);
        historyHolderPanelLayout.setHorizontalGroup(
                historyHolderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, historyHolderPanelLayout.createSequentialGroup()
                                .addComponent(inquiryFileModificationSplitter, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                                .addGap(0, 0, 0))
        );
        historyHolderPanelLayout.setVerticalGroup(
                historyHolderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(inquiryFileModificationSplitter)
        );

        secondarySplitterSeparatingLeftViewerFromHistoryHolders.setSecondComponent(historyHolderPanel);

        GroupLayout leftViewerAndHistoryHolderPanelLayout = new GroupLayout(leftViewerAndHistoryHolderPanel);
        leftViewerAndHistoryHolderPanel.setLayout(leftViewerAndHistoryHolderPanelLayout);
        leftViewerAndHistoryHolderPanelLayout.setHorizontalGroup(
                leftViewerAndHistoryHolderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(secondarySplitterSeparatingLeftViewerFromHistoryHolders, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        leftViewerAndHistoryHolderPanelLayout.setVerticalGroup(
                leftViewerAndHistoryHolderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(secondarySplitterSeparatingLeftViewerFromHistoryHolders, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainSplitter.setFirstComponent(leftViewerAndHistoryHolderPanel);

        jToolBar9.setRollover(true);

        jBList10.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane10.setViewportView(jBList10);

        GroupLayout compiledContextHolderPanelLayout = new GroupLayout(compiledContextHolderPanel);
        compiledContextHolderPanel.setLayout(compiledContextHolderPanelLayout);
        compiledContextHolderPanelLayout.setHorizontalGroup(
                compiledContextHolderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jBScrollPane10, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                        .addComponent(jToolBar9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        compiledContextHolderPanelLayout.setVerticalGroup(
                compiledContextHolderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(compiledContextHolderPanelLayout.createSequentialGroup()
                                .addComponent(jToolBar9, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jBScrollPane10, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
        );
        compiledContextHolderPanel.setMinimumSize(new Dimension(0, 0));
        compiledContextHolderPanel.setPreferredSize(new Dimension(106, 805));
        rightViewerAndCompiledContextHolderPanel.setMinimumSize(new Dimension(0, 0));
        rightViewerAndCompiledContextHolderPanel.setPreferredSize(new Dimension(859, 805));

        secondarySplitterSeparatingRightViewerFromCompiledContextHolder.setFirstComponent(compiledContextHolderPanel);

        jToolBar10.setRollover(true);

        jBList11.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane11.setViewportView(jBList11);

        GroupLayout rightViewerPanelLayout = new GroupLayout(rightViewerPanel);
        rightViewerPanel.setLayout(rightViewerPanelLayout);
        rightViewerPanelLayout.setHorizontalGroup(
                rightViewerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar10, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBScrollPane11, GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
        );
        rightViewerPanelLayout.setVerticalGroup(
                rightViewerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(rightViewerPanelLayout.createSequentialGroup()
                                .addComponent(jToolBar10, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jBScrollPane11, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
        );

        secondarySplitterSeparatingRightViewerFromCompiledContextHolder.setSecondComponent(rightViewerPanel);

        GroupLayout rightViewerAndCompiledContextHolderPanelLayout = new GroupLayout(rightViewerAndCompiledContextHolderPanel);
        rightViewerAndCompiledContextHolderPanel.setLayout(rightViewerAndCompiledContextHolderPanelLayout);
        rightViewerAndCompiledContextHolderPanelLayout.setHorizontalGroup(
                rightViewerAndCompiledContextHolderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(secondarySplitterSeparatingRightViewerFromCompiledContextHolder)
        );
        rightViewerAndCompiledContextHolderPanelLayout.setVerticalGroup(
                rightViewerAndCompiledContextHolderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(secondarySplitterSeparatingRightViewerFromCompiledContextHolder)
        );

        mainSplitter.setSecondComponent(rightViewerAndCompiledContextHolderPanel);

        /// With this
        mainPanel.add(mainSplitter, BorderLayout.CENTER);

        this.secondarySplitterSeparatingLeftViewerFromHistoryHolders.setLayout(secondarySplitterSeparatingLeftViewerFromHistoryHoldersLayout);
        this.secondarySplitterSeparatingLeftViewerFromHistoryHolders.setFirstComponent(historicalContextObjectViewer);
        this.inquiryFileModificationSplitter.setFirstComponent(historicalContextModificationListViewer);
        this.inquiryFileModificationSplitter.setSecondComponent(historicalContextInquiryListViewer);
        this.secondarySplitterSeparatingRightViewerFromCompiledContextHolder.setFirstComponent(historicalContextObjectListViewer);
        this.secondarySplitterSeparatingRightViewerFromCompiledContextHolder.setSecondComponent(historicalContextObjectListChatViewer);
        this.historicalContextObjectListChatViewer.getCancelButton().addActionListener(e -> {
            dispose();
        });
        this.historicalContextObjectListChatViewer.getSaveChangesButton().addActionListener(e -> {
            promptContextService.savePromptContext(historicalContextObjectListViewer.getContextObjectArrayList());
            dispose();
        });
        setContentPane(mainPanel);
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   // </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JBList<String> jBList10;
    private JBList<String> jBList11;
    private JBList<String> jBList3;
    private JBList<String> jBList4;
    private JBList<String> jBList8;
    private JBList<String> jBList9;
    private JBPanel historyHolderPanel;
    private JBPanel compiledContextHolderPanel;
    private JBPanel rightViewerPanel;
    private JBPanel inquiryHistoryPanel;
    private JBPanel fileModificationHistoryPanel;
    private JBPanel mainPanel;
    private JBPanel leftViewerAndHistoryHolderPanel;
    private JBPanel rightViewerAndCompiledContextHolderPanel;
    private JBPanel leftViewerPanel;
    private JBScrollPane jBScrollPane10;
    private JBScrollPane jBScrollPane11;
    private JBScrollPane jBScrollPane3;
    private JBScrollPane jBScrollPane4;
    private JBScrollPane jBScrollPane8;
    private JBScrollPane jBScrollPane9;
    private Splitter mainSplitter;
    private Splitter secondarySplitterSeparatingLeftViewerFromHistoryHolders;
    private Splitter inquiryFileModificationSplitter;
    private Splitter secondarySplitterSeparatingRightViewerFromCompiledContextHolder;
    private JToolBar jToolBar10;
    private JToolBar jToolBar3;
    private JToolBar jToolBar7;
    private JToolBar jToolBar8;
    private JToolBar jToolBar9;
    // End of variables declaration//GEN-END:variables
}
