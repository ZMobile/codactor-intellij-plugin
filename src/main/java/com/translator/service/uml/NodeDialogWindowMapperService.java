package com.translator.service.uml;

import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.view.uml.dialog.PromptNodeDialog;

import java.util.Map;

public interface NodeDialogWindowMapperService {
    Map<LabeledRectangleFigure, PromptNodeDialog> getPromptNodeDialogMap();;
}
