package com.translator.service.modification.tracking.listener;


public interface UneditableSegmentListenerService {
    void addUneditableFileModificationSegmentListener(String modificationId);

    void removeUneditableFileModificationSegmentListener(String modificationId);

    void addUneditableFileModificationSuggestionModificationSegmentListener(String modificationSuggestionModificationId);

    void removeUneditableFileModificationSuggestionModificationSegmentListener(String modificationSuggestionModificationId);
}