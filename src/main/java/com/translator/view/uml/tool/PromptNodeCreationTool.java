package com.translator.view.uml.tool;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.service.uml.NodeDialogWindowMapperService;
import com.translator.view.uml.dialog.PromptNodeDialog;
import com.translator.view.uml.factory.dialog.PromptNodeDialogFactory;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.CreationTool;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PromptNodeCreationTool extends CreationTool {
    private final PromptNodeDialogFactory promptNodeDialogFactory;
    private final NodeDialogWindowMapperService nodeDialogTrackerService;

    @Inject
    public PromptNodeCreationTool(@Assisted LabeledRectangleFigure prototype,
                                  PromptNodeDialogFactory promptNodeDialogFactory,
                                  NodeDialogWindowMapperService nodeDialogTrackerService) {
        super(prototype);
        this.promptNodeDialogFactory = promptNodeDialogFactory;
        this.nodeDialogTrackerService = nodeDialogTrackerService;
    }

    @Override
    protected void creationFinished(Figure createdFigure) {
        super.creationFinished(createdFigure);
        assert createdFigure instanceof LabeledRectangleFigure;
        LabeledRectangleFigure labeledRectangleFigure = (LabeledRectangleFigure) createdFigure;

        PromptNodeDialog promptNodeDialog;
        if (!nodeDialogTrackerService.getPromptNodeDialogMap().containsKey(labeledRectangleFigure)) {
            promptNodeDialog = promptNodeDialogFactory.create(labeledRectangleFigure);
            nodeDialogTrackerService.getPromptNodeDialogMap().put(labeledRectangleFigure, promptNodeDialog);
            promptNodeDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    nodeDialogTrackerService.getPromptNodeDialogMap().remove(labeledRectangleFigure);
                }
            });
            nodeDialogTrackerService.getPromptNodeDialogMap().put(labeledRectangleFigure, promptNodeDialog);
        } else {
            promptNodeDialog = nodeDialogTrackerService.getPromptNodeDialogMap().get(labeledRectangleFigure);
        }
        promptNodeDialog.setVisible(true);

        // Your custom code here. This will be called when a figure is created.
        System.out.println("Figure created: " + createdFigure);
    }
}
