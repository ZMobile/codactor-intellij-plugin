package com.translator.io.action;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import org.jetbrains.annotations.NotNull;

public class DoubleControlTypedActionHandler implements TypedActionHandler {
    private final TypedActionHandler originalHandler;

    public DoubleControlTypedActionHandler(TypedActionHandler originalHandler) {
        this.originalHandler = originalHandler;
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        // If the character typed is not the control key, let the original handler process the action
        if (charTyped != 0x001D) {
            originalHandler.execute(editor, charTyped, dataContext);
        } else {
            // Your custom logic for handling the double-press of the Ctrl key
        }
    }
}
