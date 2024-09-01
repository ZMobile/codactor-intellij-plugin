package com.translator.view.codactor.viewer.inquiry;

import com.google.inject.Injector;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.translator.CodactorInjector;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.renderer.InquiryRenderer;
import com.translator.view.codactor.renderer.SeparatorListCellRenderer;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class InquiryListViewer extends JPanel {
    private Project project;
    private JList<Inquiry> inquiryList;
    private JBScrollPane inquiryListScrollPane;
    private JToolBar jToolBar2;
    private JButton newInquiryButton;
    private HistoricalModificationListViewer historicalModificationListViewer;
    private CodactorToolWindowService codactorToolWindowService;
    private InquiryDao inquiryDao;
    private InquiryViewerFactory inquiryViewerFactory;
    private JPanel parentComponent = this;

    public InquiryListViewer(Project project,
                             CodactorToolWindowService codactorToolWindowService,
                             InquiryDao inquiryDao,
                             InquiryViewerFactory inquiryViewerFactory) {
        this.project = project;
        this.historicalModificationListViewer = null;
        this.codactorToolWindowService = codactorToolWindowService;
        this.inquiryDao = inquiryDao;
        this.inquiryViewerFactory = inquiryViewerFactory;
        initComponents();
    }

    private void initComponents() {
        jToolBar2 = new JToolBar();
        jToolBar2.setBackground(Color.darkGray);
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        newInquiryButton = new JButton("New Inquiry");
        Border emptyBorder = BorderFactory.createEmptyBorder();
        newInquiryButton.setBorder(emptyBorder);
        newInquiryButton.setFocusable(false);
        newInquiryButton.setHorizontalTextPosition(SwingConstants.CENTER);
        newInquiryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        newInquiryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(newInquiryButton);

        newInquiryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (historicalModificationListViewer == null) {
                    Injector injector = CodactorInjector.getInstance().getInjector(project);
                    historicalModificationListViewer = injector.getInstance(HistoricalModificationListViewer.class);
                }
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
                        .addComponent(inquiryListScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
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
                if (inquiryWithChats.getError() != null) {
                    JOptionPane.showMessageDialog(null, inquiryWithChats.getError(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                InquiryViewer inquiryViewer = inquiryViewerFactory.create();
                inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiryWithChats);
                codactorToolWindowService.createInquiryViewerToolWindow(inquiryViewer);
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
