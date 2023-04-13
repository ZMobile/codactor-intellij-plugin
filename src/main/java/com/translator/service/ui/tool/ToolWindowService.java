package com.translator.service.ui.tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

public interface ToolWindowService {
    ToolWindow getToolWindow(String toolWindowId);

    void openToolWindow(String toolWindowId);

    void closeToolWindow(String toolWindowId);
}
