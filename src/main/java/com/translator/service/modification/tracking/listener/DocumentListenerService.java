package com.translator.service.modification.tracking.listener;

public interface DocumentListenerService {
    void insertDocumentListener(String filePath);

    void removeDocumentListener(String filePath);

    void insertModificationSuggestionDocumentListener(String suggestionId);

    void removeModificationSuggestionDocumentListener(String suggestionId);
}
