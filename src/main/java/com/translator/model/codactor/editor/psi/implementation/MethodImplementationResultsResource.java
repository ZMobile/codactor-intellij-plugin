package com.translator.model.codactor.editor.psi.implementation;

import java.util.ArrayList;
import java.util.List;

public class MethodImplementationResultsResource {
    private String method;
    private List<ImplementationResult> implementations;

    public MethodImplementationResultsResource(String method) {
        this.method = method;
        this.implementations = new ArrayList<>();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<ImplementationResult> getImplementations() {
        return implementations;
    }

    public void setImplementations(List<ImplementationResult> implementations) {
        this.implementations = implementations;
    }
}
