package com.translator.view.viewer.context;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.model.api.translator.history.DesktopCodeModificationHistoryResponseResource;
import com.translator.model.history.data.HistoricalContextModificationDataHolder;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.view.renderer.HistoricalContextObjectHolderRenderer;
import com.translator.view.renderer.SeparatorListCellRenderer;
import com.translator.worker.LimitedSwingWorker;
import com.translator.worker.LimitedSwingWorkerExecutor;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class HistoricalContextModificationListViewer extends JPanel {
    private JList<HistoricalContextObjectDataHolder> modificationList;
    private JBScrollPane modificationListScrollPane;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JBLabel previousModificationsLabel;
    private JButton addButton;
    private CodeModificationHistoryDao codeModificationHistoryDao;
    private HistoricalContextObjectViewer historicalContextObjectViewer;
    private HistoricalContextObjectListViewer historicalContextObjectListViewer;
    private HistoricalContextInquiryListViewer historicalContextInquiryListViewer;
    private JPanel parentComponent = this;

    public HistoricalContextModificationListViewer(HistoricalContextObjectViewer historicalContextObjectViewer,
                                                    HistoricalContextObjectListViewer historicalContextObjectListViewer,
                                                   CodeModificationHistoryDao codeModificationHistoryDao) {
        this.historicalContextObjectViewer = historicalContextObjectViewer;
        this.codeModificationHistoryDao = codeModificationHistoryDao;
        updateModificationList();
        initComponents();
        this.historicalContextObjectListViewer = historicalContextObjectListViewer;
    }

    private void initComponents() {
        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        previousModificationsLabel = new JBLabel("Previous Modifications");
        previousModificationsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        previousModificationsLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        previousModificationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(previousModificationsLabel);

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
                if (modificationList.getSelectedIndex() != -1) {
                    HistoricalContextObjectDataHolder selectedModification = modificationList.getSelectedValue();
                    historicalContextObjectListViewer.addContextObject(selectedModification);
                    modificationList.clearSelection();
                }
            }
        });
        jToolBar3.add(addButton);

        modificationList = new JList<>();
        modificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modificationListScrollPane = new JBScrollPane(modificationList);
        modificationListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Add a horizontal line to separate each FileModification
        modificationList.setFixedCellHeight(80);
        modificationList.setCellRenderer(new SeparatorListCellRenderer<>(new HistoricalContextObjectHolderRenderer()));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );

        updateModificationList(new ArrayList<>());

        modificationList.addListSelectionListener(e -> {
            JList<HistoricalContextObjectDataHolder> inquiryList = historicalContextInquiryListViewer.getInquiryList();
            ListSelectionListener[] listSelectionListeners = inquiryList.getListSelectionListeners();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                inquiryList.removeListSelectionListener(listSelectionListener);
            }
            inquiryList.clearSelection();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                inquiryList.addListSelectionListener(listSelectionListener);
            }
            JList<HistoricalContextObjectDataHolder> contextObjectList = historicalContextObjectListViewer.getContextObjectList();
            listSelectionListeners = contextObjectList.getListSelectionListeners();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                contextObjectList.removeListSelectionListener(listSelectionListener);
            }
            contextObjectList.clearSelection();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                contextObjectList.addListSelectionListener(listSelectionListener);
            }
            if (!e.getValueIsAdjusting()) {
                int index = modificationList.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                //Set the background of the object at this index in the jlist to blue:
                HistoricalContextObjectDataHolder historicalContextObjectDataHolder = modificationList.getModel().getElementAt(index);
                historicalContextObjectViewer.updateHistoricalContextObjectHolder(historicalContextObjectDataHolder);
            }
        });
    }

    public void updateModificationList() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            DesktopCodeModificationHistoryResponseResource modifications = codeModificationHistoryDao.getRecentModifications();
            if (modifications != null) {
                updateModificationList(modifications.getModificationHistory());
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Failed to load modification history", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void updateModificationList(List<HistoricalContextModificationDataHolder> fileModifications) {
        DefaultListModel<HistoricalContextObjectDataHolder> model = new DefaultListModel<>();
        for (HistoricalContextModificationDataHolder historicalContextModificationDataHolder : fileModifications) {
            HistoricalContextObjectDataHolder historicalContextObjectDataHolder = new HistoricalContextObjectDataHolder(historicalContextModificationDataHolder);
            model.addElement(historicalContextObjectDataHolder);
        }
        modificationList.setModel(model);
    }

    public void setHistoricalContextInquiryListViewer(HistoricalContextInquiryListViewer historicalContextInquiryListViewer) {
        this.historicalContextInquiryListViewer = historicalContextInquiryListViewer;
    }

    public JList<HistoricalContextObjectDataHolder> getModificationList() {
        return modificationList;
    }
}

