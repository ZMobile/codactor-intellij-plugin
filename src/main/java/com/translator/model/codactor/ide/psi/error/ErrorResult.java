package com.translator.model.codactor.ide.psi.error;

public class ErrorResult {
    public String description;
    public String offendingSnippet;

    public ErrorResult(String description, String offendingSnippet) {
        this.description = description;
        this.offendingSnippet = offendingSnippet;
    }
}
