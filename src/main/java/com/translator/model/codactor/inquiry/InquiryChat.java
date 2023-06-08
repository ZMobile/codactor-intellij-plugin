package com.translator.model.codactor.inquiry;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class InquiryChat {
    private final String myId;
    private final LocalDateTime creationTimestamp;
    private LocalDateTime modifiedTimestamp;
    private final String userId;
    private final String inquiryId;
    private final String filePath;
    private final String previousInquiryChatId;
    private String from;
    private String message;
    private InquiryChatType inquiryChatType;
    private String likelyCodeLanguage;
    private List<String> alternateInquiryChatIds;

    public InquiryChat(String userId, String inquiryId, String filePath, String previousInquiryChatId, String from, String message, String likelyCodeLanguage) {
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.myId = null;
        this.userId = userId;
        this.inquiryId = inquiryId;
        this.filePath = filePath;
        this.previousInquiryChatId = previousInquiryChatId;
        this.from = from;
        this.message = message;
        this.inquiryChatType = InquiryChatType.DEFAULT;
        this.alternateInquiryChatIds = new ArrayList<>();
        this.likelyCodeLanguage = likelyCodeLanguage;
    }

    public InquiryChat(String userId, String inquiryId, String filePath, String previousInquiryChatId, String from, String message, String likelyCodeLanguage, InquiryChatType inquiryChatType) {
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.modifiedTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.myId = null;
        this.userId = userId;
        this.inquiryId = inquiryId;
        this.filePath = filePath;
        this.previousInquiryChatId = previousInquiryChatId;
        this.from = from;
        this.message = message;
        this.inquiryChatType = inquiryChatType;
        this.alternateInquiryChatIds = new ArrayList<>();
        this.likelyCodeLanguage = likelyCodeLanguage;
    }

    public String getId() {
        return myId;
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

    public String getUserId() {
        return userId;
    }

    public String getInquiryId() {
        return inquiryId;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getPreviousInquiryChatId() {
        return previousInquiryChatId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InquiryChatType getInquiryChatType() {
        return inquiryChatType;
    }

    public void setInquiryChatType(InquiryChatType inquiryChatType) {
        this.inquiryChatType = inquiryChatType;
    }

    public String getLikelyCodeLanguage() {
        return likelyCodeLanguage;
    }

    public void setLikelyCodeLanguage(String likelyCodeLanguage) {
        this.likelyCodeLanguage = likelyCodeLanguage;
    }

    public List<String> getAlternateInquiryChatIds() {
        return alternateInquiryChatIds;
    }

    public void setAlternateInquiryChatIds(List<String> alternateInquiryChatIds) {
        this.alternateInquiryChatIds = alternateInquiryChatIds;
    }
}
