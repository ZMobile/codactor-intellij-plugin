package com.translator.service.codactor.openai;

public interface OpenAiModelService {
    String getSelectedOpenAiModel();

    void setSelectedOpenAiModel(String selectedOpenAiModel);
}
