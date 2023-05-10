package com.translator.view.factory;

import com.translator.view.dialog.PromptContextBuilder;
import com.translator.service.context.PromptContextService;

public interface PromptContextBuilderFactory {
    PromptContextBuilder create(PromptContextService promptContextService);
}
