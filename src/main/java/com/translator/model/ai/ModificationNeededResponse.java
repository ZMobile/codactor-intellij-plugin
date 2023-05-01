package com.translator.model.ai;

public class ModificationNeededResponse {
    boolean modificationNeeded;
    String reasoning;

    public boolean isModificationNeeded() {
        return modificationNeeded;
    }

    public String getReasoning() {
        return reasoning;
    }
}
