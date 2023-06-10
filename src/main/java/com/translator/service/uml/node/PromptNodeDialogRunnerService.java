package com.translator.service.uml.node;

import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;

public interface PromptNodeDialogRunnerService {
    void run(LabeledRectangleFigure promptNodeFigure, PromptNode promptNode, String model);
}
