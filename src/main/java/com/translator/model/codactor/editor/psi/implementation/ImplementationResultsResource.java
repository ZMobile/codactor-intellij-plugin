package com.translator.model.codactor.editor.psi.implementation;

import java.util.ArrayList;
import java.util.List;

public class ImplementationResultsResource {
    private String filePath;
    private List<MethodImplementationResultsResource> methods;

    public ImplementationResultsResource(String filePath) {
        this.filePath = filePath;
        this.methods = new ArrayList<>();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<MethodImplementationResultsResource> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodImplementationResultsResource> methods) {
        this.methods = methods;
    }
}
