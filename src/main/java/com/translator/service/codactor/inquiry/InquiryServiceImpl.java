package com.translator.service.codactor.inquiry;

import com.google.gson.Gson;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.api.translator.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.modification.RecordType;
import com.translator.service.codactor.editor.GptToLanguageTransformerService;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.inquiry.functions.CodactorFunctionGeneratorService;
import com.translator.service.codactor.inquiry.functions.InquiryFunctionCallProcessorService;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InquiryServiceImpl implements InquiryService {
    private Gson gson;
    private Project project;
    private InquiryDao inquiryDao;
    private CodactorToolWindowService codactorToolWindowService;
    private OpenAiApiKeyService openAiApiKeyService;
    private PromptContextService promptContextService;
    private GptToLanguageTransformerService gptToLanguageTransformerService;
    private CodactorFunctionGeneratorService codactorFunctionGeneratorService;
    private InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService;

    @Inject
    public InquiryServiceImpl(Gson gson,
                              Project project,
                              InquiryDao inquiryDao,
                              CodactorToolWindowService codactorToolWindowService,
                              OpenAiApiKeyService openAiApiKeyService,
                              PromptContextService promptContextService,
                              GptToLanguageTransformerService gptToLanguageTransformerService,
                              CodactorFunctionGeneratorService codactorFunctionGeneratorService,
                              InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService) {
        this.gson = gson;
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.codactorToolWindowService = codactorToolWindowService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.promptContextService = promptContextService;
        this.gptToLanguageTransformerService = gptToLanguageTransformerService;
        this.codactorFunctionGeneratorService = codactorFunctionGeneratorService;
        this.inquiryFunctionCallProcessorService = inquiryFunctionCallProcessorService;
    }

    @Override
    public void createInquiry(String subjectRecordId, RecordType recordType, String question, String filePath, String model) {
        String likelyCodeLanguage = gptToLanguageTransformerService.getFromFilePath(filePath);
        Inquiry inquiry = new Inquiry.Builder()
                .build();
        InquiryChat temporaryChat = new InquiryChat.Builder()
                .withFrom("User")
                .withMessage(question)
                .withLikelyCodeLanguage(likelyCodeLanguage)
                .build();
        inquiry.getChats().add(temporaryChat);
        InquiryViewer inquiryViewer = codactorToolWindowService.getInquiryViewer();
        inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (MODIFICATION)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                inquiryViewer.setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                List<ChatGptFunction> functions = null;
                if (model.equals("gpt-3.5-turbo-0613") || model.equals("gpt-4-0613")) {
                    functions = codactorFunctionGeneratorService.generateCodactorFunctions();
                }
                Inquiry inquiry = inquiryDao.createInquiry(subjectRecordId, recordType, question, openAiApiKey, model, new ArrayList<>(), functions);
                if (inquiry != null) {
                    inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                    processPossibleFunctionCalls(inquiryViewer, inquiry, openAiApiKey, model, functions);
                }
                inquiryViewer.setLoadingChat(false);
            }
        };

        ProgressManager.getInstance().run(backgroundTask);
    }

    @Override
    public void createInquiry(String filePath, String code, String question, List<HistoricalContextObjectHolder> priorContext, String model) {
        Inquiry temporaryInquiry = new Inquiry.Builder()
                .withFilePath(filePath)
                .withSubjectCode(code)
                .withInitialQuestion(question)
                .withPriorContext(priorContext)
                .build();
        InquiryViewer inquiryViewer = codactorToolWindowService.getInquiryViewer();
        inquiryViewer.getInquiryChatListViewer().updateInquiryContents(temporaryInquiry);
        inquiryViewer.setLoadingChat(true);
        codactorToolWindowService.openInquiryViewerToolWindow();
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (CODE)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                List<ChatGptFunction> functions = null;
                if (model.equals("gpt-3.5-turbo-0613") || model.equals("gpt-4-0613")) {
                    functions = codactorFunctionGeneratorService.generateCodactorFunctions();
                }
                Inquiry inquiry = inquiryDao.createInquiry(filePath, code, question, openAiApiKey, model, priorContext, functions);
                inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                processPossibleFunctionCalls(inquiryViewer, inquiry, openAiApiKey, model, functions);
                inquiryViewer.setLoadingChat(false);
                promptContextService.clearPromptContext();
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
    }

    @Override
    public void createGeneralInquiry(String question, String model) {
        String likelyCodeLanguage = gptToLanguageTransformerService.convert(question);
        InquiryChat temporaryChat = new InquiryChat.Builder()
                .withFrom("User")
                .withMessage(question)
                .withLikelyCodeLanguage(likelyCodeLanguage)
                .build();
        Inquiry inquiry = new Inquiry.Builder()
                .build();
        inquiry.getChats().add(temporaryChat);
        InquiryViewer inquiryViewer = codactorToolWindowService.getInquiryViewer();
        inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (GENERAL)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                inquiryViewer.setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                List<ChatGptFunction> functions = null;
                if (model.equals("gpt-3.5-turbo-0613") || model.equals("gpt-4-0613")) {
                    System.out.println("This gets called");
                    functions = codactorFunctionGeneratorService.generateCodactorFunctions();
                }
                Inquiry inquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, model, new ArrayList<>(), functions);
                if (inquiry != null) {
                    inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                    inquiryViewer.getInquiryChatListViewer().componentResized();
                    processPossibleFunctionCalls(inquiryViewer, inquiry, openAiApiKey, model, functions);
                }
                inquiryViewer.setLoadingChat(false);
            }
        };

        ProgressManager.getInstance().run(backgroundTask);
    }

    @Override
    public void continueInquiry(String previousInquiryChatId, String question, String model) {
        String likelyCodeLanguage = gptToLanguageTransformerService.convert(question);
        InquiryViewer inquiryViewer = codactorToolWindowService.getInquiryViewer();
        Inquiry inquiry = inquiryViewer.getInquiry();
        InquiryChat inquiryChat = new InquiryChat.Builder()
                .withInquiryId(inquiry.getId())
                .withFilePath(inquiry.getFilePath())
                .withPreviousInquiryChatId(previousInquiryChatId)
                .withFrom("User")
                .withMessage(question)
                .withLikelyCodeLanguage(likelyCodeLanguage)
                .build();
        findAlternatesForInquiryChat(inquiry.getChats(), inquiryChat);
        inquiry.getChats().add(inquiryChat);
        inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (CONTINUED)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                inquiryViewer.setLoadingChat(true);
                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                List<ChatGptFunction> functions = null;
                if (model.equals("gpt-3.5-turbo-0613") || model.equals("gpt-4-0613")) {
                    functions = codactorFunctionGeneratorService.generateCodactorFunctions();

                }
                Inquiry response = inquiryDao.continueInquiry(previousInquiryChatId, question, openAiApiKey, model, functions);
                inquiry.getChats().remove(inquiryChat);
                inquiry.getChats().addAll(response.getChats());
                inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                inquiryViewer.getInquiryChatListViewer().componentResized();
                processPossibleFunctionCalls(inquiryViewer, inquiry, openAiApiKey, model, functions);
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

    private InquiryChat getLatestInquiryChat(List<InquiryChat> inquiryChats) {
        return inquiryChats.stream().max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                .orElse(null);
    }

    private void processPossibleFunctionCalls(InquiryViewer inquiryViewer, Inquiry inquiry, String openAiApiKey, String model, List<ChatGptFunction> functions) {
        InquiryChat latestInquiryChat = getLatestInquiryChat(inquiry.getChats());
        if (latestInquiryChat.getFunctionCall() != null) {
            while (latestInquiryChat.getFunctionCall() != null) {
                String functionCallResponse = inquiryFunctionCallProcessorService.processFunctionCall(latestInquiryChat.getFunctionCall());
                Inquiry inquiry1 = inquiryDao.respondToFunctionCall(latestInquiryChat.getId(), latestInquiryChat.getFunctionCall().getName(), functionCallResponse, openAiApiKey, model, functions);
                inquiry.getChats().addAll(inquiry1.getChats());
                inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                inquiryViewer.getInquiryChatListViewer().componentResized();
                latestInquiryChat = getLatestInquiryChat(inquiry1.getChats());
            }
        }
    }
}
