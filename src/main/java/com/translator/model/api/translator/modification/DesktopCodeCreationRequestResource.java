package com.translator.model.api.translator.modification;

import com.translator.model.history.HistoricalContextObjectHolder;

import java.util.List;

public class DesktopCodeCreationRequestResource {
    private String filePath;
    private String suggestionId;
    private String description;
    private String openAiApiKey;
    private String model;
    private List<HistoricalContextObjectHolder> priorContext;

    public DesktopCodeCreationRequestResource(String filePath,
                                              String description,
                                              String openAiApiKey,
                                              String model,
                                              List<HistoricalContextObjectHolder> priorContext) {
        this.filePath = filePath;
        this.description = description;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}