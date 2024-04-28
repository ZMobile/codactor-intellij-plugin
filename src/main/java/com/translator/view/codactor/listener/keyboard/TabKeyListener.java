package com.translator.view.codactor.listener.keyboard;

import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.service.codactor.ai.modification.tracking.FileModificationManagementService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TabKeyListener implements KeyListener {
    private String filePath;
    private final FileModificationManagementService fileModificationManagementService;
    private final CodactorToolWindowService codactorToolWindowService;
    private final JBTextArea jBTextArea;

    public TabKeyListener(String filePath,
                          FileModificationManagementService fileModificationManagementService,
                          CodactorToolWindowService codactorToolWindowService,
                          JBTextArea jBTextArea) {
        this.filePath = filePath;
        this.fileModificationManagementService = fileModificationManagementService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.jBTextArea = jBTextArea;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            int caretPosition = jBTextArea.getCaretPosition();
            FileModificationTracker fileModificationTracker = fileModificationManagementService.getActiveModificationFiles().get(filePath);
            if (fileModificationTracker == null) {
                return;
            }
            for (FileModification fileModification : fileModificationTracker.getModifications()) {
                int minCaretPosition = fileModification.getRangeMarker().getStartOffset();
                int maxCaretPosition = fileModification.getRangeMarker().getEndOffset();
                if (caretPosition >= minCaretPosition && caretPosition <= maxCaretPosition) {
                    fileModificationManagementService.implementModificationUpdate(fileModification.getId(), fileModification.getModificationOptions().get(0).getSuggestedCodeEditor().getDocument().getText(), false);
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



