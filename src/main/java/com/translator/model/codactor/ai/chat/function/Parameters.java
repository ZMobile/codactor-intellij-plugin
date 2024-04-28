package com.translator.model.codactor.ai.chat.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameters {
    private String type;
    private Map<String, Property> properties;
    private List<String> required;

    public Parameters(String type, Map<String, Property> properties, List<String> required) {
        this.type = type;
        this.properties = properties;
        this.required = required;
    }

    public Parameters(String type) {
        this.type = type;
        this.properties = new HashMap<>();
        this.required = new ArrayList<>();
    }

    // Getter methods
    public String getType() {
        return type;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public List<String> getRequired() {
        return required;
    }

    // Setter methods
    public void setType(String type) {
        this.type = type;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }
}
