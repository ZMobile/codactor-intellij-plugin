package com.translator.model.codactor.editor.psi.usage;

import java.util.ArrayList;
import java.util.List;

public class MethodUsageResultsResource {
    private String method;
    private List<UsageResult> usages;

    public MethodUsageResultsResource(String method) {
        this.method = method;
        this.usages = new ArrayList<>();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<UsageResult> getUsages() {
        return usages;
    }

    public void setUsages(List<UsageResult> usages) {
        this.usages = usages;
    }
}
