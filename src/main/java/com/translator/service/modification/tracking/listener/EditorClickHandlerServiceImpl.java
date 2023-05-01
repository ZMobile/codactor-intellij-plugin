package com.translator.service.modification.tracking.listener;

import com.google.inject.Inject;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.service.code.EditorExtractorService;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.ui.tool.CodactorToolWindowService;

import java.util.HashMap;
import java.util.Map;

public class EditorClickHandlerServiceImpl implements EditorClickHandlerService {
    private final Project project;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final CodactorToolWindowService codactorToolWindowService;
    private final EditorExtractorService editorExtractorService;
    private final Map<String, EditorClickHandler> editorClickHandlerMap;

    @Inject
    public EditorClickHandlerServiceImpl(Project project,
                                         FileModificationTrackerService fileModificationTrackerService,
                                         CodactorToolWindowService codactorToolWindowService,
                                         EditorExtractorService editorExtractorService) {
        this.project = project;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.editorExtractorService = editorExtractorService;
        this.editorClickHandlerMap = new HashMap<>();
    }

    @Override
    public void addEditorClickHandler(String filePath) {
        if (editorClickHandlerMap.containsKey(filePath)) {
            return;
        }
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (virtualFile == null) {
            return;
        }

        Editor editor = editorExtractorService.getEditorForVirtualFile(project, virtualFile);
        if (editor == null) {
            return;
        }
        addEditorClickHandler(filePath, editor);
    }

    public void addEditorClickHandler(String filePath, Editor editor) {
        if (editorClickHandlerMap.containsKey(filePath)) {
            return;
        }
        EditorClickHandler editorClickHandler = new EditorClickHandler(fileModificationTrackerService, codactorToolWindowService, filePath);
        editor.addEditorMouseListener(editorClickHandler);
        editorClickHandlerMap.put(filePath, editorClickHandler);
    }

    public void removeEditorClickHandler(String filePath) {
        if (editorClickHandlerMap.containsKey(filePath)) {
            EditorClickHandler editorClickHandler = editorClickHandlerMap.get(filePath);
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile == null) {
                return;
            }

            Editor editor = editorExtractorService.getEditorForVirtualFile(project, virtualFile);
            editorClickHandlerMap.remove(filePath);
            if (editor == null || editorClickHandler == null) {
                return;
            }
            try {
                editor.removeEditorMouseListener(editorClickHandler);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
}
