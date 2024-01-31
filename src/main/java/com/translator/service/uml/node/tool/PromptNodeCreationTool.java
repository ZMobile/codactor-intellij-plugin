package com.translator.service.uml.node.tool;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;
import com.translator.service.uml.node.NodeDialogWindowMapperService;
import com.translator.view.uml.factory.dialog.PromptNodeDialogFactory;
import com.translator.view.uml.node.dialog.prompt.PromptNodeDialog;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.CreationTool;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PromptNodeCreationTool extends CreationTool {
    private final PromptNodeDialogFactory promptNodeDialogFactory;
    private final NodeDialogWindowMapperService nodeDialogWindowMapperService;
    private final Gson gson;

    @Inject
    public PromptNodeCreationTool(@Assisted LabeledRectangleFigure prototype,
                                  PromptNodeDialogFactory promptNodeDialogFactory,
                                  NodeDialogWindowMapperService nodeDialogWindowMapperService,
                                  Gson gson) {
        super(prototype);
        this.promptNodeDialogFactory = promptNodeDialogFactory;
        this.nodeDialogWindowMapperService = nodeDialogWindowMapperService;
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
                if (!nodeDialogWindowMapperService.getPromptNodeDialogMap().containsKey(labeledRectangleFigure)) {
                    promptNodeDialog = promptNodeDialogFactory.create(labeledRectangleFigure, drawing);
                    nodeDialogWindowMapperService.getPromptNodeDialogMap().put(labeledRectangleFigure, promptNodeDialog);
                    promptNodeDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            nodeDialogWindowMapperService.getPromptNodeDialogMap().remove(labeledRectangleFigure);
                        }
                    });
                    nodeDialogWindowMapperService.getPromptNodeDialogMap().put(labeledRectangleFigure, promptNodeDialog);
                } else {
                    promptNodeDialog = nodeDialogWindowMapperService.getPromptNodeDialogMap().get(labeledRectangleFigure);
                }
                promptNodeDialog.setVisible(true);

                // Your custom code here. This will be called when a figure is created.
                System.out.println("Figure created: " + createdFigure);
            }
        }
    }
}
