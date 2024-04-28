package com.translator.model.codactor.ai.chat.function;

public class GptFunctionCall {
    private String name;
    private String arguments;

    public GptFunctionCall(String name, String arguments) {
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
