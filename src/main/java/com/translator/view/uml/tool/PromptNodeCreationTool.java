package com.translator.view.uml.tool;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;
import com.translator.service.uml.NodeDialogWindowMapperService;
import com.translator.view.uml.dialog.prompt.PromptNodeDialog;
import com.translator.view.uml.factory.dialog.PromptNodeDialogFactory;
import org.apache.tools.ant.types.optional.image.Draw;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.CreationTool;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PromptNodeCreationTool extends CreationTool {
    private final PromptNodeDialogFactory promptNodeDialogFactory;
    private final NodeDialogWindowMapperService nodeDialogTrackerService;
    private final Gson gson;

    @Inject
    public PromptNodeCreationTool(@Assisted LabeledRectangleFigure prototype,
                                  PromptNodeDialogFactory promptNodeDialogFactory,
                                  NodeDialogWindowMapperService nodeDialogTrackerService,
                                  Gson gson) {
        super(prototype);
        this.promptNodeDialogFactory = promptNodeDialogFactory;
        this.nodeDialogTrackerService = nodeDialogTrackerService;
        this.gson = gson;
    }

    @Override
    protected void creationFinished(Figure createdFigure) {
        super.creationFinished(createdFigure);
        assert createdFigure instanceof LabeledRectangleFigure;
        LabeledRectangleFigure labeledRectangleFigure = (LabeledRectangleFigure) createdFigure;
        PromptNode promptNode = new PromptNode();
        labeledRectangleFigure.setMetadata(gson.toJson(promptNode));
        DrawingEditor editor = getEditor();
        if (editor != null) {
            DrawingView view = editor.getActiveView();
            if (view != null) {
                Drawing drawing = view.getDrawing();

                PromptNodeDialog promptNodeDialog;
                if (!nodeDialogTrackerService.getPromptNodeDialogMap().containsKey(labeledRectangleFigure)) {
                    promptNodeDialog = promptNodeDialogFactory.create(labeledRectangleFigure, drawing);
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
    }
}
