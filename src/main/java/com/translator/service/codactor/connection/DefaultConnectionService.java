package com.translator.service.codactor.connection;

import java.util.List;

public interface DefaultConnectionService {
    String getOpenAiApiKey();

    void setOpenAiApiKey(String openAiApiKey);

    String[] getModels();
}
