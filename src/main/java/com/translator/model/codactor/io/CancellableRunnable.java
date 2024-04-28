package com.translator.model.codactor.io;

import com.intellij.openapi.progress.ProgressIndicator;

public interface CancellableRunnable {
    void run(ProgressIndicator customProgressIndicator);
}
