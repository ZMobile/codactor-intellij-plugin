package com.translator.service.codactor.ui.tool;

import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import com.translator.CodactorInjector;
import com.translator.service.codactor.inquiry.InquiryViewerMapService;
import com.translator.view.codactor.console.CodactorConsole;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.viewer.inquiry.InquiryListViewer;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;
import com.translator.view.codactor.viewer.modification.ProvisionalModificationViewer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodactorToolWindowServiceImpl implements CodactorToolWindowService {
    private Project project;
    private ToolWindowService toolWindowService;
    private String modificationQueueViewerToolWindowId;
    private String inquiryViewerToolWindowId;
    private String consoleToolWindowId;
    private CodactorConsole console;
    private ModificationQueueViewer modificationQueueViewer;
    private ProvisionalModificationViewer provisionalModificationViewer;
    private InquiryListViewer inquiryListViewer;
    private HistoricalModificationListViewer historicalModificationListViewer;
    private List<InquiryViewer> inquiryViewerList;
    private Map<String, Content> inquiryViewerContentMap;

    @Inject
    public CodactorToolWindowServiceImpl(Project project,
                                         ModificationQueueViewer modificationQueueViewer,
                                         ProvisionalModificationViewer provisionalModificationViewer,
                                         InquiryListViewer inquiryListViewer,
                                         HistoricalModificationListViewer historicalModificationListViewer,
                                         CodactorConsole codactorConsole,
                                         ToolWindowService toolWindowService) {
        this.project = project;
        this.modificationQueueViewer = modificationQueueViewer;
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.inquiryListViewer = inquiryListViewer;
        this.historicalModificationListViewer = historicalModificationListViewer;
        this.console = codactorConsole;
        this.toolWindowService = toolWindowService;
        this.inquiryViewerList = new ArrayList<>();
        this.modificationQueueViewerToolWindowId = "Modifications";
        this.inquiryViewerToolWindowId = "Inquiries";
        this.consoleToolWindowId = "Codactor";
    }

    @Override
    public void openCodactorConsoleToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(consoleToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        if (console == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.console = injector.getInstance(CodactorConsole.class);
        }
        Content content = contentFactory.createContent(console, "Console", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        toolWindowService.openToolWindow(consoleToolWindowId);
    }

    public void openModificationQueueViewerToolWindow() {
        ToolWindow toolWindow = toolWindowService.getToolWindow(modificationQueueViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        if (modificationQueueViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        }
        Content content = contentFactory.createContent(modificationQueueViewer, "Queue", false);
        toolWindow.getContentManager().addContentManagerListener(new ContentManagerListener() {
            @Override
            public void contentRemoved(@NotNull ContentManagerEvent event) {
                if (event.getContent() == content) {
                    toolWindow.getContentManager().removeContent(content, true);
                }
            }
        });
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

    public void createInquiryViewerToolWindow(InquiryViewer inquiryViewer) {
        inquiryViewerList.add(inquiryViewer);
        openInquiryViewerToolWindow(inquiryViewer);
    }


    private void openInquiryViewerToolWindow(InquiryViewer inquiryViewer) {
        ToolWindow toolWindow = toolWindowService.getToolWindow(inquiryViewerToolWindowId);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(inquiryViewer, "Inquiry", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
        inquiryViewer.getInquiryChatListViewer().componentResized();
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

