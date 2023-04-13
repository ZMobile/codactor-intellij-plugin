package com.translator.model.api.translator.inquiry;

public class InquiryContinuationRequestResource {
    private String previousInquiryChatId;
    private String question;
    private String openAiApiKey;
    private String model;

    public InquiryContinuationRequestResource(String previousInquiryChatId, String question, String openAiApiKey, String model) {
        this.previousInquiryChatId = previousInquiryChatId;
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
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
