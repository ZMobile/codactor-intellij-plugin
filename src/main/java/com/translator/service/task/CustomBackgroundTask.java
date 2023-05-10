package com.translator.service.task;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class CustomBackgroundTask extends Task.Backgroundable {
    private final Runnable task;
    private final Runnable cancelTask;
    private final CustomProgressIndicator customProgressIndicator;


    public CustomBackgroundTask(Project project, String taskName, Runnable task, Runnable cancelTask) {
        super(project, taskName, true);
        this.task = task;
        this.cancelTask = cancelTask;
        this.customProgressIndicator = new CustomProgressIndicator();
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        task.run();
    }

    // Custom cancel method to be called from outside
    public void cancel() {
        customProgressIndicator.cancel("Canceled by user");
        cancelTask.run();
    }

    @Override
    public void onCancel() {
        cancel(); // Delegate the onCancel behavior to the custom cancel method
    }

    public Runnable getTask() {
        return task;
    }
}
