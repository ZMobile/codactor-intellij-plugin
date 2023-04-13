package com.translator.model.api.translator.modification;

import com.translator.model.modification.FileModificationSuggestionRecord;
import com.translator.model.modification.ModificationType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DesktopCodeModificationResponseResource {
    private final LocalDateTime creationTimestamp;
    private LocalDateTime modifiedTimestamp;
    private final String myId;
    private final String userId;
    private String filePath;
    private String beforeText;
    private String modification;
    private ModificationType modificationType;
    private List<FileModificationSuggestionRecord> modificationSuggestions;
    private String model;
    private int responseCode;
    private String error;

    public DesktopCodeModificationResponseResource(String userId, String filePath, String beforeText, String modification, ModificationType modificationType, String model) {
        this.myId = UUID.randomUUID().toString();
        this.userId = userId;
        this.filePath = filePath;
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.beforeText = beforeText;
        this.modification = modification;
        this.modificationType = modificationType;
        this.modificationSuggestions = new ArrayList<>();
        this.model = model;
    }

    public DesktopCodeModificationResponseResource(int responseCode, String error) {
        this.myId = UUID.randomUUID().toString();
        this.userId = null;
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.responseCode = responseCode;
        this.error = error;
    }
    public String getId() {
        return myId;
    }

    public String getUserId() {
        return userId;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<FileModificationSuggestionRecord> getModificationSuggestions() {
        return modificationSuggestions;
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
