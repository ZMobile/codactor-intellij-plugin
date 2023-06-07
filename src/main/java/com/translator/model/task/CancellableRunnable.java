package com.translator.model.task;

import com.intellij.openapi.progress.ProgressIndicator;

public interface CancellableRunnable {
    void run(ProgressIndicator customProgressIndicator);
}
