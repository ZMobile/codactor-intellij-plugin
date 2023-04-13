package com.translator.worker;

import javax.swing.*;

public class LimitedSwingWorker extends SwingWorker<Void, Void> {

    private LimitedSwingWorkerExecutor executor;

    public LimitedSwingWorker(LimitedSwingWorkerExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // Your background task here
        return null;
    }

    @Override
    protected void done() {
        if (executor != null) {
            executor.release();
        }
    }
}
