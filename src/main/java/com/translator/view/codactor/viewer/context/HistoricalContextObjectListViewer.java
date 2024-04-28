package com.translator.view.codactor.viewer.context;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.model.codactor.ai.history.data.HistoricalObjectDataHolder;
import com.translator.view.codactor.renderer.HistoricalObjectDataHolderRenderer;
import com.translator.view.codactor.renderer.SeparatorListCellRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class HistoricalContextObjectListViewer extends JPanel {
    private JList<HistoricalObjectDataHolder> contextObjectList;
    private JBScrollPane contextObjectListScrollPane;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JBLabel contextListLabel;
    private JButton removeButton;
    private HistoricalContextModificationListViewer historicalContextModificationListViewer;
    private HistoricalContextInquiryListViewer historicalContextInquiryListViewer;
    private HistoricalContextObjectViewer historicalContextObjectViewer;
    private HistoricalContextObjectListChatViewer historicalContextObjectListChatViewer;

    public HistoricalContextObjectListViewer(HistoricalContextObjectViewer historicalContextObjectViewer) {
        this.historicalContextObjectViewer = historicalContextObjectViewer;
        initComponents();
    }


    private void initComponents() {
        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        contextListLabel = new JBLabel("Assembled Context");
        contextListLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        contextListLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        contextListLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(contextListLabel);

        jToolBar3= new JToolBar();
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
                if (contextObjectList.getSelectedIndex() != -1) {
                    HistoricalObjectDataHolder selectedObject = contextObjectList.getSelectedValue();
                    DefaultListModel<HistoricalObjectDataHolder> model = (DefaultListModel<HistoricalObjectDataHolder>) contextObjectList.getModel();
                    model.removeElement(selectedObject);
                    historicalContextObjectListChatViewer.removeContextObject(selectedObject);
                } else if (contextObjectList.getModel().getSize() > 0) {
                    HistoricalObjectDataHolder selectedObject = contextObjectList.getModel().getElementAt(contextObjectList.getModel().getSize() - 1);
                    DefaultListModel<HistoricalObjectDataHolder> model = (DefaultListModel<HistoricalObjectDataHolder>) contextObjectList.getModel();
                    model.removeElement(selectedObject);
                    historicalContextObjectListChatViewer.removeContextObject(selectedObject);
                }
            }
        });
        jToolBar3.add(removeButton);

        contextObjectList = new JList<>();
        contextObjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contextObjectListScrollPane = new JBScrollPane(contextObjectList);

        // Add a horizontal line to separate each FileModification
        contextObjectList.setFixedCellHeight(80);
        contextObjectList.setCellRenderer(new SeparatorListCellRenderer<>(new HistoricalObjectDataHolderRenderer()));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(contextObjectListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                        .addComponent(contextObjectListScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );

        updateContextObjectList(new ArrayList<>());

        contextObjectList.addListSelectionListener(e -> {
            JList<HistoricalObjectDataHolder> inquiryList = historicalContextInquiryListViewer.getInquiryList();
            ListSelectionListener[] listSelectionListeners = inquiryList.getListSelectionListeners();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                inquiryList.removeListSelectionListener(listSelectionListener);
            }
            inquiryList.clearSelection();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                inquiryList.addListSelectionListener(listSelectionListener);
            }
            JList<HistoricalObjectDataHolder> modificationList = historicalContextModificationListViewer.getModificationList();
            listSelectionListeners = modificationList.getListSelectionListeners();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                modificationList.removeListSelectionListener(listSelectionListener);
            }
            modificationList.clearSelection();
            for (ListSelectionListener listSelectionListener : listSelectionListeners) {
                modificationList.addListSelectionListener(listSelectionListener);
            }
            if (!e.getValueIsAdjusting()) {
                int index = contextObjectList.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                //Set the background of the object at this index in the jlist to blue:
                HistoricalObjectDataHolder historicalObjectDataHolder = contextObjectList.getModel().getElementAt(index);
                historicalContextObjectViewer.updateHistoricalContextObjectHolder(historicalObjectDataHolder);
            }
        });
    }

    public void updateContextObjectList(List<HistoricalObjectDataHolder> contextObjectDataHolders) {
        DefaultListModel<HistoricalObjectDataHolder> model = new DefaultListModel<>();
        for (HistoricalObjectDataHolder contextObjectDataHolder : contextObjectDataHolders) {
            model.addElement(contextObjectDataHolder);
        }
        contextObjectList.setModel(model);
        if (historicalContextObjectListChatViewer != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    historicalContextObjectListChatViewer.updateChatContents();
                }
            });
        }
    }

    public void addContextObject(HistoricalObjectDataHolder contextObjectDataHolder) {
        DefaultListModel<HistoricalObjectDataHolder> model = (DefaultListModel<HistoricalObjectDataHolder>) contextObjectList.getModel();
        model.addElement(contextObjectDataHolder);
        contextObjectList.setModel(model);
        historicalContextObjectListChatViewer.addContextObject(contextObjectDataHolder);
    }

    public void removeContextObject(int index) {
        DefaultListModel<HistoricalObjectDataHolder> model = (DefaultListModel<HistoricalObjectDataHolder>) contextObjectList.getModel();
        HistoricalObjectDataHolder historicalObjectDataHolder = model.getElementAt(index);
        model.removeElement(index);
        contextObjectList.setModel(model);
        historicalContextObjectListChatViewer.removeContextObject(historicalObjectDataHolder);
    }

    public JList<HistoricalObjectDataHolder> getContextObjectList() {
        return contextObjectList;
    }

    public List<HistoricalObjectDataHolder> getContextObjectArrayList() {
        List<HistoricalObjectDataHolder> contextObjectDataHolders = new ArrayList<>();
        for (int i = 0; i < contextObjectList.getModel().getSize(); i++) {
            contextObjectDataHolders.add(contextObjectList.getModel().getElementAt(i));
        }
        return contextObjectDataHolders;
    }

    public void setHistoricalContextModificationListViewer(HistoricalContextModificationListViewer historicalContextModificationListViewer) {
        this.historicalContextModificationListViewer = historicalContextModificationListViewer;
    }

    public void setHistoricalContextInquiryListViewer(HistoricalContextInquiryListViewer historicalContextInquiryListViewer) {
        this.historicalContextInquiryListViewer = historicalContextInquiryListViewer;
    }

    public void setHistoricalContextObjectListChatViewer(HistoricalContextObjectListChatViewer historicalContextObjectListChatViewer) {
        this.historicalContextObjectListChatViewer = historicalContextObjectListChatViewer;
    }
}
