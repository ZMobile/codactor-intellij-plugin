package com.translator.service.uml.node.query;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.draw.figure.LabeledMetadataFigure;
import com.translator.model.uml.draw.figure.MetadataLabeledLineConnectionFigure;
import com.translator.model.uml.node.Node;
import org.jetbrains.annotations.NotNull;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import java.util.ArrayList;
import java.util.List;

public class NodeQueryServiceImpl implements NodeQueryService {
    private final Gson gson;

    @Inject
    public NodeQueryServiceImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Figure getFigureFromId(@NotNull Drawing drawing, String id) {
        for (Figure figure : drawing.getChildren()) {
            if (figure instanceof LabeledMetadataFigure) {
                LabeledMetadataFigure labeledMetadataFigure = (LabeledMetadataFigure) figure;
                Node node = gson.fromJson(labeledMetadataFigure.getMetadata(), Node.class);
                if (node != null && node.getId().equals(id)) {
                    return figure;
                }
            }
        }
        return null;
    }

    @Override
    public Figure getOutputFigureFromConnection(Drawing drawing, Connection connection) {
        for (Figure figure : drawing.getChildren()) {
            if (!(figure instanceof MetadataLabeledLineConnectionFigure) && (figure instanceof LabeledMetadataFigure)) {
                LabeledMetadataFigure labeledMetadataFigure = (LabeledMetadataFigure) figure;
                Node node = gson.fromJson(labeledMetadataFigure.getMetadata(), Node.class);
                if (node != null && node.getId().equals(connection.getOutputNodeId())) {
                    return figure;
                }
            }
        }
        return null;
    }

    @Override
    public List<Figure> getOutputFiguresFromConnections(Drawing drawing, List<Connection> connections) {
        List<Figure> outputFigures = new ArrayList<>();
        for (Connection connection : connections) {
            Figure outputFigure = getOutputFigureFromConnection(drawing, connection);
            if (outputFigure != null) {
                outputFigures.add(outputFigure);
            }
        }
        return outputFigures;
    }
}
