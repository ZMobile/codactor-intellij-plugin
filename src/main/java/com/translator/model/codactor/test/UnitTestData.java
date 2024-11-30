package com.translator.model.codactor.test;

public class UnitTestData {
    private String name;
    private String description;
    private String code;

    public UnitTestData(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public UnitTestData(String name, String description, String code) {
        this.name = name;
        this.description = description;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String toString() {
        return "UnitTestData{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
