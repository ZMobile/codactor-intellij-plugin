package com.translator.service.codactor.factory;

import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.modification.AutomaticCodeModificationServiceImpl;

public interface AutomaticCodeModificationServiceFactory {
    AutomaticCodeModificationServiceImpl create(PromptContextService promptContextService);
}
