package com.translator.model.codactor.api.translator.inquiry;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.function.ChatGptFunction;

import java.util.List;

public class GeneralInquiryCreationRequestResource {

    public static class Builder {
        private String question;
        private String openAiApiKey;
        private String model;
        private boolean azure;
        private String azureResource;
        private String azureDeployment;
        private List<HistoricalContextObjectHolder> priorContext;
        private List<ChatGptFunction> functions;
        private String systemMessage;

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

        public Builder withPriorContext(List<HistoricalContextObjectHolder> priorContext) {
            this.priorContext = priorContext;
            return this;
        }

        public Builder withFunctions(List<ChatGptFunction> functions) {
            this.functions = functions;
            return this;
        }

        public Builder withSystemMessage(String systemMessage) {
            this.systemMessage = systemMessage;
            return this;
        }

        public GeneralInquiryCreationRequestResource build() {
            return new GeneralInquiryCreationRequestResource(question, openAiApiKey, model, azure, azureResource, azureDeployment, priorContext, functions, systemMessage);
        }
    }

    private String question;
    private String openAiApiKey;
    private String model;
    private boolean azure;
    private String azureResource;
    private String azureDeployment;
    private List<HistoricalContextObjectHolder> priorContext;
    private List<ChatGptFunction> functions;
    private String systemMessage;

    public GeneralInquiryCreationRequestResource(String question, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, List<HistoricalContextObjectHolder> priorContext, List<ChatGptFunction> functions, String systemMessage) {
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.azure = azure;
        this.azureResource = azureResource;
        this.azureDeployment = azureDeployment;
        this.priorContext = priorContext;
        this.functions = functions;
        this.systemMessage = systemMessage;
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

    public List<HistoricalContextObjectHolder> getPriorContext() {
        return priorContext;
    }

    public void setPriorContext(List<HistoricalContextObjectHolder> priorContext) {
        this.priorContext = priorContext;
    }

    public List<ChatGptFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<ChatGptFunction> functions) {
        this.functions = functions;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }
}
