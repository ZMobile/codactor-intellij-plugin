package com.translator.service.codactor.terminal;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.execution.impl.ConsoleViewImpl;

import java.util.concurrent.atomic.AtomicReference;

public class TerminalViewerServiceImpl {

    /*// constructor
    public TerminalViewerServiceImpl() {
        // initialize your service here if needed
    }

    /**
      * This method retrieves all text in the terminal
      * @return all text in the terminal as a String
      */
    public String viewFullTerminal() {
        final ConsoleViewImpl console = ServiceManager.getService(ConsoleViewImpl.class);
        AtomicReference<String> fullText;

        ApplicationManager.getApplication().runReadAction(() -> {
        //fullText.set(console.getText());
        });

        return null;// fullText.get();
    }

    /**
      * This method retrieves the selected text in the terminal
      * @return the selected text in terminal as a String

    public String viewSelectedTextInTerminal() {
        final ConsoleViewImpl console = ServiceManager.getService(ConsoleViewImpl.class);
        final String selectedText;

        ApplicationManager.getApplication().runReadAction(() -> {
        selectedText = console.getSelectedText();
        });

        return selectedText;
    }*/
}

