package com.translator.view.codactor.listener.mouse;

import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;
import com.translator.view.codactor.viewer.modification.ProvisionalModificationViewer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class DisplayMouseListener extends MouseAdapter {
    private String filePath;
    private Map<String, JBTextArea> displayMap;
    private FileModificationManagementService fileModificationManagementService;
    private ProvisionalModificationViewer provisionalModificationViewer;
    private ModificationQueueViewer modificationQueueViewer;
    private CodactorToolWindowService codactorToolWindowService;
    public DisplayMouseListener(String filePath,
                                Map<String, JBTextArea> displayMap,
                                FileModificationManagementService fileModificationManagementService,
                                ProvisionalModificationViewer provisionalModificationViewer,
                                ModificationQueueViewer modificationQueueViewer,
                                CodactorToolWindowService codactorToolWindowService) {
        this.filePath = filePath;
        this.displayMap = displayMap;
        this.fileModificationManagementService = fileModificationManagementService;
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.modificationQueueViewer = modificationQueueViewer;
        this.codactorToolWindowService = codactorToolWindowService;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        JBTextArea display = displayMap.get(filePath); //Needs to be the editor instead
        int offset = display.viewToModel(p);
        FileModificationTracker fileModificationTracker = fileModificationManagementService.getActiveModificationFiles().get(filePath);
        if (fileModificationTracker != null) {
            for (FileModification fileModification : fileModificationTracker.getModifications()) {
                if (offset >= fileModification.getRangeMarker().getStartOffset() && offset <= fileModification.getRangeMarker().getEndOffset()) {
                    if (fileModification.isDone()) {
                        provisionalModificationViewer.updateModificationList(fileModification);
                        codactorToolWindowService.openProvisionalModificationViewerToolWindow();
                    } else {
                        codactorToolWindowService.openModificationQueueViewerToolWindow();
                    }
                    break;
                }
            }
        }
    }
}
