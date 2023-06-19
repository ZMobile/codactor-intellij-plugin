package com.translator.view.codactor.viewer.modification;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.model.codactor.api.translator.history.DesktopCodeModificationHistoryResponseResource;
import com.translator.model.codactor.history.data.HistoricalContextModificationDataHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.codactor.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.modification.RecordType;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.renderer.HistoricalCompletedFileModificationRenderer;
import com.translator.view.codactor.renderer.SeparatorListCellRenderer;
import com.translator.view.codactor.viewer.inquiry.InquiryListViewer;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class HistoricalModificationListViewer extends JPanel {
    private Project project;
    private JList<HistoricalContextModificationDataHolder> modificationList;
    private JBScrollPane modificationListScrollPane;
    private JToolBar jToolBar2;
    private JButton otherInquiriesButton;
    private InquiryViewer inquiryViewer;
    private InquiryListViewer inquiryListViewer;
    private CodactorToolWindowService codactorToolWindowService;
    private CodeModificationHistoryDao codeModificationHistoryDao;
    private JPanel parentComponent = this;

    public HistoricalModificationListViewer(InquiryViewer inquiryViewer,
                                            CodactorToolWindowService codactorToolWindowService,
                                            CodeModificationHistoryDao codeModificationHistoryDao) {
        this.inquiryViewer = inquiryViewer;
        this.inquiryListViewer = null;
        this.codactorToolWindowService = codactorToolWindowService;
        this.codeModificationHistoryDao = codeModificationHistoryDao;
        initComponents();
    }

    private void initComponents() {
        jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        otherInquiriesButton = new JButton("Previous Inquiries");
        otherInquiriesButton.setFocusable(false);
        otherInquiriesButton.setHorizontalTextPosition(SwingConstants.CENTER);
        otherInquiriesButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        otherInquiriesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar2.add(otherInquiriesButton);

        otherInquiriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inquiryListViewer.updateInquiryList();
                codactorToolWindowService.openInquiryListViewerToolWindow();
            }
        });

        modificationList = new JList<>();
        modificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modificationListScrollPane = new JBScrollPane(modificationList);

        // Add a horizontal line to separate each FileModification
        modificationList.setFixedCellHeight(80);
        modificationList.setCellRenderer(new SeparatorListCellRenderer<>(new HistoricalCompletedFileModificationRenderer()));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );

        updateModificationList(new ArrayList<>());

        modificationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = modificationList.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                if (index == 0) {
                    Inquiry inquiry = new Inquiry(null, null, null, null, null, null, null, null, null, null);
                    inquiryViewer.updateInquiryContents(inquiry);
                    inquiryViewer.setLoadingChat(false);
                    codactorToolWindowService.openInquiryViewerToolWindow();
                    return;
                }
                HistoricalContextModificationDataHolder historicalContextModificationDataHolder = modificationList.getModel().getElementAt(index);
                Inquiry temporaryInquiry;
                if (historicalContextModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                    FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = historicalContextModificationDataHolder.getFileModificationSuggestionModificationRecord();
                    temporaryInquiry = new Inquiry(null, fileModificationSuggestionModificationRecord.getModificationId(), fileModificationSuggestionModificationRecord.getId(), RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION, fileModificationSuggestionModificationRecord.getFilePath(), fileModificationSuggestionModificationRecord.getBeforeText(), fileModificationSuggestionModificationRecord.getModification(), fileModificationSuggestionModificationRecord.getEditedCode(), fileModificationSuggestionModificationRecord.getModificationType(), null);
                } else {
                    FileModificationSuggestionRecord fileModificationSuggestionRecord = historicalContextModificationDataHolder.getFileModificationSuggestionRecord();
                    temporaryInquiry = new Inquiry(null, fileModificationSuggestionRecord.getModificationId(), fileModificationSuggestionRecord.getId(), RecordType.FILE_MODIFICATION_SUGGESTION, fileModificationSuggestionRecord.getFilePath(), fileModificationSuggestionRecord.getBeforeCode(), fileModificationSuggestionRecord.getModification(), fileModificationSuggestionRecord.getSuggestedCode(), fileModificationSuggestionRecord.getModificationType(), null);
                }
                inquiryViewer.updateInquiryContents(temporaryInquiry);
                codactorToolWindowService.openInquiryViewerToolWindow();
            }
        });
    }

    public void updateModificationList() {
        //Clear current list
        DefaultListModel<HistoricalContextModificationDataHolder> model = new DefaultListModel<>();
        model.addElement(new HistoricalContextModificationDataHolder());
        modificationList.setModel(model);
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            DesktopCodeModificationHistoryResponseResource modifications = codeModificationHistoryDao.getRecentModifications();
            if (modifications != null) {
                updateModificationList(modifications.getModificationHistory());
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Failed to load modification history", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return null;
        });
    }

    public void updateModificationList(List<HistoricalContextModificationDataHolder> fileModifications) {
        DefaultListModel<HistoricalContextModificationDataHolder> model = new DefaultListModel<>();
        model.addElement(new HistoricalContextModificationDataHolder());
        for (HistoricalContextModificationDataHolder historicalContextModificationDataHolder : fileModifications) {
            model.addElement(historicalContextModificationDataHolder);
        }
        modificationList.setModel(model);
    }

    public void setInquiryListViewer(InquiryListViewer inquiryListViewer) {
        this.inquiryListViewer = inquiryListViewer;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
