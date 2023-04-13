package com.translator.view.viewer;

import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.QueuedFileModificationObjectHolder;
import com.translator.model.modification.QueuedModificationObjectType;
import com.translator.service.file.FileOpenerService;
import com.translator.service.file.FileReaderService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.renderer.QueuedModificationObjectRenderer;
import com.translator.view.renderer.SeparatorListCellRenderer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ModificationQueueViewer extends JBPanel<ModificationQueueViewer> {

    private JList<QueuedFileModificationObjectHolder> modificationList;
    private JBScrollPane modificationListScrollPane;
    private ProvisionalModificationViewer provisionalModificationViewer;
    private CodactorToolWindowService codactorToolWindowService;
    private FileReaderService fileReaderService;
    private FileOpenerService fileOpenerService;
    private Project project;

    @Inject
    public ModificationQueueViewer(ProvisionalModificationViewer provisionalModificationViewer,
                                   CodactorToolWindowService codactorToolWindowService,
                                   FileReaderService fileReaderService,
                                   FileOpenerService fileOpenerService) {
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.codactorToolWindowService = codactorToolWindowService;
        this.fileReaderService = fileReaderService;
        this.fileOpenerService = fileOpenerService;
        initComponents();
    }

    private void initComponents() {
        modificationList = new JList<>();
        modificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modificationListScrollPane = new JBScrollPane(modificationList);

        // Add a horizontal line to separate each FileModification
        modificationList.setFixedCellHeight(80);
        modificationList.setCellRenderer(new SeparatorListCellRenderer<>(new QueuedModificationObjectRenderer(project, fileReaderService)));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );

        updateModificationList(new ArrayList<>());

        modificationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = modificationList.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                QueuedFileModificationObjectHolder queuedFileModificationObjectHolder = modificationList.getModel().getElementAt(index);
                if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION) {
                    FileModification fileModification = queuedFileModificationObjectHolder.getFileModification();
                    if (fileModification.isDone()) {
                        provisionalModificationViewer.updateModificationList(fileModification);

                        // Replace the content of the tool window with an instance of CodeSnippetListViewer
                        //ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                        codactorToolWindowService.openProvisionalModificationViewerToolWindow();
                    }
                    fileOpenerService.openFileInEditor(project, fileModification.getFilePath(), fileModification.getStartIndex());
                }
            }
        });
    }

    public void updateModificationList(List<QueuedFileModificationObjectHolder> queuedFileModificationObjectHolders) {
        DefaultListModel<QueuedFileModificationObjectHolder> model = new DefaultListModel<>();
        for (QueuedFileModificationObjectHolder queuedFileModificationObjectHolder : queuedFileModificationObjectHolders) {
            model.addElement(queuedFileModificationObjectHolder);
        }
        modificationList.setModel(model);
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
