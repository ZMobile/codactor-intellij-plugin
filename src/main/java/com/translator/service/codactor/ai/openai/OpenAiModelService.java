package com.translator.service.codactor.ai.openai;

public interface OpenAiModelService {
    String getSelectedOpenAiModel();

    void setSelectedOpenAiModel(String selectedOpenAiModel);
}
