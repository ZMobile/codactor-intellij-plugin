package com.translator.service.ui.tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.translator.service.modification.tracking.FileModificationTrackerService;

import javax.swing.*;
import java.awt.*;

public interface CodactorToolWindowService {
    void openModificationQueueViewerToolWindow();

    void openProvisionalModificationViewerToolWindow();

    void closeModificationQueueViewerToolWindow();

    void openInquiryViewerToolWindow();

    void openInquiryListViewerToolWindow();

    void openHistoricalModificationListViewerToolWindow();

    void closeInquiryViewerToolWindow();

    void setModificationQueueViewerToolWindowId(String modificationQueueViewerToolWindowId);

    void setInquiryViewerToolWindowId(String inquiryViewerToolWindowId);
}
