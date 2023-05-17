package com.translator.view.uml.dialog;

import com.google.inject.Inject;
import com.translator.view.uml.dialog.panel.PromptConnectionViewer;
import com.translator.view.uml.dialog.panel.PromptViewer;

import javax.swing.*;


public class PromptNodeDialog extends JDialog {
    private PromptViewer promptViewer;

    @Inject
    public PromptNodeDialog() {
        super();
        this.promptViewer = new PromptViewer();
        setTitle("Prompt Node");
        setSize(200, 100);
        setLocationRelativeTo(null);
        // Add a simple label to the dialog
        add(new PromptConnectionViewer());
        add(new JLabel("This is a custom dialog!"));
        add(promptViewer);
    }
}