package com.translator.service.codactor.ui.tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.inject.Inject;

public class ToolWindowServiceImpl implements ToolWindowService {
    private final Project project;

    @Inject
    public ToolWindowServiceImpl(Project project) {
        this.project = project;
    }

    public ToolWindow getToolWindow(String toolWindowId) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        return toolWindowManager.getToolWindow(toolWindowId);
    }

    public void openToolWindow(String toolWindowId) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowId);
        if (toolWindow != null) {
            toolWindow.show();
        }
    }

    public void closeToolWindow(String toolWindowId) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowId);
        if (toolWindow != null) {
            toolWindow.hide();
        }
    }
}