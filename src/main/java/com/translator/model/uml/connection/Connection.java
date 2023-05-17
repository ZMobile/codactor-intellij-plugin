package com.translator.model.uml.connection;

import java.util.UUID;

public class Connection {
    private final String myId;
    private String inputNodeId;
    private String inputPromptId;
    private String outputNodeId;
    private String key;
    private boolean open;

    public Connection() {
        myId = UUID.randomUUID().toString();
    }
}
