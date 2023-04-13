package com.translator.service.openai;

public interface OpenAiModelService {
    String getSelectedOpenAiModel();

    void setSelectedOpenAiModel(String selectedOpenAiModel);
}
