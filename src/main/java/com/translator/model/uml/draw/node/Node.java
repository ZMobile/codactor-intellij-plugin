package com.translator.model.uml.draw.node;

import java.util.UUID;

public class Node {
    private final String myId;
    private boolean running;
    private boolean processed;
    private String startedByNodeId;
    private String output;

    public Node() {
        this.myId = UUID.randomUUID().toString();
        this.running = false;
        this.output = null;
        this.startedByNodeId = null;
    }

    public String getId() {
        return myId;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getStartedByNodeId() {
        return startedByNodeId;
    }

    public void setStartedByNodeId(String startedByNodeId) {
        this.startedByNodeId = startedByNodeId;
    }
}