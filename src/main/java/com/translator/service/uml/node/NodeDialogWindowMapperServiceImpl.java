package com.translator.service.uml.node;

import com.google.inject.Inject;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.view.uml.node.dialog.prompt.PromptNodeDialog;

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
