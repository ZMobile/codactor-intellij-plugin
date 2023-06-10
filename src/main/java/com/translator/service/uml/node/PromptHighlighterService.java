package com.translator.service.uml.node;

import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.draw.figure.MetadataLabeledLineConnectionFigure;
import com.translator.view.uml.dialog.prompt.PromptNodeDialog;
import com.translator.view.uml.dialog.prompt.PromptViewer;

public interface PromptHighlighterService {
    void highlightPrompts(PromptNodeDialog promptNodeDialog);

    void highlightPromptsWithoutRemoval(PromptNodeDialog promptNodeDialog);
}
