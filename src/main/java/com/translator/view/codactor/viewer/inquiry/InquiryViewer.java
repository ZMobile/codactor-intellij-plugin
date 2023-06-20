package com.translator.view.codactor.viewer.inquiry;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.translator.CodactorInjector;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.file.MassCodeFileGeneratorService;
import com.translator.service.codactor.inquiry.InquiryService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.factory.dialog.PromptContextBuilderDialogFactory;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author zantehays
 */
public class InquiryViewer extends JPanel {
    private Inquiry inquiry;
    private JToolBar toolbar;
    private JButton otherInquiriesButton;
    private JButton newInquiryButton;
    private JButton discardInquiryButton;
    private InquiryService inquiryService;
    private InquiryChatListViewer inquiryChatListViewer;
    private InquiryChatBoxViewer inquiryChatBoxViewer;
    private InquiryListViewer inquiryListViewer;
    private HistoricalModificationListViewer historicalModificationListViewer;

    @Inject
    public InquiryViewer(Project project,
                         CodactorToolWindowService codactorToolWindowService,
                         MassCodeFileGeneratorService massCodeFileGeneratorService,
                         InquiryService inquiryService,
                         OpenAiModelService openAiModelService,
                         PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                         PromptContextServiceFactory promptContextServiceFactory) {
        initComponents(project, codactorToolWindowService, massCodeFileGeneratorService, inquiryService, openAiModelService, promptContextBuilderDialogFactory, promptContextServiceFactory);
    }

    private void initComponents(Project project,
                                 CodactorToolWindowService codactorToolWindowService,
                                 MassCodeFileGeneratorService massCodeFileGeneratorService,
                                 InquiryService inquiryService,
                                 OpenAiModelService openAiModelService,
                                 PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                                 PromptContextServiceFactory promptContextServiceFactory) {
        this.inquiryService = inquiryService;
        inquiryChatListViewer = new InquiryChatListViewer(project, codactorToolWindowService, massCodeFileGeneratorService, inquiryService, openAiModelService, promptContextBuilderDialogFactory, promptContextServiceFactory);
        inquiryChatBoxViewer = new InquiryChatBoxViewer();

        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorderPainted(false);

        otherInquiriesButton = new JButton("Previous Inquiries");
        otherInquiriesButton.setFocusable(false);
        otherInquiriesButton.setHorizontalTextPosition(SwingConstants.CENTER);
        otherInquiriesButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        otherInquiriesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherInquiriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inquiryListViewer.updateInquiryList();
                codactorToolWindowService.openInquiryListViewerToolWindow();
            }
        });
        toolbar.add(otherInquiriesButton);

        newInquiryButton = new JButton("New Inquiry");
        newInquiryButton.setFocusable(false);
        newInquiryButton.setHorizontalTextPosition(SwingConstants.CENTER);
        newInquiryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        newInquiryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        toolbar.add(newInquiryButton);

        discardInquiryButton = new JButton("Discard Inquiry");
        discardInquiryButton.setFocusable(false);
        discardInquiryButton.setHorizontalTextPosition(SwingConstants.CENTER);
        discardInquiryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        discardInquiryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        discardInquiryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inquiry.getAfterCode() != null || !inquiry.getChats().isEmpty()) {
                    inquiryChatListViewer.updateInquiryContents(new Inquiry(null, null, null, null, null, null, null, null, null, null));
                }
                /*ProvisionalModificationCustomizer provisionalModificationCustomizer = new ProvisionalModificationCustomizer(fileModification, codeModificationService, fileModificationTrackerService, aiTaskExecutor, codeSnippetListViewer, splitPaneService);
                provisionalModificationCustomizer.setVisible(true);*/
            }
        });

        toolbar.add(discardInquiryButton);

        //CodeSnippetListViewer codeSnippetListViewer = this;
        // Add inquiryChatListViewer and inquiryChatBoxViewer to the main panel
        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(inquiryChatListViewer, BorderLayout.CENTER);
        add(inquiryChatBoxViewer, BorderLayout.SOUTH);
    }

    private void askNewGeneralInquiryQuestion(String question) {
        inquiryService.createGeneralInquiry(question);
    }


    private void askInquiryQuestion(String subjectRecordId, RecordType recordType, String question, String filePath) {
        //jToolBar3.setVisible(false); Need to keep toolbar but remove "What is this code?" buttons etc.
        inquiryService.createInquiry(subjectRecordId, recordType, question, filePath);
    }

    public void askContinuedQuestion(String previousInquiryChatId, String question) {
        assert inquiry != null;
        inquiryService.continueInquiry(previousInquiryChatId, question);
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
        this.inquiryListViewer.updateInquiryList(inquiry.g);
    }

    public InquiryChatBoxViewer getInquiryChatBoxViewer() {
        return inquiryChatBoxViewer;
    }

    public InquiryChatListViewer getInquiryChatListViewer() {
        return inquiryChatListViewer;
    }

    public void setInquiryListViewer(InquiryListViewer inquiryListViewer) {
        this.inquiryListViewer = inquiryListViewer;
    }

    public void setHistoricalModificationListViewer(HistoricalModificationListViewer historicalModificationListViewer) {
        this.historicalModificationListViewer = historicalModificationListViewer;
    }
}