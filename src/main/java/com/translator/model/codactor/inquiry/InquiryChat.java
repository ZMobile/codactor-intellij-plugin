package com.translator.model.codactor.inquiry;

import com.translator.model.codactor.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.inquiry.function.ChatGptFunctionCall;

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
        private InquiryChatType inquiryChatType;
        private String likelyCodeLanguage;
        private String functionName;
        private ChatGptFunctionCall functionCall;
        private List<ChatGptFunction> functions;

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

        public Builder withInquiryChatType(InquiryChatType inquiryChatType) {
            this.inquiryChatType = inquiryChatType;
            return this;
        }

        public Builder withLikelyCodeLanguage(String likelyCodeLanguage) {
            this.likelyCodeLanguage = likelyCodeLanguage;
            return this;
        }

        public Builder withFunctionName(String functionName) {
            this.functionName = functionName;
            return this;
        }

        public Builder withFunctionCall(ChatGptFunctionCall functionCall) {
            this.functionCall = functionCall;
            return this;
        }

        public Builder withFunctions(List<ChatGptFunction> functions) {
            this.functions = functions;
            return this;
        }

        public InquiryChat build() {
            if (inquiryChatType == null) {
                inquiryChatType = InquiryChatType.DEFAULT;
            }
            return new InquiryChat(userId, inquiryId, filePath, previousInquiryChatId, from, message, likelyCodeLanguage, inquiryChatType, functionName, functionCall, functions);
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
    private InquiryChatType inquiryChatType;
    private String likelyCodeLanguage;
    private String functionName;
    private ChatGptFunctionCall functionCall;
    private List<ChatGptFunction> functions;
    private List<String> alternateInquiryChatIds;

    private InquiryChat(String userId,
                        String inquiryId,
                        String filePath,
                        String previousInquiryChatId,
                        String from,
                        String message,
                        String likelyCodeLanguage,
                        InquiryChatType inquiryChatType,
                        String functionName,
                        ChatGptFunctionCall functionCall,
                        List<ChatGptFunction> functions) {
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
        this.functionName = functionName;
        this.functionCall = functionCall;
        this.functions = functions;
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

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public ChatGptFunctionCall getFunctionCall() {
        return functionCall;
    }

    public void setFunctionCall(ChatGptFunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    public List<ChatGptFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<ChatGptFunction> functions) {
        this.functions = functions;
    }

    public List<String> getAlternateInquiryChatIds() {
        return alternateInquiryChatIds;
    }

    public void setAlternateInquiryChatIds(List<String> alternateInquiryChatIds) {
        this.alternateInquiryChatIds = alternateInquiryChatIds;
    }
}
