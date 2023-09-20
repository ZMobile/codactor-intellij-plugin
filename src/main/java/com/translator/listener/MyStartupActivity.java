package com.translator.listener;

import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.translator.CodactorInjector;
import com.translator.service.codactor.editor.CodeHighlighterService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.modification.tracking.listener.EditorClickHandlerService;
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

            EditorClickHandlerService editorClickHandlerService = injector.getInstance(EditorClickHandlerService.class);
            CodeHighlighterService codeHighlighterService = injector.getInstance(CodeHighlighterService.class);

            EditorListener.register(fileModificationTrackerService, editorClickHandlerService, codeHighlighterService);
        }
    }
}
