package com.translator.model.codactor.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class BooleanWaiter {

    private final Map<String, Boolean> booleanMap;
    private final CountDownLatch latch;

    public BooleanWaiter(Map<String, Boolean> initialMap) {
        booleanMap = new HashMap<>(initialMap);
        latch = new CountDownLatch(booleanMap.size());
    }

    public void setTrue(String key) {
        synchronized (booleanMap) {
            if (booleanMap.containsKey(key) && !booleanMap.get(key)) {
                booleanMap.put(key, true);
                latch.countDown();
            }
        }
    }

    public void waitForAllTrue() throws InterruptedException {
        latch.await();
    }
}
