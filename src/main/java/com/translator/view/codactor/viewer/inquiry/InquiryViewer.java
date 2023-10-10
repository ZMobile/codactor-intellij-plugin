package com.translator.view.codactor.viewer.inquiry;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.translator.CodactorInjector;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.modification.RecordType;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.functions.InquiryChatListFunctionCallCompressorService;
import com.translator.service.codactor.functions.InquiryFunctionCallProcessorService;
import com.translator.service.codactor.inquiry.InquiryService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author zantehays
 */
public class InquiryViewer extends JPanel {
    private Project project;
    private Inquiry inquiry;
    private JToolBar toolbar;
    private JButton otherInquiriesButton;
    private JButton newInquiryButton;
    private JButton discardInquiryButton;
    private InquiryService inquiryService;
    private CodactorToolWindowService codactorToolWindowService;
    private InquiryChatListViewer inquiryChatListViewer;
    private InquiryChatBoxViewer inquiryChatBoxViewer;
    private InquiryListViewer inquiryListViewer;
    private HistoricalModificationListViewer historicalModificationListViewer;

    @Inject
    public InquiryViewer(Gson gson,
                         Project project,
                         CodactorToolWindowService codactorToolWindowService,
                         MultiFileCreateDialogFactory multiFileCreateDialogFactory,
                         InquiryService inquiryService,
                         PromptContextServiceFactory promptContextServiceFactory,
                         TextAreaHeightCalculatorService textAreaHeightCalculatorService,
                         InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService,
                         InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService) {
        this.project = project;
        this.inquiryService = inquiryService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.inquiryChatListViewer = new InquiryChatListViewer(gson, this, textAreaHeightCalculatorService, promptContextServiceFactory, codactorToolWindowService, inquiryChatListFunctionCallCompressorService, inquiryFunctionCallProcessorService, multiFileCreateDialogFactory);
        this.inquiryChatBoxViewer = new InquiryChatBoxViewer(this);
        initComponents();
    }

    private void initComponents() {
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
                if (inquiryListViewer == null) {
                    Injector injector = CodactorInjector.getInstance().getInjector(project);
                    inquiryListViewer = injector.getInstance(InquiryListViewer.class);
                }
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
                    inquiryChatListViewer.updateInquiryContents(new Inquiry.Builder().build());
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

    public void askNewGeneralInquiryQuestion(String question) {
        String model = inquiryChatListViewer.getOpenAiModelService().getSelectedOpenAiModel();
        inquiryService.createGeneralInquiry(this, question, model);
    }

    public void askInquiryQuestion(String subjectRecordId, RecordType recordType, String question, String filePath) {
        //inquiryChatBoxViewer.getToolBar().setVisible(false);
        String model = inquiryChatListViewer.getOpenAiModelService().getSelectedOpenAiModel();
        inquiryService.createInquiry(this, subjectRecordId, recordType, question, filePath, model);
    }

    public void askContinuedQuestion(String previousInquiryChatId, String question) {
        assert inquiry != null;
        String model = inquiryChatListViewer.getOpenAiModelService().getSelectedOpenAiModel();
        inquiryService.continueInquiry(this, previousInquiryChatId, question, model);
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
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

    public void setLoadingChat(boolean loadingChat) {
        this.inquiryChatBoxViewer.getAskButton().setEnabled(!loadingChat);
        this.getInquiryChatListViewer().getEditItem().setEnabled(!loadingChat);
        this.getInquiryChatListViewer().getRegenerateItem().setEnabled(!loadingChat);
        this.getInquiryChatListViewer().getNextChat().setEnabled(!loadingChat);
        this.getInquiryChatListViewer().getPreviousChat().setEnabled(!loadingChat);
    }

    public Inquiry getInquiry() {
        return inquiry;
    }
}