package com.translator.view.codactor.listener;

import com.google.inject.Injector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.CodactorInjector;
import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.handler.EditorClickHandlerService;
import com.translator.service.codactor.ui.ModificationTypeComboBoxService;
import com.translator.service.codactor.ui.ModificationTypeSelectionListener;

import java.util.HashMap;
import java.util.Map;


public class EditorListener implements EditorFactoryListener {
    private static final Map<Project, EditorListener> activeListeners = new HashMap<>();
    private final Project project;
    private final FileModificationManagementService fileModificationManagementService;
    private final EditorClickHandlerService editorClickHandlerService;
    private final CodeHighlighterService codeHighlighterService;
    private final ModificationTypeComboBoxService modificationTypeComboBoxService;


    public EditorListener(Project project,
                          FileModificationManagementService fileModificationManagementService,
                          EditorClickHandlerService editorClickHandlerService,
                          CodeHighlighterService codeHighlighterService,
                          ModificationTypeComboBoxService modificationTypeComboBoxService) {
        this.project = project;
        this.fileModificationManagementService = fileModificationManagementService;
        this.editorClickHandlerService = editorClickHandlerService;
        this.codeHighlighterService = codeHighlighterService;
        this.modificationTypeComboBoxService = modificationTypeComboBoxService;
    }

    @Override
    public void editorCreated(EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        if (editor.getProject() != project) {
            if (!activeListeners.containsKey(editor.getProject())) {
                Injector injector = CodactorInjector.getInstance().getInjector(editor.getProject());
                if (injector != null) {
                    FileModificationManagementService fileModificationManagementService = injector.getInstance(FileModificationManagementService.class);
                    EditorClickHandlerService editorClickHandlerService = injector.getInstance(EditorClickHandlerService.class);
                    CodeHighlighterService codeHighlighterService = injector.getInstance(CodeHighlighterService.class);
                    ModificationTypeComboBoxService modificationTypeComboBoxService = injector.getInstance(ModificationTypeComboBoxService.class);

                    EditorListener.register(editor.getProject(), fileModificationManagementService, editorClickHandlerService, codeHighlighterService, modificationTypeComboBoxService);
                }
            }
            return;
        }
        editor.getSelectionModel().addSelectionListener(new ModificationTypeSelectionListener(modificationTypeComboBoxService));
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        if (virtualFile != null) {
            String filePath = virtualFile.getPath();
            boolean isDecompiledFile = filePath.matches(".+\\.jar!/.+");
            if(isDecompiledFile) {
                System.out.println("Adding editor for file path: " + filePath);
                //editorService.addEditor(filePath, editor);
            }
            FileModificationTracker fileModificationTracker = fileModificationManagementService.getModificationTracker(filePath);
            if (fileModificationTracker != null) {
                editorClickHandlerService.addEditorClickHandler(filePath, editor);
                codeHighlighterService.highlightTextArea(fileModificationTracker, editor);
                System.out.println("Editor created for file path: " + filePath);
            }
        } else {
            System.out.println("Editor created, but no associated VirtualFile found.");
        }
        System.out.println("Editor created for file: " + editor.getDocument().toString());
    }

    @Override
    public void editorReleased(EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        if (editor.getProject() != project) {
            if (!activeListeners.containsKey(editor.getProject())) {
                Injector injector = CodactorInjector.getInstance().getInjector(editor.getProject());
                if (injector != null) {
                    FileModificationManagementService fileModificationManagementService = injector.getInstance(FileModificationManagementService.class);
                    EditorClickHandlerService editorClickHandlerService = injector.getInstance(EditorClickHandlerService.class);
                    CodeHighlighterService codeHighlighterService = injector.getInstance(CodeHighlighterService.class);
                    ModificationTypeComboBoxService modificationTypeComboBoxService = injector.getInstance(ModificationTypeComboBoxService.class);

                    EditorListener.register(editor.getProject(), fileModificationManagementService, editorClickHandlerService, codeHighlighterService, modificationTypeComboBoxService);
                }
            }
            return;
        }
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        if (virtualFile != null) {
            String filePath = virtualFile.getPath();
            boolean isDecompiledFile = filePath.matches(".+\\.jar!/.+");
            if(isDecompiledFile) {
                //editorService.removeEditor(filePath);
            }
            FileModificationTracker fileModificationTracker = fileModificationManagementService.getModificationTracker(filePath);
            if (fileModificationTracker != null) {
                editorClickHandlerService.removeEditorClickHandler(filePath);
            }
        }
        System.out.println("Editor released for file: " + editor.getDocument());
    }

     public static void register(Project project,
                                 FileModificationManagementService fileModificationManagementService,
                                 EditorClickHandlerService editorClickHandlerService,
                                 CodeHighlighterService codeHighlighterService,
                                 ModificationTypeComboBoxService modificationTypeComboBoxService) {
        if (activeListeners.containsKey(project)) {
            System.out.println("Already exists. returning");
            return;
        }
        System.out.println("Editor registered.");
        EditorListener editorListener = new EditorListener(project, fileModificationManagementService, editorClickHandlerService, codeHighlighterService, modificationTypeComboBoxService);
        modificationTypeComboBoxService.addSelectionListenersToAllOpenEditors();
        activeListeners.put(project, editorListener);
        EditorFactory.getInstance().addEditorFactoryListener(editorListener, Disposer.newDisposable());
     }
}
