package com.translator.model.codactor.api.translator.modification;


import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.ModificationType;

import java.util.List;

public class DesktopCodeModificationRequestResource {
    private String filePath;
    private String suggestionId;
    private String code;
    private String modification;
    private ModificationType modificationType;
    private String openAiApiKey;
    private String model;
    private boolean azure;
    private String azureResource;
    private String azureDeployment;
    private List<HistoricalContextObjectHolder> priorContext;
    private String overrideCode;

    public DesktopCodeModificationRequestResource(String filePath,
                                                  String code,
                                                  String modification,
                                                  ModificationType modificationType,
                                                  String openAiApiKey,
                                                  String model,
                                                  boolean azure,
                                                  String azureResource,
                                                  String azureDeployment,
                                                  List<HistoricalContextObjectHolder> priorContext) {
        this.filePath = filePath;
        this.code = code;
        this.modification = modification;
        this.modificationType = modificationType;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.azure = azure;
        this.azureResource = azureResource;
        this.azureDeployment = azureDeployment;
        this.priorContext = priorContext;
    }

    public DesktopCodeModificationRequestResource(String filePath,
                                                  String code,
                                                  String modification,
                                                  ModificationType modificationType,
                                                  String openAiApiKey,
                                                  String model,
                                                  boolean azure,
                                                  String azureResource,
                                                  String azureDeployment,
                                                  String overrideCode) {
        this.filePath = filePath;
        this.code = code;
        this.modification = modification;
        this.modificationType = modificationType;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.azure = azure;
        this.azureResource = azureResource;
        this.azureDeployment = azureDeployment;
        this.overrideCode = overrideCode;
    }


    public DesktopCodeModificationRequestResource(String filePath,
                                                  String suggestionId,
                                                  String code,
                                                  String modification,
                                                  ModificationType modificationType,
                                                  String openAiApiKey,
                                                  String model,
                                                  boolean azure,
                                                  String azureResource,
                                                  String azureDeployment,
                                                  List<HistoricalContextObjectHolder> priorContext) {
        this.filePath = filePath;
        this.suggestionId = suggestionId;
        this.code = code;
        this.modification = modification;
        this.modificationType = modificationType;
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

    public String getSuggestionId() {
        return suggestionId;
    }

    public void setSuggestionId(String suggestionId) {
        this.suggestionId = suggestionId;
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

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
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

    public boolean isAzure() {
        return azure;
    }

    public String getAzureResource() {
        return azureResource;
    }

    public String getAzureDeployment() {
        return azureDeployment;
    }

    public List<HistoricalContextObjectHolder> getPriorContext() {
        return priorContext;
    }
}
