package com.translator.service.codactor.ai.modification.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;

public interface InterfaceTemplateGeneratorService {
    Inquiry generateInterfaceTemplate(String interfaceName, String filePath, String description);
}
