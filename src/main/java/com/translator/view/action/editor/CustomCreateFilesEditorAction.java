package com.translator.view.action.editor;

import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.translator.CodactorInjector;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.console.CodactorConsole;
import org.jetbrains.annotations.NotNull;

public class CustomCreateFilesEditorAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        CodactorConsole codactorConsole = injector.getInstance(CodactorConsole.class);
        CodactorToolWindowService codactorToolWindowService = injector.getInstance(CodactorToolWindowService.class);

        codactorConsole.updateModificationTypeComboBox("Create Files");
        codactorToolWindowService.openCodactorConsoleToolWindow();
    }
}
