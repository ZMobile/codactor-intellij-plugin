package com.translator.model.codactor.task;

import com.intellij.openapi.progress.ProgressIndicator;

public interface CancellableRunnable {
    void run(ProgressIndicator customProgressIndicator);
}
