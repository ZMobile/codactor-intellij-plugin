package com.translator.model.codactor.api.translator.inquiry.function;

import java.util.List;

public class Property {
    private String type;
    private String description;
    private List<String> enum_values;
    private Integer minimum;

    public Property(String type, String description, List<String> enum_values, Integer minimum) {
        this.type = type;
        this.description = description;
        this.enum_values = enum_values;
        this.minimum = minimum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEnum_values() {
        return enum_values;
    }

    public void setEnum_values(List<String> enum_values) {
        this.enum_values = enum_values;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }
}