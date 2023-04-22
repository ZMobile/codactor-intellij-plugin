package com.translator.view.factory;

import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.translator.CodactorInjector;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.service.ui.tool.ToolWindowService;
import com.translator.view.viewer.HistoricalModificationListViewer;
import com.translator.view.viewer.InquiryListViewer;
import com.translator.view.viewer.InquiryViewer;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class InquiryToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        InquiryViewer inquiryViewer = injector.getInstance(InquiryViewer.class);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(inquiryViewer, "Inquiry", false);
        toolWindow.getComponent().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                inquiryViewer.componentResized();
            }
        });
        toolWindow.getContentManager().addContent(content);
    }


}
