package com.translator.model.codactor.ide.psi.usage;

import java.util.ArrayList;
import java.util.List;

public class UsageResultsResource {
    private String filePath;
    private List<MethodUsageResultsResource> methods;
    private List<FieldUsageResultsResource> fields;

    public UsageResultsResource(String filePath) {
        this.filePath = filePath;
        this.methods = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<MethodUsageResultsResource> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodUsageResultsResource> methods) {
        this.methods = methods;
    }

    public List<FieldUsageResultsResource> getFields() {
        return fields = fields;
    }

    public void setFieldUsages(List<FieldUsageResultsResource> fields) {
        this.fields = fields;
    }
}
