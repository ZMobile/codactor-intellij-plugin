package com.translator.service.uml;

import com.google.inject.Inject;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.view.uml.dialog.PromptNodeDialog;

import java.util.HashMap;
import java.util.Map;

public class NodeDialogWindowMapperServiceImpl implements NodeDialogWindowMapperService {
    private Map<LabeledRectangleFigure, PromptNodeDialog> promptNodeDialogMap;

    @Inject
    public NodeDialogWindowMapperServiceImpl() {
        this.promptNodeDialogMap = new HashMap<>();
    }

    public Map<LabeledRectangleFigure, PromptNodeDialog> getPromptNodeDialogMap() {
        return promptNodeDialogMap;
    }
}
