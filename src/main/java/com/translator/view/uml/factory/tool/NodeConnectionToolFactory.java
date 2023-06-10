package com.translator.view.uml.factory.tool;

import com.translator.model.uml.draw.figure.MetadataLabeledLineConnectionFigure;
import com.translator.view.uml.tool.NodeConnectionTool;
import org.jhotdraw.draw.ConnectionFigure;

public interface NodeConnectionToolFactory {
    NodeConnectionTool create(ConnectionFigure connectionFigure);
}
