package com.translator.service.codactor.connection;

import java.util.List;

public interface AzureConnectionService {
    String getKey();

    void setKey(String key);

    String getResource();

    void setResource(String resource);

    void getGpt35TurboDeployment(String deployment);

    String getGpt35TurboDeployment();

    void getGpt35Turbo16kDeployment(String deployment);

    String getGpt35Turbo16kDeployment();

    void getGpt35Turbo32kDeployment(String deployment);

    String getGpt35Turbo32kDeployment();

    List<String> getActiveModels();

    boolean isAzureConnected();
}
