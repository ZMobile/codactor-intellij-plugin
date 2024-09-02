package com.translator.service.codactor.ai.chat.inquiry;

import com.google.gson.Gson;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirective;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.chat.function.GptFunction;
import com.translator.model.codactor.ai.modification.RecordType;
import com.translator.service.codactor.ai.openai.connection.AzureConnectionService;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerService;
import com.translator.service.codactor.ai.chat.functions.CodactorFunctionGeneratorService;
import com.translator.service.codactor.ai.chat.functions.InquiryFunctionCallProcessorService;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InquiryServiceImpl implements InquiryService {
    private Gson gson;
    private Project project;
    private InquiryDao inquiryDao;
    private CodactorToolWindowService codactorToolWindowService;
    private DefaultConnectionService defaultConnectionService;
    private PromptContextService promptContextService;
    private GptToLanguageTransformerService gptToLanguageTransformerService;
    private CodactorFunctionGeneratorService codactorFunctionGeneratorService;
    private InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService;
    private InquirySystemMessageGeneratorService inquirySystemMessageGeneratorService;
    private AzureConnectionService azureConnectionService;
    private InquiryViewerFactory inquiryViewerFactory;

    @Inject
    public InquiryServiceImpl(Gson gson,
                              Project project,
                              InquiryDao inquiryDao,
                              CodactorToolWindowService codactorToolWindowService,
                              DefaultConnectionService defaultConnectionService,
                              PromptContextService promptContextService,
                              GptToLanguageTransformerService gptToLanguageTransformerService,
                              CodactorFunctionGeneratorService codactorFunctionGeneratorService,
                              InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService,
                              InquirySystemMessageGeneratorService inquirySystemMessageGeneratorService,
                              AzureConnectionService azureConnectionService) {
        this.gson = gson;
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.codactorToolWindowService = codactorToolWindowService;
        this.defaultConnectionService = defaultConnectionService;
        this.promptContextService = promptContextService;
        this.gptToLanguageTransformerService = gptToLanguageTransformerService;
        this.codactorFunctionGeneratorService = codactorFunctionGeneratorService;
        this.inquiryFunctionCallProcessorService = inquiryFunctionCallProcessorService;
        this.inquirySystemMessageGeneratorService = inquirySystemMessageGeneratorService;
        this.azureConnectionService = azureConnectionService;
    }

    @Override
    public InquiryViewer createInquiry(InquiryViewer inquiryViewer, String subjectRecordId, RecordType recordType, String question, String filePath, String model) {
        String likelyCodeLanguage = gptToLanguageTransformerService.getFromFilePath(filePath);
        Inquiry inquiry = new Inquiry.Builder()
                .build();
        InquiryChat temporaryChat = new InquiryChat.Builder()
                .withFrom("User")
                .withMessage(question)
                .withLikelyCodeLanguage(likelyCodeLanguage)
                .build();
        inquiry.getChats().add(temporaryChat);
        inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (MODIFICATION)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                inquiryViewer.setLoadingChat(true);
                String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
                List<GptFunction> functions = null;
                String systemMessage = inquirySystemMessageGeneratorService.generateDefaultSystemMessage();
                if (model.equals("gpt-3.5-turbo") || model.equals("gpt-3.5-turbo-16k") || model.equals("gpt-4") || model.equals("gpt-4-32k") || model.equals("gpt-4o")) {
                    functions = codactorFunctionGeneratorService.generateCodactorFunctions();
                    systemMessage = inquirySystemMessageGeneratorService.generateFunctionsSystemMessage();
                }
                Inquiry inquiry = inquiryDao.createInquiry(subjectRecordId, recordType, question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), new ArrayList<>(), functions, systemMessage);
                if (inquiry != null && inquiry.getError() == null) {
                    inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                    processPossibleFunctionCalls(inquiryViewer, inquiry, openAiApiKey, model, functions);
                }
                inquiryViewer.setLoadingChat(false);
            }
        };

        ProgressManager.getInstance().run(backgroundTask);
        return inquiryViewer;
    }

    @Override
    public InquiryViewer createInquiry(InquiryViewer inquiryViewer, String filePath, String code, String question, List<HistoricalContextObjectHolder> priorContext, String model) {
        Inquiry temporaryInquiry = new Inquiry.Builder()
                .withFilePath(filePath)
                .withSubjectCode(code)
                .withInitialQuestion(question)
                .withPriorContext(priorContext)
                .build();
        inquiryViewer.getInquiryChatListViewer().updateInquiryContents(temporaryInquiry);
        inquiryViewer.setLoadingChat(true);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (CODE)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
                List<GptFunction> functions = null;
                String systemMessage = inquirySystemMessageGeneratorService.generateDefaultSystemMessage();
                if (model.equals("gpt-3.5-turbo") || model.equals("gpt-3.5-turbo-16k") || model.equals("gpt-4") || model.equals("gpt-4-32k") || model.equals("gpt-4o")) {
                    functions = codactorFunctionGeneratorService.generateCodactorFunctions();
                    systemMessage = inquirySystemMessageGeneratorService.generateFunctionsSystemMessage();
                }
                Inquiry inquiry = inquiryDao.createInquiry(filePath, code, question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext, functions, systemMessage);
                if (inquiry == null) {
                    JOptionPane.showMessageDialog(null, "Error creating inquiry", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (inquiry.getError() != null) {
                    JOptionPane.showMessageDialog(null, inquiry.getError(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                processPossibleFunctionCalls(inquiryViewer, inquiry, openAiApiKey, model, functions);
                inquiryViewer.setLoadingChat(false);
                promptContextService.clearPromptContext();
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
        return inquiryViewer;
    }

    @Override
    public InquiryViewer createGeneralInquiry(InquiryViewer inquiryViewer, String question, String model) {
        String likelyCodeLanguage = gptToLanguageTransformerService.convert(question);
        InquiryChat temporaryChat = new InquiryChat.Builder()
                .withFrom("User")
                .withMessage(question)
                .withLikelyCodeLanguage(likelyCodeLanguage)
                .build();
        Inquiry inquiry = new Inquiry.Builder()
                .build();
        inquiry.getChats().add(temporaryChat);
        inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Inquiry (GENERAL)", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                inquiryViewer.setLoadingChat(true);
                String openAiApiKey;
                if (azureConnectionService.isAzureConnected()) {
                    openAiApiKey = azureConnectionService.getKey();
                } else {
                    openAiApiKey = defaultConnectionService.getOpenAiApiKey();
                }
                List<GptFunction> functions = null;
                String systemMessage = inquirySystemMessageGeneratorService.generateDefaultSystemMessage();
                if (model.equals("gpt-3.5-turbo") || model.equals("gpt-3.5-turbo-16k") || model.equals("gpt-4") || model.equals("gpt-4-32k") || model.equals("gpt-4o")) {
                    functions = codactorFunctionGeneratorService.generateCodactorFunctions();
                    systemMessage = inquirySystemMessageGeneratorService.generateFunctionsSystemMessage();
                }
                Inquiry inquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), new ArrayList<>(), functions, systemMessage);
                if (inquiry != null) {
                    if (inquiry.getError() != null) {
                        JOptionPane.showMessageDialog(null, inquiry.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                    inquiryViewer.getInquiryChatListViewer().componentResized();
                    processPossibleFunctionCalls(inquiryViewer, inquiry, openAiApiKey, model, functions);
                }
                inquiryViewer.setLoadingChat(false);
            }
        };

        ProgressManager.getInstance().run(backgroundTask);
        return inquiryViewer;
    }

    @Override
    public void continueInquiry(InquiryViewer inquiryViewer, String previousInquiryChatId, String question, String model) {
        String likelyCodeLanguage = gptToLanguageTransformerService.convert(question);
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
                String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
                List<GptFunction> functions = null;
                if (model.equals("gpt-3.5-turbo") || model.equals("gpt-3.5-turbo-16k") || model.equals("gpt-4") || model.equals("gpt-4-32k") || model.equals("gpt-4o")) {
                    if (inquiry.getActiveDirective() == null) {
                        functions = codactorFunctionGeneratorService.generateCodactorFunctions();
                    } else {
                        if (inquiry.getActiveDirective() instanceof CreateAndRunUnitTestDirective) {
                            CreateAndRunUnitTestDirective createAndRunUnitTestDirective = (CreateAndRunUnitTestDirective) inquiry.getActiveDirective();
                            functions = new ArrayList<>();
                            if (!createAndRunUnitTestDirective.getSession().isUnitTestCreated()) {
                                functions.addAll(createAndRunUnitTestDirective.getPhaseOneFunctions());
                                functions.addAll(createAndRunUnitTestDirective.getPhaseOneAndTwoFunctions());
                            } else {
                                functions.addAll(createAndRunUnitTestDirective.getPhaseOneAndTwoFunctions());
                                functions.addAll(createAndRunUnitTestDirective.getPhaseTwoFunctions());
                            }
                            functions.addAll(createAndRunUnitTestDirective.getPhaseThreeFunctions());
                        }
                    }
                }
                Inquiry response = inquiryDao.continueInquiry(previousInquiryChatId, question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), functions);
                if (response.getError() != null) {
                    JOptionPane.showMessageDialog(null, response.getError(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
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

    private void processPossibleFunctionCalls(InquiryViewer inquiryViewer, Inquiry inquiry, String openAiApiKey, String model, List<GptFunction> functions) {
        InquiryChat latestInquiryChat = getLatestInquiryChat(inquiry.getChats());
        if (latestInquiryChat.getFunctionCall() != null) {
            while (latestInquiryChat.getFunctionCall() != null) {
                String functionCallResponse = inquiryFunctionCallProcessorService.processFunctionCall(inquiry, latestInquiryChat.getFunctionCall());
                Inquiry inquiry1 = inquiryDao.respondToFunctionCall(latestInquiryChat.getId(), latestInquiryChat.getFunctionCall().getName(), functionCallResponse, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), functions);
                if (inquiry1 == null) {
                    break;
                } else if (inquiry1.getError() != null){
                    JOptionPane.showMessageDialog(null, inquiry1.getError(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                }
                inquiry.getChats().addAll(inquiry1.getChats());
                inquiryViewer.getInquiryChatListViewer().updateInquiryContents(inquiry);
                inquiryViewer.getInquiryChatListViewer().componentResized();
                latestInquiryChat = getLatestInquiryChat(inquiry1.getChats());
            }
        }
    }
}
