package com.translator.service.codactor.ide.editor;

public interface GuardedBlockService {
    void addFileModificationGuardedBlock(String fileModificationId, int startOffset, int endOffset);

    void removeFileModificationGuardedBlock(String fileModificationId);

    void addFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId, int startOffset, int endOffset);

    void removeFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId);
}
