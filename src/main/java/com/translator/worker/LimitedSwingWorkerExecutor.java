package com.translator.worker;

import javax.swing.*;
import java.util.concurrent.Semaphore;

public class LimitedSwingWorkerExecutor {

    private Semaphore semaphore = new Semaphore(30);

    public void execute(SwingWorker swingWorker) {
        try {
            semaphore.acquire();
            swingWorker.execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        semaphore.release();
    }
}
