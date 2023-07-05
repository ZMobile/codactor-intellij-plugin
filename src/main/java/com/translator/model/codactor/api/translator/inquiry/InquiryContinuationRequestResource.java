package com.translator.model.codactor.api.translator.inquiry;

import com.translator.model.codactor.api.translator.inquiry.function.ChatGptFunction;

import java.util.List;

public class InquiryContinuationRequestResource {

    public static class Builder {
        private String previousInquiryChatId;
        private String question;
        private String openAiApiKey;
        private String model;
        private List<ChatGptFunction> functions;

        public Builder withPreviousInquiryChatId(String previousInquiryChatId) {
            this.previousInquiryChatId = previousInquiryChatId;
            return this;
        }

        public Builder withQuestion(String question) {
            this.question = question;
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

        public InquiryContinuationRequestResource build() {
            return new InquiryContinuationRequestResource(previousInquiryChatId, question, openAiApiKey, model, functions);
        }
    }

    private String previousInquiryChatId;
    private String question;
    private String openAiApiKey;
    private String model;
    private List<ChatGptFunction> functions;

    public InquiryContinuationRequestResource(String previousInquiryChatId,
                                              String question,
                                              String openAiApiKey,
                                              String model,
                                              List<ChatGptFunction> functions) {
        this.previousInquiryChatId = previousInquiryChatId;
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.functions = functions;
    }

    public String getPreviousInquiryChatId() {
        return previousInquiryChatId;
    }

    public void setPreviousInquiryChatId(String previousInquiryChatId) {
        this.previousInquiryChatId = previousInquiryChatId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
