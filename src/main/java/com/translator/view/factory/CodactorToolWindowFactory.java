package com.translator.view.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.translator.view.console.CodactorConsole;
import org.jetbrains.annotations.NotNull;

public class CodactorToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CodactorConsole codactorConsole = new CodactorConsole();
        //ConsoleView consoleView = codactorConsole.getConsoleView();

        // Example of how to print text to the console
        //consoleView.print("Hello, world!\n", ConsoleViewContentType.NORMAL_OUTPUT);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(codactorConsole, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}