package com.translator.service.ui.tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.translator.view.viewer.ModificationQueueViewer;
import com.translator.view.viewer.ProvisionalModificationViewer;
import com.translator.view.viewer.HistoricalModificationListViewer;
import com.translator.view.viewer.InquiryListViewer;
import com.translator.view.viewer.InquiryViewer;

import javax.inject.Inject;

public class CodactorToolWindowServiceImpl implements CodactorToolWindowService {
    private ToolWindowService toolWindowService;
    private String modificationQueueViewerToolWindowId;
    private String inquiryViewerToolWindowId;
    private ModificationQueueViewer modificationQueueViewer;
    private ProvisionalModificationViewer provisionalModificationViewer;
    private InquiryViewer inquiryViewer;
    private InquiryListViewer inquiryListViewer;
    private HistoricalModificationListViewer historicalModificationListViewer;

    @Inject
    public CodactorToolWindowServiceImpl(ModificationQueueViewer modificationQueueViewer,
                                            ProvisionalModificationViewer provisionalModificationViewer,
                                            InquiryViewer inquiryViewer,
                                            InquiryListViewer inquiryListViewer,
                                            HistoricalModificationListViewer historicalModificationListViewer,
                                            ToolWindowService toolWindowService) {
        this.modificationQueueViewer = modificationQueueViewer;
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.inquiryViewer = inquiryViewer;
        this.inquiryListViewer = inquiryListViewer;
        this.historicalModificationListViewer = historicalModificationListViewer;
        this.toolWindowService = toolWindowService;
    }

    public void openModificationQueueViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(modificationQueueViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(modificationQueueViewer, "Modification Queue", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindowService.openToolWindow(modificationQueueViewerToolWindowId);
    }

    public void openProvisionalModificationViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(modificationQueueViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(provisionalModificationViewer, "Provisional Modification", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindowService.openToolWindow(modificationQueueViewerToolWindowId);
    }

    public void closeModificationQueueViewerToolWindow() {
        toolWindowService.closeToolWindow(modificationQueueViewerToolWindowId);
    }


    public void openInquiryViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(inquiryViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(inquiryViewer, "Inquiry", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        inquiryViewer.componentResized();
        toolWindowService.openToolWindow(inquiryViewerToolWindowId);
    }

    public void openInquiryListViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(inquiryViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(inquiryListViewer, "Previous Inquiries", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindowService.openToolWindow(inquiryViewerToolWindowId);
    }

    public void openHistoricalModificationListViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(inquiryViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(historicalModificationListViewer, "Inquiry Builder", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindowService.openToolWindow(inquiryViewerToolWindowId);
    }

    public void closeInquiryViewerToolWindow() {
        toolWindowService.closeToolWindow(inquiryViewerToolWindowId);
    }

    public void setModificationQueueViewerToolWindowId(String modificationQueueViewerToolWindowId) {
        this.modificationQueueViewerToolWindowId = modificationQueueViewerToolWindowId;
    }

    public void setInquiryViewerToolWindowId(String inquiryViewerToolWindowId) {
        this.inquiryViewerToolWindowId = inquiryViewerToolWindowId;
    }
}

