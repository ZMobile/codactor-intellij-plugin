package com.translator.view.uml.factory.tool;

import com.translator.view.uml.node.tool.NodeConnectionTool;
import org.jhotdraw.draw.ConnectionFigure;

public interface NodeConnectionToolFactory {
    NodeConnectionTool create(ConnectionFigure connectionFigure);
}
