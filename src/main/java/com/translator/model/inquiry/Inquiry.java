package com.translator.model.inquiry;

import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.modification.ModificationType;
import com.translator.model.modification.RecordType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inquiry {
    private final String myId;
    private final String userId;
    private final LocalDateTime creationTimestamp;
    private LocalDateTime modifiedTimestamp;
    private String modificationId;
    private String subjectRecordId;
    private RecordType subjectRecordType;
    private String filePath;
    private String beforeCode;
    private String description;
    private String afterCode;
    private String subjectCode;
    private ModificationType modificationType;
    private String initialQuestion;
    private List<InquiryChat> chats;
    private List<HistoricalContextObjectHolder> priorContext;

    public Inquiry(String userId,
                   String modificationId,
                   String subjectRecordId,
                   RecordType subjectRecordType,
                   String filePath,
                   String beforeCode,
                   String description,
                   String afterCode,
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
        this.beforeCode = beforeCode;
        this.description = description;
        this.afterCode = afterCode;
        this.modificationType = modificationType;
        this.chats = new ArrayList<>();
        this.priorContext = new ArrayList<>();
    }

    public Inquiry(String userId,
                         String filePath,
                         String subjectCode,
                         String initialQuestion,
                         List<HistoricalContextObjectHolder> priorContext) {
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.myId = UUID.randomUUID().toString();
        this.userId = userId;
        this.filePath = filePath;
        this.subjectCode = subjectCode;
        this.initialQuestion = initialQuestion;
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
}
