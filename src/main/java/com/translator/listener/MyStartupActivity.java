package com.translator.listener;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.translator.view.action.DoubleControlAction;
import org.jetbrains.annotations.NotNull;

public class MyStartupActivity implements StartupActivity {
    private final String RUN_ANYTHING_ACTION_ID = "RunAnything";

    @Override
    public void runActivity(@NotNull Project project) {
        // Get the action manager instance
        ActionManager actionManager = ActionManager.getInstance();
        // Get the existing Run Anything action
        AnAction runAnythingAction = actionManager.getAction(RUN_ANYTHING_ACTION_ID);
        if (runAnythingAction != null) {
            // Get the keyboard shortcut for the Run Anything action
            KeyboardShortcut keyboardShortcut = actionManager.getKeyboardShortcut(RUN_ANYTHING_ACTION_ID);

            // Unregister the existing Run Anything action
            actionManager.unregisterAction(RUN_ANYTHING_ACTION_ID);

            /*// Register your custom action with the same keyboard shortcut
            DoubleControlAction doubleControlAction = new DoubleControlAction(runAnythingAction);
            actionManager.registerAction(RUN_ANYTHING_ACTION_ID, doubleControlAction);
            doubleControlAction.registerCustomShortcutSet(new CustomShortcutSet(keyboardShortcut), null);*/
        }
    }
}
