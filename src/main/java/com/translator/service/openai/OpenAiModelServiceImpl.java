package com.translator.service.openai;

import javax.inject.Inject;

public class OpenAiModelServiceImpl implements OpenAiModelService {
    private String selectedOpenAiModel;

    @Inject
    public OpenAiModelServiceImpl() {
        this.selectedOpenAiModel = "gpt-3.5-turbo";
    }

    public String getSelectedOpenAiModel() {
        return selectedOpenAiModel;
    }

    public void setSelectedOpenAiModel(String selectedOpenAiModel) {
        this.selectedOpenAiModel = selectedOpenAiModel;
    }
}
