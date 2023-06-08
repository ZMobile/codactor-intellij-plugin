package com.translator.service.codactor.inquiry;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.modification.RecordType;
import com.translator.service.codactor.code.GptToLanguageTransformerService;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.viewer.InquiryViewer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InquiryServiceImpl implements InquiryService {
    private Project project;
    private InquiryDao inquiryDao;
    private CodactorToolWindowService codactorToolWindowService;
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private PromptContextService promptContextService;
    private GptToLanguageTransformerService gptToLanguageTransformerService;

    @Inject
    public InquiryServiceImpl(Project project,
                              InquiryDao inquiryDao,
                              CodactorToolWindowService codactorToolWindowService,
                              OpenAiApiKeyService openAiApiKeyService,
                              OpenAiModelService openAiModelService,
                              PromptContextService promptContextService,
                              GptToLanguageTransformerService gptToLanguageTransformerService) {
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.codactorToolWindowService = codactorToolWindowService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.promptContextService = promptContextService;
        this.gptToLanguageTransformerService = gptToLanguageTransformerService;
    }

    @Override
    public void createInquiry(String subjectRecordId, RecordType recordType, String question, String filePath) {
        String likelyCodeLanguage = gptToLanguageTransformerService.getFromFilePath(filePath);
        Inquiry inquiry = new Inquiry(null, null, null, null, null, null, null, null, null, null);
        InquiryChat temporaryChat = new InquiryChat(null, null, null, null, "User", question, likelyCodeLanguage);
        inquiry.getChats().add(temporaryChat);
        InquiryViewer inquiryViewer = codactorToolWindowService.getInquiryViewer();
        inquiryViewer.updateInquiryContents(inquiry);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (MODIFICATION)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                inquiryViewer.setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                Inquiry inquiry = inquiryDao.createInquiry(subjectRecordId, recordType, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), new ArrayList<>());
                if (inquiry != null) {
                    inquiryViewer.updateInquiryContents(inquiry);
                    inquiryViewer.componentResized();
                }
                inquiryViewer.setLoadingChat(false);
            }
        };

        ProgressManager.getInstance().run(backgroundTask);
    }

    @Override
    public void createInquiry(String filePath, String code, String question, List<HistoricalContextObjectHolder> priorContext) {
        Inquiry temporaryInquiry = new Inquiry(null, filePath, code, question, priorContext);
        InquiryViewer inquiryViewer = codactorToolWindowService.getInquiryViewer();
        inquiryViewer.updateInquiryContents(temporaryInquiry);
        inquiryViewer.setLoadingChat(true);
        codactorToolWindowService.openInquiryViewerToolWindow();
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (CODE)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                Inquiry inquiry = inquiryDao.createInquiry(filePath, code, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                inquiryViewer.updateInquiryContents(inquiry);
                inquiryViewer.setLoadingChat(false);
                promptContextService.clearPromptContext();
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
    }

    @Override
    public void createGeneralInquiry(String question) {
        String likelyCodeLanguage = gptToLanguageTransformerService.convert(question);
        InquiryChat temporaryChat = new InquiryChat(null, null, null, null, "User", question, likelyCodeLanguage);
        Inquiry inquiry = new Inquiry(null, null, null, null, null, null, null, null, null, null);
        inquiry.getChats().add(temporaryChat);
        InquiryViewer inquiryViewer = codactorToolWindowService.getInquiryViewer();
        inquiryViewer.updateInquiryContents(inquiry);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (GENERAL)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                inquiryViewer.setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                Inquiry inquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                if (inquiry != null) {
                    inquiryViewer.updateInquiryContents(inquiry);
                    inquiryViewer.componentResized();
                }
                inquiryViewer.setLoadingChat(false);
            }
        };

        ProgressManager.getInstance().run(backgroundTask);
    }

    @Override
    public void continueInquiry(String previousInquiryChatId, String question) {
        String likelyCodeLanguage = gptToLanguageTransformerService.convert(question);
        InquiryViewer inquiryViewer = codactorToolWindowService.getInquiryViewer();
        Inquiry inquiry = inquiryViewer.getInquiry();
        InquiryChat inquiryChat = new InquiryChat(null, inquiry.getId(), inquiry.getFilePath(), previousInquiryChatId, "User", question, likelyCodeLanguage);
        findAlternatesForInquiryChat(inquiry.getChats(), inquiryChat);
        inquiry.getChats().add(inquiryChat);
        inquiryViewer.updateInquiryContents(inquiry);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (CONTINUED)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                inquiryViewer.setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                Inquiry response = inquiryDao.continueInquiry(previousInquiryChatId, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                inquiry.getChats().remove(inquiryChat);
                inquiry.getChats().addAll(response.getChats());
                inquiryViewer.updateInquiryContents(inquiry);
                inquiryViewer.componentResized();
                inquiryViewer.setLoadingChat(false);
            }
        };

        ProgressManager.getInstance().run(backgroundTask);
    }

    private void findAlternatesForInquiryChat(List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        List<InquiryChat> alternateInquiryChats = inquiryChats.stream()
                .filter(inquiryChatQuery ->
                        (inquiryChatQuery.getPreviousInquiryChatId() != null && inquiryChatQuery.getPreviousInquiryChatId().equals(inquiryChat.getPreviousInquiryChatId()))
                                || (inquiryChatQuery.getPreviousInquiryChatId() == null && inquiryChat.getPreviousInquiryChatId() == null))
                .sorted(Comparator.comparing(InquiryChat::getCreationTimestamp))
                .collect(Collectors.toList());
        //Sorted by creationTimestamp
        List<String> alternateInquiryChatIds = alternateInquiryChats.stream()
                .map(InquiryChat::getId)
                .collect(Collectors.toList());
        inquiryChat.setAlternateInquiryChatIds(alternateInquiryChatIds);
    }
}
