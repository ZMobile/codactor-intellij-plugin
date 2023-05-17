package com.translator.model.uml.node;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Node {
    private final String myId;
    private List<NodeConnectionReference> connections;
    private boolean processed;

    public Node() {
        myId = UUID.randomUUID().toString();
    }

    public String getId() {
        return myId;
    }


    public List<NodeConnectionReference> getConnections() {
        return connections;
    }

    public void setConnections(List<NodeConnectionReference> connections) {
        this.connections = connections;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}