package com.translator.view.codactor.factory;

import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.translator.CodactorInjector;
import com.translator.view.codactor.viewer.ModificationQueueViewer;
import org.jetbrains.annotations.NotNull;

public class ModificationQueueToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        createAndAddContent(project, toolWindow);
    }

    private void createAndAddContent(Project project, ToolWindow toolWindow) {
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        ModificationQueueViewer modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        //modificationQueueViewer.setProject(project);
        //CodactorConsole codactorConsole = new CodactorConsole();
        //ConsoleView consoleView = codactorConsole.getConsoleView();

        // Example of how to print text to the console
        //consoleView.print("Hello, world!\n", ConsoleViewContentType.NORMAL_OUTPUT);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(modificationQueueViewer, "Queue", false);

        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);
    }
}