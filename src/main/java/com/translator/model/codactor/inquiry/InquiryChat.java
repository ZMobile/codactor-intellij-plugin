package com.translator.model.codactor.inquiry;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class InquiryChat {

    public static class Builder {
        private String userId;
        private String inquiryId;
        private String filePath;
        private String previousInquiryChatId;
        private String from;
        private String message;
        private String functionCall;
        private InquiryChatType inquiryChatType;
        private String likelyCodeLanguage;

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withInquiryId(String inquiryId) {
            this.inquiryId = inquiryId;
            return this;
        }

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withPreviousInquiryChatId(String previousInquiryChatId) {
            this.previousInquiryChatId = previousInquiryChatId;
            return this;
        }

        public Builder withFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withFunctionCall(String functionCall) {
            this.functionCall = functionCall;
            return this;
        }

        public Builder withInquiryChatType(InquiryChatType inquiryChatType) {
            this.inquiryChatType = inquiryChatType;
            return this;
        }

        public Builder withLikelyCodeLanguage(String likelyCodeLanguage) {
            this.likelyCodeLanguage = likelyCodeLanguage;
            return this;
        }

        public InquiryChat build() {
            if (inquiryChatType == null) {
                inquiryChatType = InquiryChatType.DEFAULT;
            }
            return new InquiryChat(userId, inquiryId, filePath, previousInquiryChatId, from, message, likelyCodeLanguage, inquiryChatType);
        }
    }

    private final String myId;
    private final LocalDateTime creationTimestamp;
    private LocalDateTime modifiedTimestamp;
    private final String userId;
    private final String inquiryId;
    private final String filePath;
    private final String previousInquiryChatId;
    private String from;
    private String message;
    private String functionCall;
    private InquiryChatType inquiryChatType;
    private String likelyCodeLanguage;
    private List<String> alternateInquiryChatIds;

    private InquiryChat(String userId, String inquiryId, String filePath, String previousInquiryChatId, String from, String message, String likelyCodeLanguage, InquiryChatType inquiryChatType) {
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
