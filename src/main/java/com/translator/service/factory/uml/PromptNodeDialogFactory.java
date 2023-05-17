package com.translator.service.factory.uml;

import com.translator.view.uml.dialog.PromptNodeDialog;

import javax.swing.*;

public interface PromptNodeDialogFactory {
    PromptNodeDialog create(JFrame parent);
}
