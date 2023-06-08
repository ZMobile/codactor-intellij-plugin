package com.translator.view.codactor.listener;

import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationTracker;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;

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
                int minCaretPosition = fileModification.getRangeMarker().getStartOffset();
                int maxCaretPosition = fileModification.getRangeMarker().getEndOffset();
                if (caretPosition >= minCaretPosition && caretPosition <= maxCaretPosition) {
                    fileModificationTrackerService.implementModificationUpdate(fileModification.getId(), fileModification.getModificationOptions().get(0).getSuggestedCode().getDocument().getText().trim(), false);
                    //if (codactorToolWindowService.getRightComponent() instanceof ProvisionalModificationViewer) {
                        //codactorToolWindowService.closeModificationQueueViewerToolWindow();
                    //}

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



