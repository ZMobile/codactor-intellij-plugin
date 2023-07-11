package com.translator.view.codactor.viewer.context;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.history.data.HistoricalInquiryDataHolder;
import com.translator.model.codactor.history.data.HistoricalObjectDataHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.view.codactor.renderer.HistoricalObjectDataHolderRenderer;
import com.translator.view.codactor.renderer.SeparatorListCellRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class HistoricalContextInquiryListViewer extends JPanel {
    private JList<HistoricalObjectDataHolder> inquiryList;
    private JBScrollPane inquiryListScrollPane;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JBLabel previousInquiriesLabel;
    private JButton addButton;
    private InquiryDao inquiryDao;
    private HistoricalContextObjectViewer historicalContextObjectViewer;
    private HistoricalContextObjectListViewer historicalContextObjectListViewer;
    private HistoricalContextModificationListViewer historicalContextModificationListViewer;
    private JPanel parentComponent = this;

    public HistoricalContextInquiryListViewer(InquiryDao inquiryDao,
                             HistoricalContextObjectViewer historicalContextObjectViewer,
                             HistoricalContextObjectListViewer historicalContextObjectListViewer) {
        this.inquiryDao = inquiryDao;
        this.historicalContextObjectViewer = historicalContextObjectViewer;
        this.historicalContextObjectListViewer = historicalContextObjectListViewer;
        updateInquiryList();
        initComponents();
    }

    private void initComponents() {
        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        previousInquiriesLabel = new JBLabel("Previous Inquiries");
        previousInquiriesLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        previousInquiriesLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        previousInquiriesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(previousInquiriesLabel);

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
                if (inquiryList.getSelectedIndex() != -1) {
                    HistoricalObjectDataHolder selectedInquiry = inquiryList.getSelectedValue();
                    historicalContextObjectListViewer.addContextObject(selectedInquiry);
                    inquiryList.clearSelection();
                }
            }
        });
        jToolBar3.add(addButton);

        inquiryList = new JList<>();
        inquiryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inquiryListScrollPane = new JBScrollPane(inquiryList);
        inquiryListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Add a horizontal line to separate each FileModification
        inquiryList.setFixedCellHeight(80);
        inquiryList.setCellRenderer(new SeparatorListCellRenderer<>(new HistoricalObjectDataHolderRenderer()));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(inquiryListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                        .addComponent(inquiryListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );

        updateInquiryList(new ArrayList<>());

        inquiryList.addListSelectionListener(e -> {
            JList<HistoricalObjectDataHolder> modificationList = historicalContextModificationListViewer.getModificationList();
            ListSelectionListener[] listSelectionListeners = modificationList.getListSelectionListeners();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                modificationList.removeListSelectionListener(listSelectionListener);
            }
            modificationList.clearSelection();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                modificationList.addListSelectionListener(listSelectionListener);
            }
            JList<HistoricalObjectDataHolder> contextObjectList = historicalContextObjectListViewer.getContextObjectList();
            listSelectionListeners = contextObjectList.getListSelectionListeners();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                contextObjectList.removeListSelectionListener(listSelectionListener);
            }
            contextObjectList.clearSelection();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                contextObjectList.addListSelectionListener(listSelectionListener);
            }
            if (!e.getValueIsAdjusting()) {
                int index = inquiryList.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                HistoricalObjectDataHolder historicalObjectDataHolder = inquiryList.getModel().getElementAt(index);
                System.out.println("This gets called:");
                Gson gson = new Gson();
                System.out.println(gson.toJson(historicalObjectDataHolder));
                historicalContextObjectViewer.updateHistoricalContextObjectHolder(historicalObjectDataHolder);
            }
        });
    }

    public void updateInquiryList() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<Inquiry> inquiries = inquiryDao.getRecentInquiries();
            System.out.println("Testo: ");
            Gson gson = new Gson();
            System.out.println(gson.toJson(inquiries.get(0)));
            if (inquiries != null) {
                updateInquiryList(inquiries);
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Failed to load inquiry history", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void updateInquiryList(List<Inquiry> inquiries) {
        DefaultListModel<HistoricalObjectDataHolder> model = new DefaultListModel<>();
        for (Inquiry inquiry : inquiries) {
            HistoricalInquiryDataHolder historicalInquiryDataHolder = new HistoricalInquiryDataHolder(inquiry);
            HistoricalObjectDataHolder historicalObjectDataHolder = new HistoricalObjectDataHolder(historicalInquiryDataHolder);
            model.addElement(historicalObjectDataHolder);
        }
        inquiryList.setModel(model);
    }

    public void setHistoricalContextModificationListViewer(HistoricalContextModificationListViewer historicalContextModificationListViewer) {
        this.historicalContextModificationListViewer = historicalContextModificationListViewer;
    }

    public JList<HistoricalObjectDataHolder> getInquiryList() {
        return inquiryList;
    }
}

