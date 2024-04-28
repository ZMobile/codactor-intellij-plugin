package com.translator.service.codactor.ai.openai.connection;

public interface CodactorConnectionService {
    CodactorConnectionType setConnectionType(CodactorConnectionType connectionType);

    CodactorConnectionType getConnectionType();
}
