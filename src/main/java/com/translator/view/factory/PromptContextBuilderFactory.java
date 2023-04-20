package com.translator.view.factory;

import com.intellij.ui.components.JBLabel;
import com.translator.PromptContextBuilder;
import com.translator.service.context.PromptContextService;

public interface PromptContextBuilderFactory {
    PromptContextBuilder create(PromptContextService promptContextService);
}
