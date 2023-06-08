package com.translator.model.codactor.modification;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class MultiFileModification {
    private final String myId;
    private final LocalDateTime creationTimestamp;
    private String description;
    private String language;
    private String fileExtension;
    private String filePath;
    private String stage;

    public MultiFileModification(String description, String language, String fileExtension, String filePath) {
        this.myId = UUID.randomUUID().toString();
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.description = description;
        this.language = language;
        this.fileExtension = fileExtension;
        this.filePath = filePath;
        this.stage = "(0/3)";
    }

    public MultiFileModification(String description) {
        this.myId = UUID.randomUUID().toString();
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.description = description;
        this.stage = "";
    }

    public String getId() {
        return myId;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
