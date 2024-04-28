package com.translator.service.codactor.ide.handler;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.viewer.modification.ProvisionalModificationViewer;

import java.awt.event.MouseEvent;

public class EditorClickHandler implements EditorMouseListener {
    private FileModificationManagementService fileModificationManagementService;
    private CodactorToolWindowService codactorToolWindowService;
    private FileModificationTracker fileModificationTracker;

    public EditorClickHandler(FileModificationManagementService fileModificationManagementService,
                              CodactorToolWindowService codactorToolWindowService,
                              FileModificationTracker fileModificationTracker) {
        this.fileModificationManagementService = fileModificationManagementService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.fileModificationTracker = fileModificationTracker;
    }

    @Override
    public void mouseClicked(EditorMouseEvent e) {
        // Get the Editor instance
        Editor editor = e.getEditor();

        // Get the MouseEvent instance
        MouseEvent mouseEvent = e.getMouseEvent();

        // Get the position of the click in the document
        int offset = editor.logicalPositionToOffset(editor.xyToLogicalPosition(mouseEvent.getPoint()));
        for (FileModification fileModification : fileModificationTracker.getModifications()) {
            if (offset >= fileModification.getRangeMarker().getStartOffset() && offset <= fileModification.getRangeMarker().getEndOffset()) {
                if (fileModification.isDone()) {
                    ProvisionalModificationViewer provisionalModificationViewer = codactorToolWindowService.getProvisionalModificationViewer();
                    provisionalModificationViewer.updateModificationList(fileModification);
                    codactorToolWindowService.openProvisionalModificationViewerToolWindow();
                } else {
                    codactorToolWindowService.openModificationQueueViewerToolWindow();
                }
            }
        }
        // Do something with the offset, e.g., print it to the console
    }

    @Override
    public void mousePressed(EditorMouseEvent e) {
    }

    @Override
    public void mouseReleased(EditorMouseEvent e) {
    }

    @Override
    public void mouseEntered(EditorMouseEvent e) {
    }

    @Override
    public void mouseExited(EditorMouseEvent e) {
    }
}