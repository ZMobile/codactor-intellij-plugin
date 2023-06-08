package com.translator.service.codactor.factory;

import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.modification.AutomaticMassCodeModificationServiceImpl;

public interface AutomaticMassCodeModificationServiceFactory {
    AutomaticMassCodeModificationServiceImpl create(PromptContextService promptContextService);
}
