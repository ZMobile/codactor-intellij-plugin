package com.translator.service.codactor.factory;

import com.translator.service.codactor.ai.chat.context.PromptContextServiceImpl;

public interface PromptContextServiceFactory {
    PromptContextServiceImpl create();
}
