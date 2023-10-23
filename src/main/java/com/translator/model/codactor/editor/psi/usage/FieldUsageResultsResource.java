package com.translator.model.codactor.editor.psi.usage;

import java.util.ArrayList;
import java.util.List;

public class FieldUsageResultsResource {
    private String field;
    private List<UsageResult> internalUsages;
    private List<UsageResult> externalUsages;

    public FieldUsageResultsResource(String field) {
        this.field = field;
        this.internalUsages = new ArrayList<>();
        this.externalUsages = new ArrayList<>();
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<UsageResult> getInternalUsages() {
        return internalUsages;
    }

    public void setInternalUsages(List<UsageResult> internalUsages) {
        this.internalUsages = internalUsages;
    }

    public List<UsageResult> getExternalUsages() {
        return externalUsages;
    }

    public void setExternalUsages(List<UsageResult> externalUsages) {
        this.externalUsages = externalUsages;
    }
}
