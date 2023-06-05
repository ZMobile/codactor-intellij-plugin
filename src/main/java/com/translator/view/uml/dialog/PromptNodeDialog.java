package com.translator.view.uml.dialog;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;
import com.translator.view.uml.dialog.panel.PromptConnectionViewer;
import com.translator.view.uml.dialog.panel.PromptViewer;

import javax.swing.*;
import java.awt.*;

public class PromptNodeDialog extends JDialog {
    private PromptConnectionViewer promptConnectionViewer;
    private PromptViewer promptViewer;
    private LabeledRectangleFigure promptNodeFigure;
    private PromptNode promptNode;
    private Gson gson;
    private JButton runButton;

    @Inject
    public PromptNodeDialog(@Assisted LabeledRectangleFigure promptNodeFigure,
                            Gson gson) {
        super();
        this.promptNodeFigure = promptNodeFigure;
        this.promptNode = gson.fromJson(promptNodeFigure.getMetadata(), PromptNode.class);
        this.promptConnectionViewer = new PromptConnectionViewer(promptNode);
        this.promptViewer = new PromptViewer(promptNode);


        setTitle("Prompt Node");
        setSize(400, 500);
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        runButton = new JButton("Run");
        toolBar.add(Box.createHorizontalGlue());  // add this line to move the button to the right
        toolBar.add(runButton);
        add(toolBar, BorderLayout.NORTH);
        panel.add(promptConnectionViewer, BorderLayout.NORTH);
        panel.add(promptViewer, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);

    }

    public PromptConnectionViewer getPromptConnectionViewer() {
        return promptConnectionViewer;
    }

    public PromptViewer getPromptViewer() {
        return promptViewer;
    }
}