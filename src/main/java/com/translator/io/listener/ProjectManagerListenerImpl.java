package com.translator.io.listener;

import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.translator.CodactorInjector;
import com.translator.service.codactor.ai.modification.tracking.FileModificationManagementService;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.handler.EditorClickHandlerService;
import com.translator.service.codactor.ui.ModificationTypeComboBoxService;
import com.translator.view.codactor.action.DoubleControlAction;
import com.translator.view.codactor.listener.EditorListener;
import com.translator.view.codactor.viewer.inquiry.InquiryListViewer;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;
import org.jetbrains.annotations.NotNull;

import static com.intellij.ide.actions.runAnything.RunAnythingAction.RUN_ANYTHING_ACTION_ID;

public class ProjectManagerListenerImpl implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        if (project != null) {
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
            FileModificationManagementService fileModificationManagementService = injector.getInstance(FileModificationManagementService.class);
            ModificationQueueViewer modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);

            fileModificationManagementService.setModificationQueueViewer(modificationQueueViewer);
            InquiryViewer inquiryViewer = injector.getInstance(InquiryViewer.class);
            InquiryListViewer inquiryListViewer = injector.getInstance(InquiryListViewer.class);
            inquiryViewer.setInquiryListViewer(inquiryListViewer);
            HistoricalModificationListViewer historicalModificationListViewer = injector.getInstance(HistoricalModificationListViewer.class);
            historicalModificationListViewer.setInquiryListViewer(inquiryListViewer);
            historicalModificationListViewer.setProject(project);
            inquiryListViewer.setHistoricalModificationListViewer(historicalModificationListViewer);
            inquiryViewer.setHistoricalModificationListViewer(historicalModificationListViewer);

            EditorClickHandlerService editorClickHandlerService = injector.getInstance(EditorClickHandlerService.class);
            CodeHighlighterService codeHighlighterService = injector.getInstance(CodeHighlighterService.class);
            ModificationTypeComboBoxService modificationTypeComboBoxService = injector.getInstance(ModificationTypeComboBoxService.class);

            EditorListener.register(project, fileModificationManagementService, editorClickHandlerService, codeHighlighterService, modificationTypeComboBoxService);
        }
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        // Your code to execute when a project is closed
    }

    // Implement other methods as needed
}