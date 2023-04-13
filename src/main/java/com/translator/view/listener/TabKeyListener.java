package com.translator.view.listener;

import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationTracker;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.viewer.ProvisionalModificationViewer;
import com.intellij.ui.components.JBTextArea;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TabKeyListener implements KeyListener {
    private String filePath;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final CodactorToolWindowService codactorToolWindowService;
    private final JBTextArea jBTextArea;

    public TabKeyListener(String filePath,
                          FileModificationTrackerService fileModificationTrackerService,
                          CodactorToolWindowService codactorToolWindowService,
                          JBTextArea jBTextArea) {
        this.filePath = filePath;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.jBTextArea = jBTextArea;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            int caretPosition = jBTextArea.getCaretPosition();
            FileModificationTracker fileModificationTracker = fileModificationTrackerService.getActiveModificationFiles().get(filePath);
            if (fileModificationTracker == null) {
                return;
            }
            for (FileModification fileModification : fileModificationTracker.getModifications()) {
                int minCaretPosition = fileModification.getStartIndex();
                int maxCaretPosition = fileModification.getEndIndex();
                if (caretPosition >= minCaretPosition && caretPosition <= maxCaretPosition) {
                    fileModificationTrackerService.getUneditableSegmentListenerService().removeUneditableFileModificationSegmentListener(fileModification.getId());
                    fileModificationTrackerService.getDocumentListenerService().removeDocumentListener(filePath);
                    fileModificationTrackerService.implementModificationUpdate(fileModification.getId(), fileModification.getModificationOptions().get(0).getSuggestedCode().trim());
                    //if (codactorToolWindowService.getRightComponent() instanceof ProvisionalModificationViewer) {
                        //codactorToolWindowService.closeModificationQueueViewerToolWindow();
                    //}
                    fileModificationTrackerService.getDocumentListenerService().insertDocumentListener(filePath);
                    break;
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // This method is called when a key is typed (pressed and released)
        // We don't need to check for the tab button in this method
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // This method is called when a key is released
        // We don't need to check for the tab button in this method
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}



