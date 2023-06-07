package com.translator.model.uml.draw.figure.listener;

import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.service.uml.NodeDialogWindowMapperService;
import com.translator.view.uml.factory.dialog.PromptNodeDialogFactory;
import com.translator.view.uml.dialog.prompt.PromptNodeDialog;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.FigureSelectionListener;
import org.jhotdraw.draw.event.FigureSelectionEvent;

import javax.inject.Inject;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CustomFigureSelectionListener implements FigureSelectionListener {
    private PromptNodeDialogFactory promptNodeDialogFactory;
    private NodeDialogWindowMapperService nodeDialogTrackerService;

    @Inject
    public CustomFigureSelectionListener(PromptNodeDialogFactory promptNodeDialogFactory, NodeDialogWindowMapperService nodeDialogTrackerService) {
        this.promptNodeDialogFactory = promptNodeDialogFactory;
        this.nodeDialogTrackerService = nodeDialogTrackerService;
    }

    @Override
    public void selectionChanged(FigureSelectionEvent evt) {
        // This method is called whenever the selected figures change
        System.out.println("Selection changed: " + evt.getNewSelection());
        if (!evt.getNewSelection().isEmpty()) {
            for (Object object : evt.getNewSelection().toArray()) {
                if (object instanceof Figure) {
                    Figure figure = (Figure) object;
                    if (figure instanceof LabeledRectangleFigure) {
                        LabeledRectangleFigure labeledRectangleFigure = (LabeledRectangleFigure) figure;
                        PromptNodeDialog promptNodeDialog;
                        if (!nodeDialogTrackerService.getPromptNodeDialogMap().containsKey(labeledRectangleFigure)) {
                            promptNodeDialog = promptNodeDialogFactory.create(labeledRectangleFigure);
                            nodeDialogTrackerService.getPromptNodeDialogMap().put(labeledRectangleFigure, promptNodeDialog);
                            promptNodeDialog.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosed(WindowEvent e) {
                                    nodeDialogTrackerService.getPromptNodeDialogMap().remove(figure);
                                }
                            });
                            nodeDialogTrackerService.getPromptNodeDialogMap().put(labeledRectangleFigure, promptNodeDialog);
                        } else {
                            promptNodeDialog = nodeDialogTrackerService.getPromptNodeDialogMap().get(figure);
                        }
                        promptNodeDialog.setVisible(true);
                    }
                }
            }
        }
    }
}

