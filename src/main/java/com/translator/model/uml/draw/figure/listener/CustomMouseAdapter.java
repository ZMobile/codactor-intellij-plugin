package com.translator.model.uml.draw.figure.listener;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.service.uml.node.NodeDialogWindowMapperService;
import com.translator.view.uml.factory.dialog.PromptNodeDialogFactory;
import com.translator.view.uml.node.dialog.prompt.PromptNodeDialog;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.Figure;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CustomMouseAdapter extends MouseAdapter {
    private DefaultDrawingView defaultDrawingView;
    private PromptNodeDialogFactory promptNodeDialogFactory;
    private NodeDialogWindowMapperService nodeDialogWindowMapperService;

    @Inject
    public CustomMouseAdapter(@Assisted DefaultDrawingView defaultDrawingView,
                              PromptNodeDialogFactory promptNodeDialogFactory,
                              NodeDialogWindowMapperService nodeDialogWindowMapperService) {
        this.defaultDrawingView = defaultDrawingView;
        this.promptNodeDialogFactory = promptNodeDialogFactory;
        this.nodeDialogWindowMapperService = nodeDialogWindowMapperService;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        Figure figure = defaultDrawingView.findFigure(e.getPoint());

        if (figure != null && e.getClickCount() == 2) {
            // a figure was found at the clicked location
            System.out.println("Figure clicked: " + figure);
            if (figure instanceof LabeledRectangleFigure) {
                LabeledRectangleFigure labeledRectangleFigure = (LabeledRectangleFigure) figure;
                PromptNodeDialog promptNodeDialog;
                if (!nodeDialogWindowMapperService.getPromptNodeDialogMap().containsKey(labeledRectangleFigure)) {
                    promptNodeDialog = promptNodeDialogFactory.create(labeledRectangleFigure, defaultDrawingView.getDrawing());
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
                promptNodeDialog.getPromptConnectionViewer().updateConnections();
                promptNodeDialog.setVisible(true);
            }
        } else {
            // no figure was found at the clicked location
            System.out.println("No figure at clicked location.");
        }

    }
}
