package com.translator.service.codactor.runner;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

public class YourMainPluginClass extends AnAction {
    // Implement method
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        /*// Accessing the settings
        CodactorSettings settings = ServiceManager.getService(CodactorSettings.class);
        String someSetting = settings.getSomeSetting();
        // Your code here...*/
    }
}

