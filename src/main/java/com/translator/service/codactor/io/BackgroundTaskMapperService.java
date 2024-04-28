package com.translator.service.codactor.io;

import com.translator.model.codactor.io.CustomBackgroundTask;

public interface BackgroundTaskMapperService {
    void addTask(String id, CustomBackgroundTask customBackgroundTask);

    void cancelTask(String id);

    void removeTask(String id);

    boolean hasTask(String id);

    CustomBackgroundTask getTask(String id);
}
