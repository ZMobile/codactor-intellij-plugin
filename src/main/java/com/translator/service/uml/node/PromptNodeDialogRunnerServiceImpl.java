package com.translator.service.uml.node;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.task.CancellableRunnable;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;
import com.translator.model.uml.prompt.Prompt;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.model.codactor.task.CustomBackgroundTask;
import com.translator.view.uml.dialog.prompt.PromptNodeDialog;

import java.util.*;

public class PromptNodeDialogRunnerServiceImpl implements PromptNodeDialogRunnerService {
    private final Project project;
    private final InquiryDao inquiryDao;
    private final OpenAiApiKeyService openAiApiKeyService;
    private final NodeDialogWindowMapperService nodeDialogWindowMapperService;
    private final BackgroundTaskMapperService backgroundTaskMapperService;
    private final Gson gson;

    @Inject
    public PromptNodeDialogRunnerServiceImpl(Project project,
                                             InquiryDao inquiryDao,
                                             OpenAiApiKeyService openAiApiKeyService,
                                             NodeDialogWindowMapperService nodeDialogWindowMapperService,
                                             BackgroundTaskMapperService backgroundTaskMapperService,
                                             Gson gson) {
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.openAiApiKeyService = openAiApiKeyService;
        this.nodeDialogWindowMapperService = nodeDialogWindowMapperService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.gson = gson;
    }

    @Override
    public void run(LabeledRectangleFigure promptNodeFigure, PromptNode promptNode, String model) {
        if (promptNode.isRunning()) {
            return;
        }
        CancellableRunnable task = customProgressIndicator -> {
            promptNode.getActiveInquiryList().clear();
            promptNode.setRunning(true);
            Inquiry inquiry = new Inquiry(null, null, null, null, null, null, null, null, null, null);
            InquiryChat previousInquiryChat = null;
            for (int i = 0; i < promptNode.getPromptList().size(); i++) {
                Prompt prompt = promptNode.getPromptList().get(i);
                InquiryChat newQuestion = new InquiryChat(null, null, null, null, "User", prompt.getPrompt(), null);
                inquiry.getChats().add(newQuestion);
                if (nodeDialogWindowMapperService.getPromptNodeDialogMap().containsKey(promptNodeFigure)) {
                    PromptNodeDialog promptNodeDialog = nodeDialogWindowMapperService.getPromptNodeDialogMap().get(promptNodeFigure);
                    promptNodeDialog.getPromptViewer().updateInquiryChatContents(inquiry.getChats());
                }
                prompt.setProcessed(false);
                Inquiry newInquiry;
                if (i == 0) {
                    newInquiry = inquiryDao.createGeneralInquiry(prompt.getPrompt(), openAiApiKeyService.getOpenAiApiKey(), model);
                    inquiry = newInquiry;
                    previousInquiryChat = newInquiry.getChats().stream()
                            .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                            .orElseThrow();
                } else {
                    newInquiry = inquiryDao.continueInquiry(previousInquiryChat.getId(), prompt.getPrompt(), openAiApiKeyService.getOpenAiApiKey(), model);
                    previousInquiryChat = newInquiry.getChats().stream()
                            .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                            .orElseThrow();
                    inquiry.getChats().add(previousInquiryChat);
                }
                if (customProgressIndicator.isCanceled()) {
                    break;
                }
                int inquiryIndex = promptNode.getActiveInquiryList().indexOf(inquiry);
                if (inquiryIndex == -1) {
                    promptNode.getActiveInquiryList().add(inquiry);
                } else {
                    promptNode.getActiveInquiryList().set(inquiryIndex, inquiry);
                }
                promptNodeFigure.setMetadata(gson.toJson(promptNode));
                if (nodeDialogWindowMapperService.getPromptNodeDialogMap().containsKey(promptNodeFigure)) {
                    PromptNodeDialog promptNodeDialog = nodeDialogWindowMapperService.getPromptNodeDialogMap().get(promptNodeFigure);
                    promptNodeDialog.getPromptViewer().updateInquiryChatContents(inquiry.getChats());
                }
            }
            for (Prompt prompt : promptNode.getPromptList()) {
                prompt.setProcessed(true);
            }
            if (nodeDialogWindowMapperService.getPromptNodeDialogMap().containsKey(promptNodeFigure)) {
                PromptNodeDialog promptNodeDialog = nodeDialogWindowMapperService.getPromptNodeDialogMap().get(promptNodeFigure);
                promptNodeDialog.getRunButton().setEnabled(true);
                promptNodeDialog.getRunButton().setText("Re-run");
                promptNodeDialog.getCancelButton().setVisible(false);
                promptNodeDialog.getResetButton().setVisible(true);
            }
            promptNode.setRunning(false);
            promptNode.setProcessed(true);
            promptNodeFigure.setMetadata(gson.toJson(promptNode));
            backgroundTaskMapperService.removeTask(promptNode.getId());
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "Prompt Node", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(promptNode.getId(), backgroundTask);
    }
}
