package com.translator.model.codactor.ai.chat.function;

import java.util.List;

public class FunctionCallResponseRequestResource {

    public static class Builder {
        private String previousInquiryChatId;
        private String functionName;
        private String content;
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

        public FunctionCallResponseRequestResource build() {
            return new FunctionCallResponseRequestResource(previousInquiryChatId, functionName, content, openAiApiKey, model, azure, azureResource, azureDeployment, functions);
        }
    }

    private FunctionCallResponseRequestResource(String previousInquiryChatId, String functionName, String content, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, List<GptFunction> functions) {
        this.previousInquiryChatId = previousInquiryChatId;
        this.functionName = functionName;
        this.content = content;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.azure = azure;
        this.azureResource = azureResource;
        this.azureDeployment = azureDeployment;
        this.functions = functions;
    }

    private String previousInquiryChatId;
    private String functionName;
    private String content;
    private String openAiApiKey;
    private String model;
    private boolean azure;
    private String azureResource;
    private String azureDeployment;
    private List<GptFunction> functions;

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

    public List<GptFunction> getFunctions() {
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

    public void setFunctions(List<GptFunction> functions) {
        this.functions = functions;
    }
}
