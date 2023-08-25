package com.translator.service.uml.node.runner;

import com.intellij.openapi.progress.ProgressIndicator;
import org.jhotdraw.draw.Drawing;

public interface NodeRunnerService {
    void runNode(Drawing drawing, String startingNodeId, String nodeId, ProgressIndicator progressIndicator);
}
