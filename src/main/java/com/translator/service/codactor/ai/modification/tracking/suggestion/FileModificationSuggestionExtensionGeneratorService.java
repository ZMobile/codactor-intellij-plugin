package com.translator.service.codactor.ai.modification.tracking.suggestion;

import com.translator.model.codactor.ai.modification.FileModification;

public interface FileModificationSuggestionExtensionGeneratorService {
    String generateExtension(FileModification fileModification);
}
