package com.translator.model.codactor.editor.psi;

public class ErrorResult {
    public String description;
    public String offendingSnippet;

    public ErrorResult(String description, String offendingSnippet) {
        this.description = description;
        this.offendingSnippet = offendingSnippet;
    }
}
