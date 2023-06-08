package com.translator.view.codactor.factory;

import com.translator.PromptContextBuilder;
import com.translator.service.codactor.context.PromptContextService;

public interface PromptContextBuilderFactory {
    PromptContextBuilder create(PromptContextService promptContextService);
}
