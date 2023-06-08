package com.translator.service.codactor.task;

import com.translator.model.codactor.task.CustomBackgroundTask;

import java.util.HashMap;
import java.util.Map;

public class BackgroundTaskMapperServiceImpl implements BackgroundTaskMapperService {
    private Map<String, CustomBackgroundTask> taskMap = new HashMap<>();

    public void addTask(String id, CustomBackgroundTask task) {
        taskMap.put(id, task);
    }

    public void cancelTask(String id) {
        if (taskMap.containsKey(id)) {
            System.out.println("Canceling task " + id);
            taskMap.get(id).cancel();
            taskMap.remove(id);
        }
    }

    public void removeTask(String id) {
        taskMap.remove(id);
    }

    public boolean hasTask(String id) {
        return taskMap.containsKey(id);
    }

    @Override
    public CustomBackgroundTask getTask(String id) {
        return taskMap.get(id);
    }
}
