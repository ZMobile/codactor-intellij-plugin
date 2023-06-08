package com.translator.view.uml.factory.tool;

import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.view.uml.tool.PromptNodeCreationTool;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;

public interface PromptNodeCreationToolFactory {
    PromptNodeCreationTool create(LabeledRectangleFigure labeledRectangleFigure);
}
