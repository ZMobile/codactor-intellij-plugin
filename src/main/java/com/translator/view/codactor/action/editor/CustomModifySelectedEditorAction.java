package com.translator.view.codactor.action.editor;

import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.translator.CodactorInjector;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.console.CodactorConsole;
import org.jetbrains.annotations.NotNull;

public class CustomModifySelectedEditorAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        CodactorConsole codactorConsole = injector.getInstance(CodactorConsole.class);
        CodactorToolWindowService codactorToolWindowService = injector.getInstance(CodactorToolWindowService.class);

        codactorConsole.updateModificationTypeComboBox("Modify Selected");
        codactorToolWindowService.openCodactorConsoleToolWindow();
    }
}
