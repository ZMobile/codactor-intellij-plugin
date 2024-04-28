package com.translator.service.uml.node.runner;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.model.codactor.io.CancellableRunnable;
import com.translator.model.codactor.io.CustomBackgroundTask;
import com.translator.model.uml.draw.connection.Connection;
import com.translator.model.uml.draw.figure.LabeledMetadataFigure;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.draw.figure.MetadataLabeledLineConnectionFigure;
import com.translator.service.codactor.io.BackgroundTaskMapperService;
import com.translator.service.uml.node.query.ConnectionQueryService;
import com.translator.service.uml.node.query.NodeQueryService;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeRunnerManagerServiceImpl implements NodeRunnerManagerService {
    private final Project project;
    private final Map<Class<? extends LabeledMetadataFigure>, NodeRunnerService> nodeRunners;
    private final NodeQueryService nodeQueryService;
    private final ConnectionQueryService connectionQueryService;
    private final BackgroundTaskMapperService backgroundTaskMapperService;
    private final Gson gson;

    @Inject
    public NodeRunnerManagerServiceImpl(Project project,
                                        PromptNodeRunnerService promptNodeRunnerService,
                                        NodeQueryService nodeQueryService,
                                        ConnectionQueryService connectionQueryService,
                                        BackgroundTaskMapperService backgroundTaskMapperService,
                                        Gson gson) {
        this.project = project;
        this.nodeRunners = new HashMap<>();
        this.nodeRunners.put(LabeledRectangleFigure.class, promptNodeRunnerService);
        this.nodeQueryService = nodeQueryService;
        this.connectionQueryService = connectionQueryService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.gson = gson;
    }

    @Override
    public void runNode(Drawing drawing, String nodeId) {
        CancellableRunnable task = progressIndicator -> {
            runNode(drawing, nodeId, nodeId, progressIndicator);
            backgroundTaskMapperService.removeTask(nodeId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "Prompt Node", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(nodeId, backgroundTask);
    }

    private void runNode(Drawing drawing, String startingNodeId, String nodeId, ProgressIndicator progressIndicator) {
        Figure figure = nodeQueryService.getFigureFromId(drawing, nodeId);
        if (figure instanceof LabeledMetadataFigure) {
            LabeledMetadataFigure labeledMetadataFigure = (LabeledMetadataFigure) figure;
            if (labeledMetadataFigure.getMetadata().contains(nodeId)) {
                NodeRunnerService nodeRunnerService = nodeRunners.get(labeledMetadataFigure.getClass());
                if (nodeRunnerService != null) {
                    nodeRunnerService.runNode(drawing, startingNodeId, nodeId, progressIndicator);
                }
            }
        }
        if (!progressIndicator.isCanceled()) {
            List<MetadataLabeledLineConnectionFigure> metadataLabeledLineConnectionFigureList = connectionQueryService.getOutputsForNode(drawing, nodeId);
            for (MetadataLabeledLineConnectionFigure metadataLabeledLineConnectionFigure : metadataLabeledLineConnectionFigureList) {
                Connection connection = gson.fromJson(metadataLabeledLineConnectionFigure.getMetadata(), Connection.class);
                connection.setOpen(true);
                metadataLabeledLineConnectionFigure.setMetadata(gson.toJson(connection));
                if (inputsOpen(drawing, connection.getOutputNodeId())) {
                    runNode(drawing, startingNodeId, connection.getOutputNodeId(), progressIndicator);
                }
            }
        }
    }

    private boolean inputsOpen(Drawing drawing, String nodeId) {
        List<MetadataLabeledLineConnectionFigure> metadataLabeledLineConnectionFigureList = connectionQueryService.getInputsForNode(drawing, nodeId);
        List<Connection> inputConnections = new ArrayList<>();
        for (MetadataLabeledLineConnectionFigure metadataLabeledLineConnectionFigure : metadataLabeledLineConnectionFigureList) {
            Connection connection = gson.fromJson(metadataLabeledLineConnectionFigure.getMetadata(), Connection.class);
            inputConnections.add(connection);
        }
        boolean inputsOpen = true;
        for (Connection connection : inputConnections) {
            if (!connection.isOpen()) {
                inputsOpen = false;
                break;
            }
        }
        return inputsOpen;
    }
}
