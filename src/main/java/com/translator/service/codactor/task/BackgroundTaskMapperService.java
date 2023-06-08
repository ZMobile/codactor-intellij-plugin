package com.translator.service.codactor.task;

import com.translator.model.codactor.task.CustomBackgroundTask;

public interface BackgroundTaskMapperService {
    void addTask(String id, CustomBackgroundTask customBackgroundTask);

    void cancelTask(String id);

    void removeTask(String id);

    boolean hasTask(String id);

    CustomBackgroundTask getTask(String id);
}
