package com.translator.service.codactor.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;

public interface InterfaceTemplateGeneratorService {
    Inquiry generateInterfaceTemplate(String interfaceName, String filePath, String description);
}
