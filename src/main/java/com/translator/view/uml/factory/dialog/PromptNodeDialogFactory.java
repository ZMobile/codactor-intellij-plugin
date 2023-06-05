package com.translator.view.uml.factory.dialog;

import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.view.uml.dialog.PromptNodeDialog;

import javax.swing.*;

public interface PromptNodeDialogFactory {
    PromptNodeDialog create(LabeledRectangleFigure labeledRectangleFigure);
}
