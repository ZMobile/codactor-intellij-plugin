package com.translator.model.codactor.modification;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class FileModificationSuggestionModificationRecord {
    private final LocalDateTime creationTimestamp;
    private LocalDateTime modifiedTimestamp;
    private String myId;
    private final String modificationId;
    private final String suggestionId;
    private String modificationSuggestionModificationId;
    private String filePath;
    private String subjectLine;
    private String beforeText;
    private String modification;
    private String editedCode;
    private ModificationType modificationType;
    private int responseCode;
    private String error;

    public FileModificationSuggestionModificationRecord(String modificationId,
                                                        String suggestionId,
                                                        String filePath,
                                                        String beforeText,
                                                        String modification,
                                                        String editedCode,
                                                        ModificationType modificationType) {
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.myId = UUID.randomUUID().toString();
        this.modificationId = modificationId;
        this.suggestionId = suggestionId;
        this.modificationSuggestionModificationId = null;
        this.filePath = filePath;
        this.beforeText = beforeText;
        this.modification = modification;
        this.editedCode = editedCode;
        this.modificationType = modificationType;
    }

    public FileModificationSuggestionModificationRecord(int responseCode, String error) {
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modificationId = null;
        this.suggestionId = null;
        this.modificationSuggestionModificationId = null;
        this.myId = UUID.randomUUID().toString();
        this.responseCode = responseCode;
        this.error = error;
    }

    public String getId() {
        return myId;
    }

    public void setId(String myId) {
        this.myId = myId;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public LocalDateTime getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public void updateModifiedTimestamp() {
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
    }

    public String getModificationId() {
        return modificationId;
    }

    public String getSuggestionId() {
        return suggestionId;
    }

    public String getModificationSuggestionModificationId() {
        return modificationSuggestionModificationId;
    }

    public void setModificationSuggestionModificationId(String modificationSuggestionModificationId) {
        this.modificationSuggestionModificationId = modificationSuggestionModificationId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSubjectLine() {
        return subjectLine;
    }

    public void setSubjectLine(String subjectLine) {
        this.subjectLine = subjectLine;
    }

    public String getBeforeText() {
        return beforeText;
    }

    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public String getEditedCode() {
        return editedCode;
    }

    public void setEditedCode(String editedCode) {
        this.editedCode = editedCode;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
