package com.translator.model.codactor.modification;

public class QueuedFileModificationObjectHolder {
    private FileModification fileModification;
    private FileModificationSuggestionModification fileModificationSuggestionModification;
    private MultiFileModification multiFileModification;
    private QueuedModificationObjectType queuedModificationObjectType;

    public QueuedFileModificationObjectHolder(FileModification fileModification) {
        this.fileModification = fileModification;
        this.queuedModificationObjectType = QueuedModificationObjectType.FILE_MODIFICATION;
    }

    public QueuedFileModificationObjectHolder(FileModificationSuggestionModification fileModificationSuggestionModification) {
        this.fileModificationSuggestionModification = fileModificationSuggestionModification;
        this.queuedModificationObjectType = QueuedModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION;
    }

    public QueuedFileModificationObjectHolder(MultiFileModification multiFileModification) {
        this.multiFileModification = multiFileModification;
        this.queuedModificationObjectType = QueuedModificationObjectType.MULTI_FILE_MODIFICATION;
    }

    public FileModification getFileModification() {
        return fileModification;
    }

    public void setFileModification(FileModification fileModification) {
        this.fileModification = fileModification;
    }

    public FileModificationSuggestionModification getFileModificationSuggestionModification() {
        return fileModificationSuggestionModification;
    }

    public void setFileModificationSuggestionModification(FileModificationSuggestionModification fileModificationSuggestionModification) {
        this.fileModificationSuggestionModification = fileModificationSuggestionModification;
    }

    public MultiFileModification getMultiFileModification() {
        return multiFileModification;
    }

    public void setMultiFileModification(MultiFileModification multiFileModification) {
        this.multiFileModification = multiFileModification;
    }

    public QueuedModificationObjectType getQueuedModificationObjectType() {
        return queuedModificationObjectType;
    }

    public void setQueuedModificationObjectType(QueuedModificationObjectType queuedModificationObjectType) {
        this.queuedModificationObjectType = queuedModificationObjectType;
    }
}