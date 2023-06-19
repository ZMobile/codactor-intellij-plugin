package com.translator.service.uml.node.runner;

import com.intellij.openapi.progress.ProgressIndicator;
import com.translator.model.codactor.task.CustomProgressIndicator;
import org.jhotdraw.draw.Drawing;

public interface PromptNodeRunnerService extends NodeRunnerService {
    void runNode(Drawing drawing, String startingNodeId, String nodeId, ProgressIndicator progressIndicator);
}
