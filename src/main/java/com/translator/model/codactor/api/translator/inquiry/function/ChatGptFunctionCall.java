package com.translator.model.codactor.api.translator.inquiry.function;

public class ChatGptFunctionCall {
    private String name;
    private String arguments;

    public ChatGptFunctionCall(String name, String arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String getArguments() {
        return arguments;
    }

    // Setter methods

    public void setName(String name) {
        this.name = name;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }
}
