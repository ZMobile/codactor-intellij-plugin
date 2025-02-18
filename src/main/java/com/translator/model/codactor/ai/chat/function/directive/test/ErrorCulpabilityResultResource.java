package com.translator.model.codactor.ai.chat.function.directive.test;

import com.translator.model.codactor.ai.chat.Inquiry;

public class ErrorCulpabilityResultResource {
    private Inquiry inquiry;
    private boolean unitTestCulpable;

    public ErrorCulpabilityResultResource(Inquiry inquiry, boolean unitTestCulpable) {
        this.inquiry = inquiry;
        this.unitTestCulpable = unitTestCulpable;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public boolean isUnitTestCulpable() {
        return unitTestCulpable;
    }

    public void setUnitTestCulpable(boolean unitTestCulpable) {
        this.unitTestCulpable = unitTestCulpable;
    }
}
