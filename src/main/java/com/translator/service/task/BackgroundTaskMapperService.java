package com.translator.service.task;

import com.intellij.openapi.progress.Task;

public interface BackgroundTaskMapperService {
    void addTask(String id, CustomBackgroundTask customBackgroundTask);

    void cancelTask(String id);

    void removeTask(String id);

    boolean hasTask(String id);
}
