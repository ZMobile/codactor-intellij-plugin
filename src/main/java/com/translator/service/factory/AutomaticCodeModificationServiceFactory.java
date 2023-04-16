package com.translator.service.factory;

import com.translator.service.context.PromptContextService;
import com.translator.service.modification.AutomaticCodeModificationService;
import com.translator.service.modification.AutomaticCodeModificationServiceImpl;

public interface AutomaticCodeModificationServiceFactory {
    AutomaticCodeModificationServiceImpl create(PromptContextService promptContextService);
}
