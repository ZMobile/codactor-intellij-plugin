package com.translator.model.codactor.api.translator.modification;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;

import java.util.List;

public class DesktopCodeTranslationRequestResource {
    private String filePath;
    private String code;
    private String newLanguage;
    private String newFileType;
    private String openAiApiKey;
    private String model;
    private boolean azure;
    private String azureResource;
    private String azureDeployment;
    private List<HistoricalContextObjectHolder> priorContext;
    private String overrideCode;

    public DesktopCodeTranslationRequestResource(String filePath,
                                                 String code,
                                                 String newLanguage,
                                                 String newFileType,
                                                 String openAiApiKey,
                                                 String model,
                                                 boolean azure,
                                                 String azureResource,
                                                 String azureDeployment,
                                                 List<HistoricalContextObjectHolder> priorContext) {
        this.filePath = filePath;
        this.code = code;
        this.newLanguage = newLanguage;
        this.newFileType = newFileType;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewLanguage() {
        return newLanguage;
    }

    public void setNewLanguage(String newLanguage) {
        this.newLanguage = newLanguage;
    }

    public String getNewFileType() {
        return newFileType;
    }

    public void setNewFileType(String newFileType) {
        this.newFileType = newFileType;
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

    public boolean isAzure() {
        return azure;
    }

    public void setAzure(boolean azure) {
        this.azure = azure;
    }

    public List<HistoricalContextObjectHolder> getPriorContext() {
        return priorContext;
    }

    public void setPriorContext(List<HistoricalContextObjectHolder> priorContext) {
        this.priorContext = priorContext;
    }

    public String getOverrideCode() {
        return overrideCode;
    }

    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }
}
