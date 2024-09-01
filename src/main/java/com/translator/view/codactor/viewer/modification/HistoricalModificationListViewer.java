package com.translator.view.codactor.viewer.modification;

import com.google.inject.Injector;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.translator.CodactorInjector;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.model.codactor.api.translator.history.DesktopCodeModificationHistoryResponseResource;
import com.translator.model.codactor.ai.history.data.HistoricalFileModificationDataHolder;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.ai.modification.RecordType;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.renderer.HistoricalCompletedFileModificationRenderer;
import com.translator.view.codactor.renderer.SeparatorListCellRenderer;
import com.translator.view.codactor.viewer.inquiry.InquiryListViewer;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HistoricalModificationListViewer extends JPanel {
    private Project project;
    private JList<HistoricalFileModificationDataHolder> modificationList;
    private JBScrollPane modificationListScrollPane;
    private JToolBar jToolBar2;
    private JButton otherInquiriesButton;
    private InquiryListViewer inquiryListViewer;
    private CodactorToolWindowService codactorToolWindowService;
    private CodeModificationHistoryDao codeModificationHistoryDao;
    private InquiryViewerFactory inquiryViewerFactory;
    private JPanel parentComponent = this;

    public HistoricalModificationListViewer(CodactorToolWindowService codactorToolWindowService,
                                            CodeModificationHistoryDao codeModificationHistoryDao,
                                            InquiryViewerFactory inquiryViewerFactory) {
        this.inquiryListViewer = null;
        this.codactorToolWindowService = codactorToolWindowService;
        this.codeModificationHistoryDao = codeModificationHistoryDao;
        this.inquiryViewerFactory = inquiryViewerFactory;
        initComponents();
    }

    private void initComponents() {
        jToolBar2 = new JToolBar();
        jToolBar2.setBackground(Color.darkGray);
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        otherInquiriesButton = new JButton("Previous Inquiries");
        Border emptyBorder = BorderFactory.createEmptyBorder();
        otherInquiriesButton.setBorder(emptyBorder);
        otherInquiriesButton.setFocusable(false);
        otherInquiriesButton.setHorizontalTextPosition(SwingConstants.CENTER);
        otherInquiriesButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        otherInquiriesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherInquiriesButton.addActionListener(e -> {
            if (inquiryListViewer == null) {
                Injector injector = CodactorInjector.getInstance().getInjector(project);
                inquiryListViewer = injector.getInstance(InquiryListViewer.class);
            }
            inquiryListViewer.updateInquiryList();
            codactorToolWindowService.openInquiryListViewerToolWindow();
        });
        jToolBar2.add(otherInquiriesButton);

        otherInquiriesButton.addActionListener(e -> {
            if (inquiryListViewer == null) {
                Injector injector = CodactorInjector.getInstance().getInjector(project);
                inquiryListViewer = injector.getInstance(InquiryListViewer.class);
            }
            inquiryListViewer.updateInquiryList();
            codactorToolWindowService.openInquiryListViewerToolWindow();
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
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );

        updateModificationList(new ArrayList<>());

        modificationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = modificationList.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                if (index == 0) {
                    Inquiry inquiry = new Inquiry.Builder()
                            .build();
                    InquiryViewer inquiryViewer = inquiryViewerFactory.create();
                    inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                    inquiryViewer.setLoadingChat(false);
                    codactorToolWindowService.createInquiryViewerToolWindow(inquiryViewer);
                    return;
                }
                HistoricalFileModificationDataHolder historicalFileModificationDataHolder = modificationList.getModel().getElementAt(index);
                Inquiry temporaryInquiry;
                if (historicalFileModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                    FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = historicalFileModificationDataHolder.getFileModificationSuggestionModificationRecord();
                    temporaryInquiry = new Inquiry.Builder()
                            .withModificationId(fileModificationSuggestionModificationRecord.getModificationId())
                            .withSubjectRecordId(fileModificationSuggestionModificationRecord.getId())
                            .withSubjectRecordType(RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION)
                            .withFilePath(fileModificationSuggestionModificationRecord.getFilePath())
                            .withBeforeCode(fileModificationSuggestionModificationRecord.getBeforeText())
                            .withAfterCode(fileModificationSuggestionModificationRecord.getEditedCode())
                            .withDescription(fileModificationSuggestionModificationRecord.getModification())
                            .withModificationType(fileModificationSuggestionModificationRecord.getModificationType())
                            .build();
                } else {
                    FileModificationSuggestionRecord fileModificationSuggestionRecord = historicalFileModificationDataHolder.getFileModificationSuggestionRecord();
                    temporaryInquiry = new Inquiry.Builder()
                            .withModificationId(fileModificationSuggestionRecord.getModificationId())
                            .withSubjectRecordId(fileModificationSuggestionRecord.getId())
                            .withSubjectRecordType(RecordType.FILE_MODIFICATION_SUGGESTION)
                            .withFilePath(fileModificationSuggestionRecord.getFilePath())
                            .withBeforeCode(fileModificationSuggestionRecord.getBeforeCode())
                            .withAfterCode(fileModificationSuggestionRecord.getSuggestedCode())
                            .withDescription(fileModificationSuggestionRecord.getModification())
                            .withModificationType(fileModificationSuggestionRecord.getModificationType())
                            .build();
                }
                InquiryViewer inquiryViewer = inquiryViewerFactory.create();
                inquiryViewer.getInquiryChatListViewer().updateInquiryContents(temporaryInquiry);
                codactorToolWindowService.createInquiryViewerToolWindow(inquiryViewer);
            }
        });
    }

    public void updateModificationList() {
        //Clear current list
        DefaultListModel<HistoricalFileModificationDataHolder> model = new DefaultListModel<>();
        model.addElement(new HistoricalFileModificationDataHolder());
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

    public void updateModificationList(List<HistoricalFileModificationDataHolder> fileModifications) {
        DefaultListModel<HistoricalFileModificationDataHolder> model = new DefaultListModel<>();
        model.addElement(new HistoricalFileModificationDataHolder());
        for (HistoricalFileModificationDataHolder historicalFileModificationDataHolder : fileModifications) {
            model.addElement(historicalFileModificationDataHolder);
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
