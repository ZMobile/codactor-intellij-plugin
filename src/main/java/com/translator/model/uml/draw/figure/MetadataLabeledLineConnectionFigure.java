package com.translator.model.uml.draw.figure;

import com.google.gson.Gson;
import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.node.Node;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LabeledLineConnectionFigure;

public class MetadataLabeledLineConnectionFigure extends LabeledLineConnectionFigure {
    private String metadata;

    public MetadataLabeledLineConnectionFigure(Gson gson) {
        super();

        String inputNodeId = getInputNodeId(gson);
        String outputNodeId = getOutputNodeId(gson);
        System.out.println("Test input node id: " + inputNodeId);
        System.out.println("Test output node id: " + outputNodeId);
        Connection connection = new Connection(inputNodeId, outputNodeId);
        this.metadata = gson.toJson(connection);
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getInputNodeId(Gson gson) {
        Figure startFigure = getStartFigure();
        String metadata = getLabeledNodeMetadata(startFigure);
        if (metadata == null) {
            return null;
        }
        return gson.fromJson(metadata, Node.class).getId();
    }

    public String getOutputNodeId(Gson gson) {
        Figure endFigure = getEndFigure();
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
