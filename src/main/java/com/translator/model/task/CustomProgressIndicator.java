package com.translator.model.task;

import com.intellij.openapi.progress.util.AbstractProgressIndicatorBase;

public class CustomProgressIndicator extends AbstractProgressIndicatorBase {
    public void cancel(String message) {
        if (!isCanceled()) {
            super.cancel();
            setText(message);
        }
    }
}


