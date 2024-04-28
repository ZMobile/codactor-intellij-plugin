package com.translator.service.codactor.ai.chat.functions;

public class TestObject {
    String path;
    String startBoundary;
    String endBoundary;
    String replacementCodeSnippet;
    String modificationType;
    String description;

    public TestObject(String path, String startBoundary, String endBoundary, String replacementCodeSnippet, String modificationType, String description) {
        this.path = path;
        this.startBoundary = startBoundary;
        this.endBoundary = endBoundary;
        this.replacementCodeSnippet = replacementCodeSnippet;
        this.modificationType = modificationType;
        this.description = description;
    }
}
