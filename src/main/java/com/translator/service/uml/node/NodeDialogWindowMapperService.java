package com.translator.service.uml.node;

import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.view.uml.dialog.prompt.PromptNodeDialog;

import java.util.Map;

public interface NodeDialogWindowMapperService {
    Map<LabeledRectangleFigure, PromptNodeDialog> getPromptNodeDialogMap();;
}
