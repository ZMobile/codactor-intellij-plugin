package com.translator.service.factory;

import com.translator.service.context.PromptContextService;
import com.translator.service.modification.AutomaticMassCodeModificationServiceImpl;

public interface AutomaticMassCodeModificationServiceFactory {
    AutomaticMassCodeModificationServiceImpl create(PromptContextService promptContextService);
}
