package com.translator.view.factory;

import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.translator.CodactorInjector;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.view.console.CodactorConsole;
import com.translator.view.viewer.ModificationQueueViewer;
import org.jetbrains.annotations.NotNull;

public class CodactorToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        FileModificationTrackerService fileModificationTrackerService = injector.getInstance(FileModificationTrackerService.class);
        //ConsoleView consoleView = codactorConsole.getConsoleView();

        // Example of how to print text to the console
        //consoleView.print("Hello, world!\n", ConsoleViewContentType.NORMAL_OUTPUT);
        ModificationQueueViewer modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);

        fileModificationTrackerService.setModificationQueueViewer(modificationQueueViewer);

        CodactorConsole codactorConsole = injector.getInstance(CodactorConsole.class);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(codactorConsole, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}