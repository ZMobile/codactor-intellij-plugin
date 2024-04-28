package com.translator.model.codactor.ai.chat;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.model.codactor.ai.modification.RecordType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class Inquiry {

    public static class Builder {
        private String userId;
        private String modificationId;
        private String subjectRecordId;
        private RecordType subjectRecordType;
        private String filePath;
        private String subjectLine;
        private String beforeCode;
        private String afterCode;
        private String subjectCode;
        private String description;
        private ModificationType modificationType;
        private String initialQuestion;
        private List<HistoricalContextObjectHolder> priorContext;

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withModificationId(String modificationId) {
            this.modificationId = modificationId;
            return this;
        }

        public Builder withSubjectRecordId(String subjectRecordId) {
            this.subjectRecordId = subjectRecordId;
            return this;
        }

        public Builder withSubjectRecordType(RecordType subjectRecordType) {
            this.subjectRecordType = subjectRecordType;
            return this;
        }

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withSubjectLine(String subjectLine) {
            this.subjectLine = subjectLine;
            return this;
        }

        public Builder withBeforeCode(String beforeCode) {
            this.beforeCode = beforeCode;
            return this;
        }

        public Builder withAfterCode(String afterCode) {
            this.afterCode = afterCode;
            return this;
        }

        public Builder withSubjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withModificationType(ModificationType modificationType) {
            this.modificationType = modificationType;
            return this;
        }

        public Builder withInitialQuestion(String initialQuestion) {
            this.initialQuestion = initialQuestion;
            return this;
        }

        public Builder withPriorContext(List<HistoricalContextObjectHolder> priorContext) {
            this.priorContext = priorContext;
            return this;
        }

        public Inquiry build() {
            return new Inquiry(userId, modificationId, subjectRecordId, subjectRecordType, filePath, subjectLine, beforeCode, afterCode, subjectCode, initialQuestion, description, modificationType, priorContext);
        }
    }

    private final String myId;
    private final String userId;
    private final LocalDateTime creationTimestamp;
    private LocalDateTime modifiedTimestamp;
    private String modificationId;
    private String subjectRecordId;
    private RecordType subjectRecordType;
    private String filePath;
    private String subjectLine;
    private String beforeCode;
    private String description;
    private String afterCode;
    private String subjectCode;
    private ModificationType modificationType;
    private String initialQuestion;
    private List<InquiryChat> chats;
    private List<HistoricalContextObjectHolder> priorContext;
    private String error;

    private Inquiry(String userId,
                   String modificationId,
                   String subjectRecordId,
                   RecordType subjectRecordType,
                   String filePath,
                   String subjectLine,
                   String beforeCode,
                   String afterCode,
                   String subjectCode,
                   String initialQuestion,
                   String description,
                   ModificationType modificationType,
                   List<HistoricalContextObjectHolder> priorContext) {
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.userId = userId;
        this.myId = subjectRecordId;
        this.modificationId = modificationId;
        this.subjectRecordId = subjectRecordId;
        this.subjectRecordType = subjectRecordType;
        this.filePath = filePath;
        this.subjectLine = subjectLine;
        this.beforeCode = beforeCode;
        this.afterCode = afterCode;
        this.subjectCode = subjectCode;
        this.initialQuestion = initialQuestion;
        this.description = description;
        this.modificationType = modificationType;
        this.chats = new ArrayList<>();
        this.priorContext = priorContext;
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

    public String getSubjectRecordId() {
        return subjectRecordId;
    }

    public String getModificationId() {
        return modificationId;
    }

    public void setModificationId(String modificationId) {
        this.modificationId = modificationId;
    }

    public void setSubjectRecordId(String subjectRecordId) {
        this.subjectRecordId = subjectRecordId;
    }

    public RecordType getSubjectRecordType() {
        return subjectRecordType;
    }

    public void setSubjectRecordType(RecordType subjectRecordType) {
        this.subjectRecordType = subjectRecordType;
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

    public String getBeforeCode() {
        return beforeCode;
    }

    public void setBeforeCode(String beforeCode) {
        this.beforeCode = beforeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAfterCode() {
        return afterCode;
    }

    public void setAfterCode(String afterCode) {
        this.afterCode = afterCode;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public String getInitialQuestion() {
        return initialQuestion;
    }

    public void setInitialQuestion(String initialQuestion) {
        this.initialQuestion = initialQuestion;
    }

    public List<InquiryChat> getChats() {
        return chats;
    }

    public String getError() {
        return error;
    }
}
