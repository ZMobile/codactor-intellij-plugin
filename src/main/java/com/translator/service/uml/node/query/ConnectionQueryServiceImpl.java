package com.translator.service.uml.node.query;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.draw.figure.MetadataLabeledLineConnectionFigure;
import org.jhotdraw.draw.Drawing;

import java.util.List;
import java.util.stream.Collectors;

public class ConnectionQueryServiceImpl implements ConnectionQueryService {
    private final Gson gson;

    @Inject
    public ConnectionQueryServiceImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public List<MetadataLabeledLineConnectionFigure> getInputsForNode(Drawing drawing, String nodeId) {
        return getConnectionsInDrawing(drawing, nodeId).stream()
                .filter(connection -> gson.fromJson(connection.getMetadata(), Connection.class).getOutputNodeId().equals(nodeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MetadataLabeledLineConnectionFigure> getOutputsForNode(Drawing drawing, String nodeId) {
        return getConnectionsInDrawing(drawing, nodeId).stream()
                .filter(connection -> gson.fromJson(connection.getMetadata(), Connection.class).getInputNodeId().equals(nodeId))
                .collect(Collectors.toList());
    }

    private List<MetadataLabeledLineConnectionFigure> getConnectionsInDrawing(Drawing drawing, String nodeId) {
        return drawing.getChildren().stream()
                .filter(child -> child instanceof MetadataLabeledLineConnectionFigure)
                .map(child -> (MetadataLabeledLineConnectionFigure) child)
                .collect(Collectors.toList());
    }
}
