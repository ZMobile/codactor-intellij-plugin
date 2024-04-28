package com.translator.model.uml.prompt;

import java.util.UUID;

public class Prompt {
    private String myId;
    private String nodeId;
    private String prompt;
    private String functions;
    private boolean processed;

    public Prompt(String nodeId, String prompt) {
        this.myId = UUID.randomUUID().toString();
        this.nodeId = nodeId;
        this.prompt = prompt;
        this.processed = false;
    }

    public String getId() {
        return myId;
    }

    public void setId(String id) {
        this.myId = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getFunctions() {
        return functions;
    }

    public void setFunctions(String functions) {
        this.functions = functions;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
