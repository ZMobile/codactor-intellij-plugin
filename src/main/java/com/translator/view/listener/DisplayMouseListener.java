package com.translator.view.listener;

import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationTracker;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.viewer.ModificationQueueViewer;
import com.translator.view.viewer.ProvisionalModificationViewer;
import com.intellij.ui.components.JBTextArea;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class DisplayMouseListener extends MouseAdapter {
    private String filePath;
    private Map<String, JBTextArea> displayMap;
    private FileModificationTrackerService fileModificationTrackerService;
    private ProvisionalModificationViewer provisionalModificationViewer;
    private ModificationQueueViewer modificationQueueViewer;
    private CodactorToolWindowService codactorToolWindowService;
    public DisplayMouseListener(String filePath,
                                Map<String, JBTextArea> displayMap,
                                FileModificationTrackerService fileModificationTrackerService,
                                ProvisionalModificationViewer provisionalModificationViewer,
                                ModificationQueueViewer modificationQueueViewer,
                                CodactorToolWindowService codactorToolWindowService) {
        this.filePath = filePath;
        this.displayMap = displayMap;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.modificationQueueViewer = modificationQueueViewer;
        this.codactorToolWindowService = codactorToolWindowService;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        JBTextArea display = displayMap.get(filePath);
        int offset = display.viewToModel(p);
        FileModificationTracker fileModificationTracker = fileModificationTrackerService.getActiveModificationFiles().get(filePath);
        if (fileModificationTracker != null) {
            for (FileModification fileModification : fileModificationTracker.getModifications()) {
                if (offset >= fileModification.getRangeMarker().getStartOffset() && offset <= fileModification.getRangeMarker().getEndOffset()) {
                    if (fileModification.isDone()) {
                        provisionalModificationViewer.updateModificationList(fileModification);
                        codactorToolWindowService.openProvisionalModificationViewerToolWindow();
                    } else {
                        //codactorToolWindowService.openModificationQueueViewerToolWindow();
                    }
                    break;
                }
            }
        }
    }
}
