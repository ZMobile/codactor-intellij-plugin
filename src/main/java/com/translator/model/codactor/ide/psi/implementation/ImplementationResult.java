package com.translator.model.codactor.ide.psi.implementation;

public class ImplementationResult {
    public ImplementationResult(String filePath, String snippet) {
        this.filePath = filePath;
        this.snippet = snippet;
    }

    public String filePath;
    public String snippet;
}
