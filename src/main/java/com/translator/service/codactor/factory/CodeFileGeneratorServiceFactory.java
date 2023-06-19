package com.translator.service.codactor.factory;

import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.file.CodeFileGeneratorServiceImpl;

public interface CodeFileGeneratorServiceFactory {
    CodeFileGeneratorServiceImpl create(PromptContextService promptContextService);
}
