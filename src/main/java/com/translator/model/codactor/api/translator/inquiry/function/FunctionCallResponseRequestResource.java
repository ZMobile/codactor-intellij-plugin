package com.translator.model.codactor.api.translator.inquiry.function;

import java.util.List;

public class FunctionCallResponseRequestResource {

    public static class Builder {
        private String previousInquiryChatId;
        private String functionName;
        private String content;
        private String openAiApiKey;
        private String model;
        private List<ChatGptFunction> functions;

        public Builder withPreviousInquiryChatId(String previousInquiryChatId) {
            this.previousInquiryChatId = previousInquiryChatId;
            return this;
        }

        public Builder withFunctionName(String functionName) {
            this.functionName = functionName;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withOpenAiApiKey(String openAiApiKey) {
            this.openAiApiKey = openAiApiKey;
            return this;
        }

        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        public Builder withFunctions(List<ChatGptFunction> functions) {
            this.functions = functions;
            return this;
        }

        public FunctionCallResponseRequestResource build() {
            return new FunctionCallResponseRequestResource(this.previousInquiryChatId, this.functionName, this.content, this.openAiApiKey, this.model, this.functions);
        }
    }

    private FunctionCallResponseRequestResource(String previousInquiryChatId, String functionName, String content, String openAiApiKey, String model, List<ChatGptFunction> functions) {
        this.previousInquiryChatId = previousInquiryChatId;
        this.functionName = functionName;
        this.content = content;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.functions = functions;
    }

    private String previousInquiryChatId;
    private String functionName;
    private String content;
    private String openAiApiKey;
    private String model;
    private List<ChatGptFunction> functions;

    // Getters
    public String getPreviousInquiryChatId() {
        return this.previousInquiryChatId;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public String getContent() {
        return this.content;
    }

    public String getOpenAiApiKey() {
        return this.openAiApiKey;
    }

    public String getModel() {
        return this.model;
    }

    public List<ChatGptFunction> getFunctions() {
        return this.functions;
    }

    // Setters
    public void setPreviousInquiryChatId(String previousInquiryChatId) {
        this.previousInquiryChatId = previousInquiryChatId;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setFunctions(List<ChatGptFunction> functions) {
        this.functions = functions;
    }
}
