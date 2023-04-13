package com.translator.model.modification;

public class FileModificationUpdate {
    private String modificationId;
    private String modification;

    public FileModificationUpdate(String modificationId, String modification) {
        this.modificationId = modificationId;
        this.modification = modification;
    }

    public String getModificationId() {
        return modificationId;
    }

    public void setModificationId(String modificationId) {
        this.modificationId = modificationId;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }
}
