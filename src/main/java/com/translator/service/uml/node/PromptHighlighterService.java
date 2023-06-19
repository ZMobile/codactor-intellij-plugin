package com.translator.service.uml.node;

import com.translator.view.uml.node.dialog.prompt.PromptNodeDialog;

public interface PromptHighlighterService {
    void highlightPrompts(PromptNodeDialog promptNodeDialog);

    void highlightPromptsWithoutRemoval(PromptNodeDialog promptNodeDialog);
}
