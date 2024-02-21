package com.translator.model.uml.draw.connection;

import java.util.UUID;

public class Connection {
    private final String myId;
    private String inputNodeId;
    private String outputNodeId;
    private String inputKey;
    private String outputKey;
    private boolean open;

    public Connection(String inputNodeId, String outputNodeId) {
        this.myId = UUID.randomUUID().toString();
        this.inputNodeId = inputNodeId;
        this.outputNodeId = outputNodeId;
        this.inputKey = null;
        this.outputKey = null;
        this.open = false;
    }

    public String getId() {
        return myId;
    }

    public String getInputNodeId() {
        return inputNodeId;
    }

    public void setInputNodeId(String inputNodeId) {
        this.inputNodeId = inputNodeId;
    }

    public String getOutputNodeId() {
        return outputNodeId;
    }

    public void setOutputNodeId(String outputNodeId) {
        this.outputNodeId = outputNodeId;
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    public String getOutputKey() {
        return outputKey;
    }

    public void setOutputKey(String outputKey) {
        this.outputKey = outputKey;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
