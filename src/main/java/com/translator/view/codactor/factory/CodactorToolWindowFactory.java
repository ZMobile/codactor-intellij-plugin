package com.translator.view.codactor.factory;

import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.translator.CodactorInjector;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.console.CodactorConsole;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;
import org.jetbrains.annotations.NotNull;

public class CodactorToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        //ConsoleView consoleView = codactorConsole.getConsoleView();
        ModificationQueueViewer modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        FileModificationManagementService fileModificationManagementService = injector.getInstance(FileModificationManagementService.class);
        fileModificationManagementService.setModificationQueueViewer(modificationQueueViewer);
        CodactorToolWindowService codactorToolWindowService = injector.getInstance(CodactorToolWindowService.class);

        // Example of how to print text to the console
        //consoleView.print("Hello, world!\n", ConsoleViewContentType.NORMAL_OUTPUT);

        CodactorConsole codactorConsole = injector.getInstance(CodactorConsole.class);
        codactorToolWindowService.setConsole(codactorConsole);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(codactorConsole, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}