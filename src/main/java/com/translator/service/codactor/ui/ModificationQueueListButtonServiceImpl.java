package com.translator.service.codactor.ui;

import com.translator.service.codactor.ai.modification.tracking.FileModificationManagementService;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class ModificationQueueListButtonServiceImpl implements ModificationQueueListButtonService {
    private FileModificationManagementService fileModificationManagementService;
    private JToggleButton modificationQueueListButton;
    private Color defaultColor;

    @Inject
    public ModificationQueueListButtonServiceImpl(FileModificationManagementService fileModificationManagementService) {
        //this.fileModificationTrackerService = fileModificationTrackerService;
    }

    @Override
    public JToggleButton getModificationQueueListButton() {
        return modificationQueueListButton;
    }

    @Override
    public void updateModificationQueueListButton() {
        /*if (modificationQueueListButton == null) {
            return;
        }
        if (modificationQueueListButton.isSelected()) {
            modificationQueueListButton.setBackground(getModificationQueueListButtonColor().darker().darker());
        } else {
            modificationQueueListButton.setBackground(getModificationQueueListButtonColor());
        }*/
    }

    @Override
    public Color getModificationQueueListButtonColor() {
        /*List<QueuedFileModificationObjectHolder> queuedFileModificationObjectHolders = fileModificationTrackerService.getQueuedFileModificationObjectHolders();
        if (!queuedFileModificationObjectHolders.isEmpty()) {
            boolean hasUnsolvedModifications = false;
            for (QueuedFileModificationObjectHolder queuedFileModificationObjectHolder : queuedFileModificationObjectHolders) {
                if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION) {
                FileModification fileModification = queuedFileModificationObjectHolder .getFileModification();
                    if (!fileModification.isDone()) {
                        hasUnsolvedModifications = true;
                        break;
                    }
                } else if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.MULTI_FILE_MODIFICATION
                || queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                    hasUnsolvedModifications = true;
                }
            }
            return hasUnsolvedModifications ? Color.decode("#009688") : Color.decode("#228B22");
        } else {*/
            return defaultColor;
        //}*/
    }

    @Override
    public void setModificationQueueListButton(JToggleButton modificationQueueListButton) {
        //this.modificationQueueListButton = modificationQueueListButton;
        //this.defaultColor = modificationQueueListButton.getBackground();
    }
}
