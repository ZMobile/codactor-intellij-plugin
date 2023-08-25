package com.translator.listener;

import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.translator.CodactorInjector;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.view.codactor.action.DoubleControlAction;
import com.translator.view.codactor.viewer.inquiry.InquiryListViewer;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;
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
        ModificationQueueViewer modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);

        fileModificationTrackerService.setModificationQueueViewer(modificationQueueViewer);
        InquiryViewer inquiryViewer = injector.getInstance(InquiryViewer.class);
        InquiryListViewer inquiryListViewer = injector.getInstance(InquiryListViewer.class);
        inquiryViewer.setInquiryListViewer(inquiryListViewer);
        HistoricalModificationListViewer historicalModificationListViewer = injector.getInstance(HistoricalModificationListViewer.class);
        historicalModificationListViewer.setInquiryListViewer(inquiryListViewer);
        historicalModificationListViewer.setProject(project);
        inquiryListViewer.setHistoricalModificationListViewer(historicalModificationListViewer);
        inquiryViewer.setHistoricalModificationListViewer(historicalModificationListViewer);
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        // Your code to execute when a project is closed
    }

    // Implement other methods as needed
}