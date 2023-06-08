package com.translator.model.codactor.api.translator.modification;

import com.translator.model.codactor.modification.FileModificationSuggestionRecord;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DesktopCodeTranslationResponseResource {
    private final LocalDateTime creationTimestamp;
    private LocalDateTime modifiedTimestamp;
    private final String myId;
    private final String userId;
    private String filePath;
    private String beforeText;
    private String newLanguage;
    private String newFileType;
    private List<FileModificationSuggestionRecord> modificationSuggestions;
    private String model;
    private int responseCode;
    private String error;

    public DesktopCodeTranslationResponseResource(String userId, String filePath, String beforeText, String newLanguage, String newFileType, String model) {
        this.myId = UUID.randomUUID().toString();
        this.userId = userId;
        this.filePath = filePath;
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.beforeText = beforeText;
        this.newLanguage = newLanguage;
        this.newFileType = newFileType;
        this.modificationSuggestions = new ArrayList<>();
        this.model = model;
    }

    public DesktopCodeTranslationResponseResource(int responseCode, String error) {
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

    public String getNewLanguage() {
        return newLanguage;
    }

    public void setNewLanguage(String newLanguage) {
        this.newLanguage = newLanguage;
    }

    public String getNewFileType() {
        return newFileType;
    }

    public void setNewFileType(String newFileType) {
        this.newFileType = newFileType;
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
