package com.translator.service.task;

import com.intellij.openapi.progress.Task;

import java.util.HashMap;
import java.util.Map;

public class BackgroundTaskMapperServiceImpl implements BackgroundTaskMapperService {
    private Map<String, CustomBackgroundTask> taskMap = new HashMap<>();

    public void addTask(String id, CustomBackgroundTask task) {
        taskMap.put(id, task);
    }

    public void cancelTask(String id) {
        taskMap.get(id).cancel();
        taskMap.remove(id);
    }
}
