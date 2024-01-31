package com.translator.view.uml.factory.tool;

import com.translator.service.uml.node.tool.NodeConnectionTool;
import org.jhotdraw.draw.ConnectionFigure;

public interface NodeConnectionToolFactory {
    NodeConnectionTool create(ConnectionFigure connectionFigure);
}
