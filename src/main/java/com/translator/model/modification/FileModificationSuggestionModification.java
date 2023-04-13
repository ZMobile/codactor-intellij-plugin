package com.translator.model.modification;

public class FileModificationSuggestionModification extends FileModification {
    private final String modificationId;
    private final String suggestionId;
    private String editedCode;

    public FileModificationSuggestionModification(String filePath,
                                                  String modificationId,
                                                  String suggestionId,
                                                  int startIndex,
                                                  int endIndex,
                                                  String beforeText,
                                                  ModificationType modificationType) {
        super(filePath, startIndex, endIndex, beforeText, modificationType);
        this.modificationId = modificationId;
        this.suggestionId = suggestionId;
    }

    public String getModificationId() {
        return modificationId;
    }

    public String getSuggestionId() {
        return suggestionId;
    }

    public void setEditedCode(String editedCode) {
        this.editedCode = editedCode;
    }

    public String getEditedCode() {
        return editedCode;
    }

    public String getModificationSuggestionModificationRecordId() {
        return getModificationRecordId();
    }

    public void setModificationSuggestionModificationRecordId(String modificationSuggestionModificationRecordId) {
        setModificationRecordId(modificationSuggestionModificationRecordId);
    }
}
