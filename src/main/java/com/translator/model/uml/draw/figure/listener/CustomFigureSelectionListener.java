package com.translator.model.uml.draw.figure.listener;

import com.translator.PromptContextBuilder;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.service.context.PromptContextService;
import com.translator.service.context.PromptContextServiceImpl;
import com.translator.service.factory.uml.PromptNodeDialogFactory;
import com.translator.view.factory.PromptContextBuilderFactory;
import com.translator.view.uml.dialog.PromptNodeDialog;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.FigureSelectionListener;
import org.jhotdraw.draw.event.FigureSelectionEvent;

import javax.inject.Inject;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class CustomFigureSelectionListener implements FigureSelectionListener {
   private PromptNodeDialogFactory promptNodeDialogFactory;
    private Map<Figure, PromptNodeDialog> promptNodeDialogMap;

    @Inject
    public CustomFigureSelectionListener(PromptNodeDialogFactory promptNodeDialogFactory) {
        this.promptNodeDialogFactory = promptNodeDialogFactory;
        this.promptNodeDialogMap = new HashMap<>();
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
                        PromptNodeDialog promptNodeDialog;
                        if (!promptNodeDialogMap.containsKey(figure)) {
                            promptNodeDialog = promptNodeDialogFactory.create(null);
                            promptNodeDialogMap.put(figure, promptNodeDialog);
                            promptNodeDialog.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosed(WindowEvent e) {
                                    promptNodeDialogMap.remove(figure);
                                }
                            });
                            promptNodeDialogMap.put(figure, promptNodeDialog);
                        } else {
                            promptNodeDialog = promptNodeDialogMap.get(figure);
                        }
                        promptNodeDialog.setVisible(true);
                    }
                }
            }
        }
    }
}

