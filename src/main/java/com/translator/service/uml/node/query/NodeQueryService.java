package com.translator.service.uml.node.query;

import com.translator.model.uml.connection.Connection;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import java.util.List;

public interface NodeQueryService {
    Figure getFigureFromId(Drawing drawing, String id);

    Figure getOutputFigureFromConnection(Drawing drawing, Connection connection);

    List<Figure> getOutputFiguresFromConnections(Drawing drawing, List<Connection> connections);
}
