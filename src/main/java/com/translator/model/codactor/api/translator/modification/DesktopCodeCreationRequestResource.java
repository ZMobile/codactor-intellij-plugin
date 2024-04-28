package com.translator.model.codactor.api.translator.modification;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;

import java.util.List;

public class DesktopCodeCreationRequestResource {
    private String filePath;
    private String suggestionId;
    private String description;
    private String openAiApiKey;
    private String model;
    private boolean azure;
    private String azureResource;
    private String azureDeployment;
    private List<HistoricalContextObjectHolder> priorContext;
    private String overrideCode;

    public DesktopCodeCreationRequestResource(String filePath,
                                              String description,
                                              String openAiApiKey,
                                              String model,
                                              boolean azure,
                                              String azureResource,
                                              String azureDeployment,
                                              List<HistoricalContextObjectHolder> priorContext) {
        this.filePath = filePath;
        this.description = description;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.azure = azure;
        this.azureResource = azureResource;
        this.azureDeployment = azureDeployment;
        this.priorContext = priorContext;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSuggestionId() {
        return suggestionId;
    }

    public void setSuggestionId(String suggestionId) {
        this.suggestionId = suggestionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }

    public boolean isAzure() {
        return azure;
    }

    public void setAzure(boolean azure) {
        this.azure = azure;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAzureResource() {
        return azureResource;
    }

    public void setAzureResource(String azureResource) {
        this.azureResource = azureResource;
    }

    public String getAzureDeployment() {
        return azureDeployment;
    }

    public void setAzureDeployment(String azureDeployment) {
        this.azureDeployment = azureDeployment;
    }

    public String getOverrideCode() {
        return overrideCode;
    }

    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }
}