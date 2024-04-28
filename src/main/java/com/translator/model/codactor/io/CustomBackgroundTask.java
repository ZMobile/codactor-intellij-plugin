package com.translator.model.codactor.io;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class CustomBackgroundTask extends Task.Backgroundable {
    private final CancellableRunnable task;
    private final Runnable cancelTask;
    private ProgressIndicator progressIndicator;


    public CustomBackgroundTask(Project project, String taskName, CancellableRunnable task, Runnable cancelTask) {
        super(project, taskName, true);
        this.task = task;
        this.cancelTask = cancelTask;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        this.progressIndicator = indicator;
        task.run(indicator);
    }

    // Custom cancel method to be called from outside
    public void cancel() {
        progressIndicator.cancel();
        cancelTask.run();
    }

    @Override
    public void onCancel() {
        //cancel();
    }

    public CancellableRunnable getTask() {
        return task;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }
}
