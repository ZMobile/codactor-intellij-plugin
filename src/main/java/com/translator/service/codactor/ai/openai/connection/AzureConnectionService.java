package com.translator.service.codactor.ai.openai.connection;

import java.util.List;

public interface AzureConnectionService {
    String getKey();

    void setKey(String key);

    String getResource();

    void setResource(String resource);

    void setGpt35TurboDeployment(String deployment);

    String getGpt35TurboDeployment();

    void setGpt35Turbo16kDeployment(String deployment);

    String getGpt35Turbo16kDeployment();

    void setGpt4Deployment(String deployment);

    String getGpt4Deployment();

    void setGpt432kDeployment(String deployment);

    String getGpt432kDeployment();

    void setGpt4oDeployment(String deployment);

    String getGpt4oDeployment();

    List<String> getActiveModels();

    String getDeploymentForModel(String model);

    boolean isAzureConnected();

    String[] getModels();
}
