package com.translator.service.codactor.ai.openai.connection;

public interface DefaultConnectionService {
    String getOpenAiApiKey();

    void setOpenAiApiKey(String openAiApiKey);

    String[] getModels();
}
