package com.translator.service.ui.tool;

import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.translator.CodactorInjector;
import com.translator.view.console.CodactorConsole;
import com.translator.view.viewer.*;

import javax.inject.Inject;

public class CodactorToolWindowServiceImpl implements CodactorToolWindowService {
    private Project project;
    private ToolWindowService toolWindowService;
    private String modificationQueueViewerToolWindowId;
    private String inquiryViewerToolWindowId;
    private CodactorConsole console;
    private ModificationQueueViewer modificationQueueViewer;
    private ProvisionalModificationViewer provisionalModificationViewer;
    private InquiryViewer inquiryViewer;
    private InquiryListViewer inquiryListViewer;
    private HistoricalModificationListViewer historicalModificationListViewer;

    @Inject
    public CodactorToolWindowServiceImpl(Project project,
                                         ModificationQueueViewer modificationQueueViewer,
                                            ProvisionalModificationViewer provisionalModificationViewer,
                                            InquiryViewer inquiryViewer,
                                            InquiryListViewer inquiryListViewer,
                                            HistoricalModificationListViewer historicalModificationListViewer,
                                            ToolWindowService toolWindowService) {
        this.project = project;
        this.modificationQueueViewer = modificationQueueViewer;
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.inquiryViewer = inquiryViewer;
        this.inquiryListViewer = inquiryListViewer;
        this.historicalModificationListViewer = historicalModificationListViewer;
        this.toolWindowService = toolWindowService;
        this.modificationQueueViewerToolWindowId = "Modifications";
        this.inquiryViewerToolWindowId = "Inquiries";
    }

    public void openModificationQueueViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(modificationQueueViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        if (modificationQueueViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        }
        Content content = contentFactory.createContent(modificationQueueViewer, "Queue", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindowService.openToolWindow(modificationQueueViewerToolWindowId);
    }

    public void openProvisionalModificationViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(modificationQueueViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        if (provisionalModificationViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.provisionalModificationViewer = injector.getInstance(ProvisionalModificationViewer.class);
        }
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
        if (inquiryViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.inquiryViewer = injector.getInstance(InquiryViewer.class);
        }
        Content content = contentFactory.createContent(inquiryViewer, "Inquiry", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        inquiryViewer.componentResized();
        toolWindowService.openToolWindow(inquiryViewerToolWindowId);
    }

    public void openInquiryListViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(inquiryViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        if (inquiryListViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.inquiryListViewer = injector.getInstance(InquiryListViewer.class);
        }
        Content content = contentFactory.createContent(inquiryListViewer, "Previous Inquiries", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindowService.openToolWindow(inquiryViewerToolWindowId);
    }

    public void openHistoricalModificationListViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(inquiryViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        if (historicalModificationListViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.historicalModificationListViewer = injector.getInstance(HistoricalModificationListViewer.class);
        }
        Content content = contentFactory.createContent(historicalModificationListViewer, "Inquiry Builder", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindowService.openToolWindow(inquiryViewerToolWindowId);
    }

    public void closeInquiryViewerToolWindow() {
        toolWindowService.closeToolWindow(inquiryViewerToolWindowId);
    }


    @Override
    public InquiryViewer getInquiryViewer() {
        return inquiryViewer;
    }

    @Override
    public InquiryListViewer getInquiryListViewer() {
        return inquiryListViewer;
    }

    @Override
    public HistoricalModificationListViewer getHistoricalModificationListViewer() {
        return historicalModificationListViewer;
    }

    @Override
    public ModificationQueueViewer getModificationQueueViewer() {
        return modificationQueueViewer;
    }

    public ProvisionalModificationViewer getProvisionalModificationViewer() {
        return provisionalModificationViewer;
    }

    public CodactorConsole getConsole() {
        return console;
    }

    public void setConsole(CodactorConsole console) {
        this.console = console;
    }
}

