package com.translator.model.codactor.ai.modification;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileModificationSuggestionRecord {
    private final LocalDateTime creationTimestamp;
    private LocalDateTime modifiedTimestamp;
    private final String myId;
    private final String userId;
    private final String modificationId;
    private final ModificationType modificationType;
    private final String filePath;
    private final String subjectLine;
    private final String beforeCode;
    private final String modification;
    private final String suggestedCodeBeforeRestoration;
    private final String suggestedCode;
    private final String language;
    private final List<String> modificationSuggestionModificationIds;

    public FileModificationSuggestionRecord(String userId,
                                            String modificationId,
                                            ModificationType modificationType,
                                            String filePath,
                                            String subjectLine,
                                            String beforeCode,
                                            String modification,
                                            String suggestedCodeBeforeRestoration,
                                            String suggestedCode) {
        this.myId = UUID.randomUUID().toString();
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.userId = userId;
        this.modificationId = modificationId;
        this.modificationType = modificationType;
        this.filePath = filePath;
        this.subjectLine = subjectLine;
        this.beforeCode = beforeCode;
        this.modification = modification;
        this.suggestedCodeBeforeRestoration = suggestedCodeBeforeRestoration;
        this.suggestedCode = suggestedCode;
        this.language = null;
        this.modificationSuggestionModificationIds = new ArrayList<>();
    }

    public String getId() {
        return myId;
    }

    public String getUserId() {
        return userId;
    }

    public String getModificationId() {
        return modificationId;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSubjectLine() {
        return subjectLine;
    }

    public String getBeforeCode() {
        return beforeCode;
    }

    public String getModification() {
        return modification;
    }

    public String getSuggestedCodeBeforeRestoration() {
        return suggestedCodeBeforeRestoration;
    }

    public String getSuggestedCode() {
        return suggestedCode;
    }

    public String getLanguage() {
        return language;
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

    public List<String> getModificationSuggestionModificationIds() {
        return modificationSuggestionModificationIds;
    }
}
