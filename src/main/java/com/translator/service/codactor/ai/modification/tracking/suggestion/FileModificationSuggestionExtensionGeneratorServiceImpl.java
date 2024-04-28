package com.translator.service.codactor.ai.modification.tracking.suggestion;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerService;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerServiceImpl;

public class FileModificationSuggestionExtensionGeneratorServiceImpl implements FileModificationSuggestionExtensionGeneratorService {
    @Override
    public String generateExtension(FileModification fileModification) {
        String extension;
        if (fileModification.getModificationType() == ModificationType.TRANSLATE) {
            extension = fileModification.getNewFileType().trim().toLowerCase();
            if (extension.startsWith(".")) {
                extension = extension.substring(1);
            }
        } else {
            String filePath = fileModification.getFilePath();
            extension = filePath.substring(filePath.lastIndexOf('.') + 1);
        }
        return extension;
    }
}
