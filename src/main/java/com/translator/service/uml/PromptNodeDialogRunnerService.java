package com.translator.service.uml;

import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;

public interface PromptNodeDialogRunnerService {
    void run(LabeledRectangleFigure promptNodeFigure, PromptNode promptNode, String model);
}
