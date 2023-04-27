package com.translator.service.ui.tool;

import com.intellij.openapi.wm.ToolWindow;

public interface ToolWindowService {
    ToolWindow getToolWindow(String toolWindowId);

    void openToolWindow(String toolWindowId);

    void closeToolWindow(String toolWindowId);
}
