package com.translator.service.codactor.factory;

import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.context.PromptContextServiceImpl;

public interface PromptContextServiceFactory {
    PromptContextServiceImpl create();
}
