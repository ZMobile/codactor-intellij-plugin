package com.translator.model.codactor.editor.psi;

public class DeclarationResult {
    public String filePath;
    public String snippet;

    public DeclarationResult(String filePath, String snippet) {
        this.filePath = filePath;
        this.snippet = snippet;
    }
}