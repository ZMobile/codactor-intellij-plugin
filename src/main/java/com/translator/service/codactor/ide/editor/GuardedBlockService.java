package com.translator.service.codactor.ide.editor;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;

public interface GuardedBlockService {
    void addFileModificationGuardedBlock(FileModification fileModification, int startOffset, int endOffset);

    void addFileModificationGuardedBlock(String fileModificationId, int startOffset, int endOffset);

    void removeFileModificationGuardedBlock(String fileModificationId);

    void addFileModificationSuggestionModificationGuardedBlock(FileModificationSuggestionModification fileModificationSuggestionModification, int startOffset, int endOffset);

    void addFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId, int startOffset, int endOffset);

    void removeFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId);
}
