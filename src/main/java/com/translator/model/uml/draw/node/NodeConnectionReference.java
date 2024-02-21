package com.translator.model.uml.draw.node;

public class NodeConnectionReference {
    private String connectionId;
    private boolean output;
    private String key;

    public NodeConnectionReference() {
        this.connectionId = null;
        this.output = false;
        this.key = null;
    }

    public NodeConnectionReference(String connectionId, boolean output, String key) {
        this.connectionId = connectionId;
        this.output = output;
        this.key = key;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
