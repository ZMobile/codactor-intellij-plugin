package com.translator.model.codactor.modification.data;

import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.modification.MultiFileModification;

public class FileModificationDataReferenceHolder {
    private String id;
    private ModificationObjectType modificationObjectType;

    public FileModificationDataReferenceHolder(FileModification fileModification) {
        this.id = fileModification.getId();
        this.modificationObjectType = ModificationObjectType.FILE_MODIFICATION;
    }

    public FileModificationDataReferenceHolder(FileModificationSuggestionModification fileModificationSuggestionModification) {
        this.id = fileModificationSuggestionModification.getId();
        this.modificationObjectType = ModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION;
    }

    public FileModificationDataReferenceHolder(MultiFileModification multiFileModification) {
        this.id = multiFileModification.getId();
        this.modificationObjectType = ModificationObjectType.MULTI_FILE_MODIFICATION;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ModificationObjectType getQueuedModificationObjectType() {
        return modificationObjectType;
    }

    public void setQueuedModificationObjectType(ModificationObjectType modificationObjectType) {
        this.modificationObjectType = modificationObjectType;
    }
}