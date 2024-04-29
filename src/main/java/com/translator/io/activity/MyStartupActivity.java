package com.translator.io.activity;

import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.translator.CodactorInjector;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ide.handler.EditorClickHandlerService;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ui.ModificationTypeComboBoxService;
import com.translator.view.codactor.listener.EditorListener;
import com.translator.view.codactor.viewer.inquiry.InquiryListViewer;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;
import org.jetbrains.annotations.NotNull;

public class MyStartupActivity implements StartupActivity {
    private final String RUN_ANYTHING_ACTION_ID = "RunAnything";

    @Override
    public void runActivity(@NotNull Project project) {
        if (project != null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            // Get the action manager instance
            ActionManager actionManager = ActionManager.getInstance();
            // Get the existing Run Anything action
            AnAction runAnythingAction = actionManager.getAction(RUN_ANYTHING_ACTION_ID);
            if (runAnythingAction != null) {
                // Get the keyboard shortcut for the Run Anything action
                KeyboardShortcut keyboardShortcut = actionManager.getKeyboardShortcut(RUN_ANYTHING_ACTION_ID);

                // Unregister the existing Run Anything action
                actionManager.unregisterAction(RUN_ANYTHING_ACTION_ID);
            }

            FileModificationTrackerService fileModificationTrackerService = injector.getInstance(FileModificationTrackerService.class);
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

            EditorListener.register(project, fileModificationTrackerService, editorClickHandlerService, codeHighlighterService, modificationTypeComboBoxService);
        }
    }
}
