package com.translator.view.viewer;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.inquiry.Inquiry;
import com.translator.view.renderer.InquiryRenderer;
import com.translator.view.renderer.SeparatorListCellRenderer;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.worker.LimitedSwingWorker;
import com.translator.worker.LimitedSwingWorkerExecutor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class InquiryListViewer extends JPanel {
    private JList<Inquiry> inquiryList;
    private JBScrollPane inquiryListScrollPane;
    private JToolBar jToolBar2;
    private JButton newInquiryButton;
    private InquiryViewer inquiryViewer;
    private HistoricalModificationListViewer historicalModificationListViewer;
    private CodactorToolWindowService codactorToolWindowService;
    private InquiryDao inquiryDao;
    private JPanel parentComponent = this;

    public InquiryListViewer(InquiryViewer inquiryViewer,
                             CodactorToolWindowService codactorToolWindowService,
                             InquiryDao inquiryDao) {
        this.inquiryViewer = inquiryViewer;
        this.historicalModificationListViewer = null;
        this.codactorToolWindowService = codactorToolWindowService;
        this.inquiryDao = inquiryDao;
        initComponents();
    }

    private void initComponents() {
        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        newInquiryButton = new JButton("New Inquiry");
        newInquiryButton.setFocusable(false);
        newInquiryButton.setHorizontalTextPosition(SwingConstants.CENTER);
        newInquiryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        newInquiryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(newInquiryButton);

        newInquiryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                historicalModificationListViewer.updateModificationList();
                codactorToolWindowService.openHistoricalModificationListViewerToolWindow();
            }
        });

        inquiryList = new JList<>();
        inquiryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inquiryListScrollPane = new JBScrollPane(inquiryList);

        // Add a horizontal line to separate each FileModification
        inquiryList.setFixedCellHeight(80);
        inquiryList.setCellRenderer(new SeparatorListCellRenderer<>(new InquiryRenderer()));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(inquiryListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(inquiryListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );

        updateInquiryList(new ArrayList<>());

        inquiryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = inquiryList.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                Inquiry inquiry = inquiryList.getModel().getElementAt(index);
                Inquiry inquiryWithChats = inquiryDao.getInquiry(inquiry.getId());
                inquiryViewer.updateInquiryContents(inquiryWithChats);
                codactorToolWindowService.openInquiryViewerToolWindow();
            }
        });
    }

    public void updateInquiryList() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<Inquiry> inquiries = inquiryDao.getRecentInquiries();
            if (inquiries != null) {
                updateInquiryList(inquiries);
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Failed to load inquiry history", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return null;
        });
    }

    public void updateInquiryList(List<Inquiry> inquiries) {
        DefaultListModel<Inquiry> model = new DefaultListModel<>();
        for (Inquiry inquiry : inquiries) {
            model.addElement(inquiry);
        }
        inquiryList.setModel(model);
    }

    public void setHistoricalModificationListViewer(HistoricalModificationListViewer historicalModificationListViewer) {
        this.historicalModificationListViewer = historicalModificationListViewer;
    }
}
