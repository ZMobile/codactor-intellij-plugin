package com.translator.view.codactor.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.ComboBoxCompositeEditor;
import com.intellij.ui.components.JBTextArea;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class DoubleControlAction extends AnAction {
    private final AnAction originalRunAnythingAction;

    public DoubleControlAction(AnAction originalRunAnythingAction) {
        this.originalRunAnythingAction = originalRunAnythingAction;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Check if the currently focused component is a JBTextArea or an Editor
        Component focusedComponent = IdeFocusManager.getGlobalInstance().getFocusedDescendantFor(Objects.requireNonNull(e.getData(PlatformDataKeys.CONTEXT_COMPONENT)));

        if (focusedComponent instanceof JBTextArea || focusedComponent instanceof ComboBoxCompositeEditor.EditorComponent) {
            // Activate your TTS functionality here
        } else {
            // Trigger the original Run Anything action
            originalRunAnythingAction.actionPerformed(e);
        }
    }
}