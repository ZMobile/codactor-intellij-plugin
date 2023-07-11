package com.translator.model.codactor.modification.data;

import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.modification.MultiFileModification;

public class FileModificationDataHolder {
    private FileModification fileModification;
    private FileModificationSuggestionModification fileModificationSuggestionModification;
    private MultiFileModification multiFileModification;
    private ModificationObjectType modificationObjectType;

    public FileModificationDataHolder(FileModification fileModification) {
        this.fileModification = fileModification;
        this.modificationObjectType = ModificationObjectType.FILE_MODIFICATION;
    }

    public FileModificationDataHolder(FileModificationSuggestionModification fileModificationSuggestionModification) {
        this.fileModificationSuggestionModification = fileModificationSuggestionModification;
        this.modificationObjectType = ModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION;
    }

    public FileModificationDataHolder(MultiFileModification multiFileModification) {
        this.multiFileModification = multiFileModification;
        this.modificationObjectType = ModificationObjectType.MULTI_FILE_MODIFICATION;
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

    public ModificationObjectType getQueuedModificationObjectType() {
        return modificationObjectType;
    }

    public void setQueuedModificationObjectType(ModificationObjectType modificationObjectType) {
        this.modificationObjectType = modificationObjectType;
    }
}