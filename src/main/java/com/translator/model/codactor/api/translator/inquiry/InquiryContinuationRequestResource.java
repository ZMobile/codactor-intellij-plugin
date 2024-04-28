package com.translator.model.codactor.api.translator.inquiry;

import com.translator.model.codactor.ai.chat.function.GptFunction;

import java.util.List;

public class InquiryContinuationRequestResource {

    public static class Builder {
        private String previousInquiryChatId;
        private String question;
        private String openAiApiKey;
        private String model;
        private boolean azure;
        private String azureResource;
        private String azureDeployment;
        private List<GptFunction> functions;

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

        public Builder withAzure(boolean azure) {
            this.azure = azure;
            return this;
        }

        public Builder withAzureResource(String azureResource) {
            this.azureResource = azureResource;
            return this;
        }

        public Builder withAzureDeployment(String azureDeployment) {
            this.azureDeployment = azureDeployment;
            return this;
        }

        public Builder withFunctions(List<GptFunction> functions) {
            this.functions = functions;
            return this;
        }

        public InquiryContinuationRequestResource build() {
            return new InquiryContinuationRequestResource(previousInquiryChatId, question, openAiApiKey, model, azure, azureResource, azureDeployment, functions);
        }
    }

    private String previousInquiryChatId;
    private String question;
    private String openAiApiKey;
    private String model;
    private boolean azure;
    private String azureResource;
    private String azureDeployment;
    private List<GptFunction> functions;

    public InquiryContinuationRequestResource(String previousInquiryChatId,
                                              String question,
                                              String openAiApiKey,
                                              String model,
                                              boolean azure,
                                              String azureResource,
                                              String azureDeployment,
                                              List<GptFunction> functions) {
        this.previousInquiryChatId = previousInquiryChatId;
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.azure = azure;
        this.azureResource = azureResource;
        this.azureDeployment = azureDeployment;
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
