package com.translator.view.uml.factory.tool;

import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.view.uml.tool.PromptNodeCreationTool;

public interface PromptNodeCreationToolFactory {
    PromptNodeCreationTool create(LabeledRectangleFigure labeledRectangleFigure);
}
