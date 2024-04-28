package com.translator.model.codactor.ide.psi.usage;

public class UsageResult {
    public UsageResult(String filePath, String snippet) {
        this.filePath = filePath;
        this.snippet = snippet;
    }

    public String filePath;
    public String snippet;
}
