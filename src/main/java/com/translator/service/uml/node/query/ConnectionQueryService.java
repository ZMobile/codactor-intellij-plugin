package com.translator.service.uml.node.query;

import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.draw.figure.MetadataLabeledLineConnectionFigure;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import java.util.List;

public interface ConnectionQueryService {
    List<MetadataLabeledLineConnectionFigure> getInputsForNode(Drawing drawing, String nodeId);

    List<MetadataLabeledLineConnectionFigure> getOutputsForNode(Drawing drawing, String nodeId);
}
