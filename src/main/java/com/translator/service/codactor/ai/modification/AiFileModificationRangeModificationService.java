package com.translator.service.codactor.ai.modification;

import com.translator.model.codactor.ai.modification.FileModification;

public interface AiFileModificationRangeModificationService {
    void modifyFileModificationRange(FileModification fileModification, int newStartIndex, int newEndIndex);
}
