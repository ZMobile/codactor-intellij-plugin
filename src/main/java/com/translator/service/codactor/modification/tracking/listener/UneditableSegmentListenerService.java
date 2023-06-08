package com.translator.service.codactor.modification.tracking.listener;


public interface UneditableSegmentListenerService {
    void addUneditableFileModificationSegmentListener(String modificationId);

    void removeUneditableFileModificationSegmentListener(String modificationId);

    void addUneditableFileModificationSuggestionModificationSegmentListener(String modificationSuggestionModificationId);

    void removeUneditableFileModificationSuggestionModificationSegmentListener(String modificationSuggestionModificationId);
}