package com.translator.listener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.translator.CodactorInjector;
import com.translator.dao.CodeTranslatorDaoConfig;
import com.translator.service.CodeTranslatorServiceConfig;
import com.translator.service.constructor.CodeFileGeneratorService;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.ui.ModificationQueueListButtonService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.service.ui.tool.ToolWindowService;
import com.translator.view.CodeTranslatorViewConfig;
import com.translator.view.action.DoubleControlAction;
import com.translator.view.viewer.HistoricalModificationListViewer;
import com.translator.view.viewer.InquiryListViewer;
import com.translator.view.viewer.InquiryViewer;
import com.translator.view.viewer.ModificationQueueViewer;
import org.jetbrains.annotations.NotNull;

import static com.intellij.ide.actions.runAnything.RunAnythingAction.RUN_ANYTHING_ACTION_ID;

public class ProjectManagerListenerImpl implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        Injector injector = CodactorInjector.getInstance().getInjector(project);

        // Get the action manager instance
        ActionManager actionManager = ActionManager.getInstance();

        // Get the existing Run Anything action
        AnAction runAnythingAction = actionManager.getAction(RUN_ANYTHING_ACTION_ID);

        if (runAnythingAction != null) {
            // Get the keyboard shortcut for the Run Anything action
            KeyboardShortcut keyboardShortcut = actionManager.getKeyboardShortcut(RUN_ANYTHING_ACTION_ID);

            DoubleControlAction doubleControlAction = new DoubleControlAction(runAnythingAction);
            actionManager.registerAction(RUN_ANYTHING_ACTION_ID, doubleControlAction);
            doubleControlAction.registerCustomShortcutSet(new CustomShortcutSet(keyboardShortcut), null);
        }
        // Your code to execute when a project is opened
        FileModificationTrackerService fileModificationTrackerService = injector.getInstance(FileModificationTrackerService.class);
        ModificationQueueListButtonService modificationQueueListButtonService = injector.getInstance(ModificationQueueListButtonService.class);

        fileModificationTrackerService.setModificationQueueListButtonService(modificationQueueListButtonService);
        //promptContextService.setStatusLabel(jLabel2);

    }

    @Override
    public void projectClosed(@NotNull Project project) {
        // Your code to execute when a project is closed
    }

    // Implement other methods as needed
}