package com.translator.view.uml.tool;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.draw.figure.*;
import com.translator.model.uml.node.Node;
import com.translator.service.uml.node.NodeDialogWindowMapperService;
import com.translator.view.uml.dialog.prompt.PromptNodeDialog;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.tool.ConnectionTool;

public class NodeConnectionTool extends ConnectionTool {
    private NodeDialogWindowMapperService nodeDialogWindowMapperService;
    private Gson gson;

    @Inject
    public NodeConnectionTool(@Assisted ConnectionFigure prototype,
                              NodeDialogWindowMapperService nodeDialogWindowMapperService,
                              Gson gson) {
        super(prototype);
        this.nodeDialogWindowMapperService = nodeDialogWindowMapperService;
        this.gson = gson;
    }

    @Override
    protected void creationFinished(Figure createdFigure) {
        super.creationFinished(createdFigure);
        assert createdFigure instanceof MetadataLabeledLineConnectionFigure;
        MetadataLabeledLineConnectionFigure metadataLabeledLineConnectionFigure = (MetadataLabeledLineConnectionFigure) createdFigure;
        String inputNodeId = getInputNodeId(metadataLabeledLineConnectionFigure.getStartFigure());
        String outputNodeId = getOutputNodeId(metadataLabeledLineConnectionFigure.getEndFigure());
        Connection connection = new Connection(inputNodeId, outputNodeId);
        metadataLabeledLineConnectionFigure.setMetadata(gson.toJson(connection));
        DrawingEditor editor = getEditor();
        if (editor != null) {
            DrawingView view = editor.getActiveView();
            if (view != null) {
                Drawing drawing = view.getDrawing();

                for (Figure figure : drawing.getChildren()) {
                    if (figure instanceof LabeledRectangleFigure) {
                        LabeledRectangleFigure labeledRectangleFigure = (LabeledRectangleFigure) figure;
                        if (nodeDialogWindowMapperService.getPromptNodeDialogMap().containsKey(labeledRectangleFigure)) {
                            PromptNodeDialog promptNodeDialog = nodeDialogWindowMapperService.getPromptNodeDialogMap().get(labeledRectangleFigure);
                            promptNodeDialog.getPromptConnectionViewer().updateConnections();
                        }
                    }
                }
            }
        }
    }

    public String getInputNodeId(Figure startFigure) {
        String metadata = getLabeledNodeMetadata(startFigure);
        if (metadata == null) {
            return null;
        }
        return gson.fromJson(metadata, Node.class).getId();
    }


    public String getOutputNodeId(Figure endFigure) {
        String metadata = getLabeledNodeMetadata(endFigure);
        if (metadata == null) {
            return null;
        }
        return gson.fromJson(metadata, Node.class).getId();
    }

    public String getLabeledNodeMetadata(Figure figure) {
        if (figure instanceof LabeledRectangleFigure) {
            return ((LabeledRectangleFigure) figure).getMetadata();
        } else if (figure instanceof LabeledDiamondFigure) {
            return  ((LabeledDiamondFigure) figure).getMetadata();
        } else if (figure instanceof LabeledEllipseFigure) {
            return ((LabeledEllipseFigure) figure).getMetadata();
        } else if (figure instanceof LabeledRoundRectangleFigure) {
            return ((LabeledRoundRectangleFigure) figure).getMetadata();
        } else if (figure instanceof LabeledTriangleFigure) {
            return ((LabeledTriangleFigure) figure).getMetadata();
        } else {
            return null;
        }
    }
}
