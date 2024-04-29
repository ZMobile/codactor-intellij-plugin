package com.translator.service.codactor.ide.handler;

import com.google.inject.Inject;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ide.editor.EditorExtractorService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;

import java.util.HashMap;
import java.util.Map;

public class EditorClickHandlerServiceImpl implements EditorClickHandlerService {
    private final Project project;
    private final CodactorToolWindowService codactorToolWindowService;
    private final EditorExtractorService editorExtractorService;
    private final Map<String, EditorClickHandler> editorClickHandlerMap;

    @Inject
    public EditorClickHandlerServiceImpl(Project project,
                                         CodactorToolWindowService codactorToolWindowService,
                                         EditorExtractorService editorExtractorService) {
        this.project = project;
        this.codactorToolWindowService = codactorToolWindowService;
        this.editorExtractorService = editorExtractorService;
        this.editorClickHandlerMap = new HashMap<>();
    }

    @Override
    public void addEditorClickHandler(FileModificationTracker fileModificationTracker) {
        if (editorClickHandlerMap.containsKey(fileModificationTracker.getFilePath())) {
            return;
        }
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileModificationTracker.getFilePath());
        if (virtualFile == null) {
            return;
        }

        Editor editor = editorExtractorService.getEditorForVirtualFile(project, virtualFile);
        if (editor == null) {
            return;
        }
        addEditorClickHandler(fileModificationTracker, editor);
    }

    public void addEditorClickHandler(FileModificationTracker fileModificationTracker, Editor editor) {
        if (editorClickHandlerMap.containsKey(fileModificationTracker.getFilePath())) {
            return;
        }
        EditorClickHandler editorClickHandler = new EditorClickHandler(codactorToolWindowService, fileModificationTracker);
        editor.addEditorMouseListener(editorClickHandler);
        editorClickHandlerMap.put(fileModificationTracker.getFilePath(), editorClickHandler);
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
