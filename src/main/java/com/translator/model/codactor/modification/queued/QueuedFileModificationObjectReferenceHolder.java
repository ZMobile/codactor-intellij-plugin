package com.translator.model.codactor.modification.queued;

import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.modification.MultiFileModification;

public class QueuedFileModificationObjectReferenceHolder {
    private String id;
    private QueuedModificationObjectType queuedModificationObjectType;

    public QueuedFileModificationObjectReferenceHolder(FileModification fileModification) {
        this.id = fileModification.getId();
        this.queuedModificationObjectType = QueuedModificationObjectType.FILE_MODIFICATION;
    }

    public QueuedFileModificationObjectReferenceHolder(FileModificationSuggestionModification fileModificationSuggestionModification) {
        this.id = fileModificationSuggestionModification.getId();
        this.queuedModificationObjectType = QueuedModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION;
    }

    public QueuedFileModificationObjectReferenceHolder(MultiFileModification multiFileModification) {
        this.id = multiFileModification.getId();
        this.queuedModificationObjectType = QueuedModificationObjectType.MULTI_FILE_MODIFICATION;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QueuedModificationObjectType getQueuedModificationObjectType() {
        return queuedModificationObjectType;
    }

    public void setQueuedModificationObjectType(QueuedModificationObjectType queuedModificationObjectType) {
        this.queuedModificationObjectType = queuedModificationObjectType;
    }
}