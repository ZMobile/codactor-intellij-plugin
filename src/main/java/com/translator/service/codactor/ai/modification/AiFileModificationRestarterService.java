package com.translator.service.codactor.ai.modification;

import com.translator.model.codactor.ai.modification.FileModification;

public interface AiFileModificationRestarterService {
    void restartFileModification(FileModification fileModification);
}
