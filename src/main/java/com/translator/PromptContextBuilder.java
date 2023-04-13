/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.translator;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.intellij.ui.components.JBScrollPane;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.dao.history.ContextQueryDao;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.context.PromptContextService;
import com.translator.view.viewer.context.*;
import com.translator.worker.LimitedSwingWorkerExecutor;

import javax.inject.Inject;
import java.util.Map;

/**
 *
 * @author zantehays
 */
public class PromptContextBuilder extends javax.swing.JFrame {
    private final HistoricalContextObjectViewer historicalContextObjectViewer;
    private final HistoricalContextModificationListViewer historicalContextModificationListViewer;
    private final HistoricalContextInquiryListViewer historicalContextInquiryListViewer;
    private final HistoricalContextObjectListViewer historicalContextObjectListViewer;
    private final HistoricalContextObjectListChatViewer historicalContextObjectListChatViewer;

    /**
     * Creates new form PromptContextBuilder
     */
    @Inject
    public PromptContextBuilder(@Named("extensionToSyntaxMap") Map<String, String> extensionToSyntaxMap,
                                ContextQueryDao contextQueryDao,
                                InquiryDao inquiryDao,
                                CodeModificationHistoryDao codeModificationHistoryDao,
                                @Named("historyFetchingTaskExecutor") LimitedSwingWorkerExecutor historyFetchingTaskExecutor,
                                @Assisted PromptContextService promptContextService) {
        this.historicalContextObjectViewer = new HistoricalContextObjectViewer(extensionToSyntaxMap, contextQueryDao, historyFetchingTaskExecutor);
        this.historicalContextObjectListViewer = new HistoricalContextObjectListViewer(historicalContextObjectViewer);
        this.historicalContextModificationListViewer = new HistoricalContextModificationListViewer(historicalContextObjectViewer, historicalContextObjectListViewer, codeModificationHistoryDao, historyFetchingTaskExecutor);
        this.historicalContextInquiryListViewer = new HistoricalContextInquiryListViewer(inquiryDao, historicalContextObjectViewer, historicalContextObjectListViewer, historyFetchingTaskExecutor);
        this.historicalContextModificationListViewer.setHistoricalContextInquiryListViewer(historicalContextInquiryListViewer);
        this.historicalContextInquiryListViewer.setHistoricalContextModificationListViewer(historicalContextModificationListViewer);
        this.historicalContextObjectListViewer.setHistoricalContextModificationListViewer(historicalContextModificationListViewer);
        this.historicalContextObjectListViewer.setHistoricalContextInquiryListViewer(historicalContextInquiryListViewer);
        this.historicalContextObjectViewer.setHistoricalContextModificationListViewer(historicalContextModificationListViewer);
        this.historicalContextObjectViewer.setHistoricalContextInquiryListViewer(historicalContextInquiryListViewer);
        this.historicalContextObjectViewer.setHistoricalContextObjectListViewer(historicalContextObjectListViewer);
        this.historicalContextObjectListChatViewer = new HistoricalContextObjectListChatViewer(extensionToSyntaxMap, contextQueryDao, historyFetchingTaskExecutor, historicalContextObjectListViewer);
        this.historicalContextObjectListViewer.setHistoricalContextObjectListChatViewer(historicalContextObjectListChatViewer);
        if (promptContextService.getPromptContext() != null) {
            this.historicalContextObjectListViewer.updateContextObjectList(promptContextService.getPromptContext());
        }
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.jSplitPane1.setDividerLocation(800);
        this.jSplitPane2.setLeftComponent(historicalContextObjectViewer);
        this.jSplitPane2.setDividerLocation(350);
        this.jSplitPane3.setLeftComponent(historicalContextModificationListViewer);
        this.jSplitPane3.setDividerLocation(225);
        this.jSplitPane3.setRightComponent(historicalContextInquiryListViewer);
        this.jSplitPane4.setLeftComponent(historicalContextObjectListViewer);
        this.jSplitPane4.setRightComponent(historicalContextObjectListChatViewer);
        this.historicalContextObjectListChatViewer.getCancelButton().addActionListener(e -> {
            dispose();
        });
        this.historicalContextObjectListChatViewer.getSaveChangesButton().addActionListener(e -> {
            promptContextService.savePromptContext(historicalContextObjectListViewer.getContextObjectArrayList());
            dispose();
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jBScrollPane3 = new JBScrollPane();
        jList3 = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel7 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel9 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jBScrollPane4 = new JBScrollPane();
        jList4 = new javax.swing.JList<>();
        jPanel10 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel13 = new javax.swing.JPanel();
        jToolBar7 = new javax.swing.JToolBar();
        jBScrollPane8 = new JBScrollPane();
        jList8 = new javax.swing.JList<>();
        jPanel14 = new javax.swing.JPanel();
        jToolBar8 = new javax.swing.JToolBar();
        jBScrollPane9 = new JBScrollPane();
        jList9 = new javax.swing.JList<>();
        jPanel8 = new javax.swing.JPanel();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel11 = new javax.swing.JPanel();
        jToolBar9 = new javax.swing.JToolBar();
        jBScrollPane10 = new JBScrollPane();
        jList10 = new javax.swing.JList<>();
        jPanel12 = new javax.swing.JPanel();
        jToolBar10 = new javax.swing.JToolBar();
        jBScrollPane11 = new JBScrollPane();
        jList11 = new javax.swing.JList<>();

        jList3.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane3.setViewportView(jList3);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setDividerLocation(400);

        jToolBar3.setRollover(true);

        jList4.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane4.setViewportView(jList4);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jBScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jBScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel9);

        jToolBar7.setRollover(true);

        jList8.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane8.setViewportView(jList8);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jBScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
            .addComponent(jToolBar7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jToolBar7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jBScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
        );

        jSplitPane3.setLeftComponent(jPanel13);

        jToolBar8.setRollover(true);

        jList9.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane9.setViewportView(jList9);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar8, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
            .addComponent(jBScrollPane9)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jToolBar8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE))
        );

        jSplitPane3.setRightComponent(jPanel14);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3)
        );

        jSplitPane2.setRightComponent(jPanel10);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );

        jSplitPane1.setLeftComponent(jPanel7);

        jToolBar9.setRollover(true);

        jList10.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane10.setViewportView(jList10);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jBScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
            .addComponent(jToolBar9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jToolBar9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jBScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
        );

        jSplitPane4.setLeftComponent(jPanel11);

        jToolBar10.setRollover(true);

        jList11.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane11.setViewportView(jList11);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jBScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jToolBar10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jBScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE))
        );

        jSplitPane4.setRightComponent(jPanel12);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane4)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane4)
        );

        jSplitPane1.setRightComponent(jPanel8);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1375, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> jList10;
    private javax.swing.JList<String> jList11;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList8;
    private javax.swing.JList<String> jList9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private JBScrollPane jBScrollPane10;
    private JBScrollPane jBScrollPane11;
    private JBScrollPane jBScrollPane3;
    private JBScrollPane jBScrollPane4;
    private JBScrollPane jBScrollPane8;
    private JBScrollPane jBScrollPane9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JToolBar jToolBar10;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JToolBar jToolBar8;
    private javax.swing.JToolBar jToolBar9;
    // End of variables declaration//GEN-END:variables
}
