package com.translator.service.uml.node.runner;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.draw.figure.LabeledMetadataFigure;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.Node;
import com.translator.model.uml.node.PromptNode;
import com.translator.model.uml.prompt.Prompt;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.uml.node.NodeDialogWindowMapperService;
import com.translator.service.uml.node.query.ConnectionQueryService;
import com.translator.service.uml.node.query.NodeQueryService;
import com.translator.view.uml.node.dialog.prompt.PromptNodeDialog;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import java.util.*;
import java.util.stream.Collectors;

public class PromptNodeRunnerServiceImpl implements PromptNodeRunnerService {
    private final Project project;
    private final InquiryDao inquiryDao;
    private final OpenAiApiKeyService openAiApiKeyService;
    private final NodeQueryService nodeQueryService;
    private final ConnectionQueryService connectionQueryService;
    private final NodeDialogWindowMapperService nodeDialogWindowMapperService;
    private final Gson gson;

    @Inject
    public PromptNodeRunnerServiceImpl(Project project,
                                       InquiryDao inquiryDao,
                                       OpenAiApiKeyService openAiApiKeyService,
                                       NodeQueryService nodeQueryService,
                                       ConnectionQueryService connectionQueryService,
                                       NodeDialogWindowMapperService nodeDialogWindowMapperService,
                                       Gson gson) {
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.openAiApiKeyService = openAiApiKeyService;
        this.nodeQueryService = nodeQueryService;
        this.connectionQueryService = connectionQueryService;
        this.nodeDialogWindowMapperService = nodeDialogWindowMapperService;
        this.gson = gson;
    }

    @Override
    public void runNode(Drawing drawing, String startingNodeId, String nodeId, ProgressIndicator progressIndicator) {
        LabeledRectangleFigure promptNodeFigure = (LabeledRectangleFigure) nodeQueryService.getFigureFromId(drawing, nodeId);
        if (nodeDialogWindowMapperService.getPromptNodeDialogMap().containsKey(promptNodeFigure)) {
            PromptNodeDialog promptNodeDialog = nodeDialogWindowMapperService.getPromptNodeDialogMap().get(promptNodeFigure);
            promptNodeDialog.getRunButton().setEnabled(false);
            promptNodeDialog.getCancelButton().setVisible(true);
        }

        PromptNode promptNode = gson.fromJson(promptNodeFigure.getMetadata(), PromptNode.class);
        Map<Connection, LabeledMetadataFigure> inputConnectionMap = getInputsForNode(drawing, promptNode);
        if (promptNode.isRunning()) {
            return;
        }
        Map<Connection, LabeledMetadataFigure> finalInputConnectionMap = inputConnectionMap;
            promptNode.getActiveInquiryList().clear();
            promptNode.setRunning(true);
            promptNode.setStartedByNodeId(startingNodeId);
            promptNodeFigure.setMetadata(gson.toJson(promptNode));
            Inquiry inquiry = new Inquiry(null, null, null, null, null, null, null, null, null, null);
            InquiryChat previousInquiryChat = null;
            for (int i = 0; i < promptNode.getPromptList().size(); i++) {
                Prompt prompt = promptNode.getPromptList().get(i);
                String newPrompt = prompt.getPrompt();
                for (Connection connection : finalInputConnectionMap.keySet()) {
                    if (!Objects.equals(connection.getOutputKey().trim(), "") && newPrompt.contains(connection.getOutputKey())) {
                        LabeledMetadataFigure outputFigure = finalInputConnectionMap.get(connection);
                        Node node = gson.fromJson(outputFigure.getMetadata(), Node.class);
                        newPrompt = newPrompt.replace(connection.getOutputKey(), node.getOutput());
                    }
                }
                InquiryChat newQuestion = new InquiryChat(null, null, null, null, "User", newPrompt, null);
                inquiry.getChats().add(newQuestion);
                if (nodeDialogWindowMapperService.getPromptNodeDialogMap().containsKey(promptNodeFigure)) {
                    PromptNodeDialog promptNodeDialog = nodeDialogWindowMapperService.getPromptNodeDialogMap().get(promptNodeFigure);
                    promptNodeDialog.getPromptViewer().updateInquiryChatContents(inquiry.getChats());
                }
                prompt.setProcessed(false);
                Inquiry newInquiry;
                if (i == 0) {
                    newInquiry = inquiryDao.createGeneralInquiry(newPrompt, openAiApiKeyService.getOpenAiApiKey(), promptNode.getModel());
                    inquiry = newInquiry;
                    previousInquiryChat = newInquiry.getChats().stream()
                            .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                            .orElseThrow();
                } else {
                    newInquiry = inquiryDao.continueInquiry(previousInquiryChat.getId(), prompt.getPrompt(), openAiApiKeyService.getOpenAiApiKey(), promptNode.getModel());
                    previousInquiryChat = newInquiry.getChats().stream()
                            .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                            .orElseThrow();
                    inquiry.getChats().add(previousInquiryChat);
                }
                if (progressIndicator.isCanceled()) {
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
            assert previousInquiryChat != null;
            promptNode.setOutput(previousInquiryChat.getMessage());
            promptNodeFigure.setMetadata(gson.toJson(promptNode));
    }

    public Map<Connection, LabeledMetadataFigure> getInputsForNode(Drawing drawing, PromptNode promptNode) {
        Map<Connection, LabeledMetadataFigure> inputConnectionMap = new HashMap<>();
        List<Connection> connections = connectionQueryService.getInputsForNode(drawing, promptNode.getId()).stream()
                .map(connectionFigure -> gson.fromJson(connectionFigure.getMetadata(), Connection.class))
                .collect(Collectors.toList());
        for (Connection connection : connections) {
            Figure figure = nodeQueryService.getFigureFromId(drawing, connection.getInputNodeId());
            LabeledMetadataFigure labeledMetadataFigure = (LabeledMetadataFigure) figure;
            inputConnectionMap.put(connection, labeledMetadataFigure);
        }
        return inputConnectionMap;
    }
}
