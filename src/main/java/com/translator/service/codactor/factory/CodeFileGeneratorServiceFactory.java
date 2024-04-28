package com.translator.service.codactor.factory;

import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.ide.file.CodeFileGeneratorServiceImpl;

public interface CodeFileGeneratorServiceFactory {
    CodeFileGeneratorServiceImpl create(PromptContextService promptContextService);
}
