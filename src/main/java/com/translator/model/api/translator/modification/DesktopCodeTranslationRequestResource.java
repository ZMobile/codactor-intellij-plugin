package com.translator.model.api.translator.modification;

import com.translator.model.history.HistoricalContextObjectHolder;

import java.util.List;

public class DesktopCodeTranslationRequestResource {
    private String filePath;
    private String code;
    private String newLanguage;
    private String newFileType;
    private String openAiApiKey;
    private String model;
    private List<HistoricalContextObjectHolder> priorContext;

    public DesktopCodeTranslationRequestResource(String filePath,
                                                 String code,
                                                 String newLanguage,
                                                 String newFileType,
                                                 String openAiApiKey,
                                                 String model,
                                                 List<HistoricalContextObjectHolder> priorContext) {
        this.filePath = filePath;
        this.code = code;
        this.newLanguage = newLanguage;
        this.newFileType = newFileType;
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

    public List<HistoricalContextObjectHolder> getPriorContext() {
        return priorContext;
    }

    public void setPriorContext(List<HistoricalContextObjectHolder> priorContext) {
        this.priorContext = priorContext;
    }
}
