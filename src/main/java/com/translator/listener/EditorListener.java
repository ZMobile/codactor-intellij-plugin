package com.translator.listener;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.modification.FileModificationTracker;
import com.translator.service.codactor.editor.CodeHighlighterService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.modification.tracking.listener.EditorClickHandlerService;

public class EditorListener implements EditorFactoryListener {
    private final FileModificationTrackerService fileModificationTrackerService;
    private final EditorClickHandlerService editorClickHandlerService;
    private final CodeHighlighterService codeHighlighterService;

    public EditorListener(FileModificationTrackerService fileModificationTrackerService,
                          EditorClickHandlerService editorClickHandlerService,
                          CodeHighlighterService codeHighlighterService) {
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.editorClickHandlerService = editorClickHandlerService;
        this.codeHighlighterService = codeHighlighterService;
    }

    @Override
    public void editorCreated(EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        if (virtualFile != null) {
            String filePath = virtualFile.getPath();
            FileModificationTracker fileModificationTracker = fileModificationTrackerService.getModificationTracker(filePath);
            if (fileModificationTracker != null) {
                editorClickHandlerService.addEditorClickHandler(filePath, editor);
                codeHighlighterService.highlightTextArea(fileModificationTracker, editor);
                System.out.println("Editor created for file path: " + filePath);
            }
        } else {
            System.out.println("Editor created, but no associated VirtualFile found.");
        }
        // Your code to run for every editor created.
        System.out.println("Editor created for file: " + editor.getDocument().toString());
    }

    @Override
    public void editorReleased(EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        if (virtualFile != null) {
            String filePath = virtualFile.getPath();
            FileModificationTracker fileModificationTracker = fileModificationTrackerService.getModificationTracker(filePath);
            if (fileModificationTracker != null) {
                editorClickHandlerService.removeEditorClickHandler(filePath);
            }
        }
        // Your code to run for every editor released.
        System.out.println("Editor released for file: " + editor.getDocument());
    }

    public static void register(FileModificationTrackerService fileModificationTrackerService,
                                EditorClickHandlerService editorClickHandlerService,
                                CodeHighlighterService codeHighlighterService) {
        EditorFactory.getInstance().addEditorFactoryListener(new EditorListener(fileModificationTrackerService, editorClickHandlerService, codeHighlighterService), Disposer.newDisposable());
    }
}
