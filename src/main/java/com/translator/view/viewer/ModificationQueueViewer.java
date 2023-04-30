package com.translator.view.viewer;

import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestionModification;
import com.translator.model.modification.QueuedFileModificationObjectHolder;
import com.translator.model.modification.QueuedModificationObjectType;
import com.translator.service.file.FileOpenerService;
import com.translator.service.file.FileReaderService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.renderer.QueuedModificationObjectRenderer;
import com.translator.view.renderer.SeparatorListCellRenderer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ModificationQueueViewer extends JBPanel<ModificationQueueViewer> {

    private JBList<QueuedFileModificationObjectHolder> modificationList;
    private JBScrollPane modificationListScrollPane;
    private JBPopupMenu jBPopupMenu;
    private ProvisionalModificationViewer provisionalModificationViewer;
    private CodactorToolWindowService codactorToolWindowService;
    private FileReaderService fileReaderService;
    private FileOpenerService fileOpenerService;
    private Project project;

    @Inject
    public ModificationQueueViewer(Project project,
                                   ProvisionalModificationViewer provisionalModificationViewer,
                                   CodactorToolWindowService codactorToolWindowService,
                                   FileReaderService fileReaderService,
                                   FileOpenerService fileOpenerService) {
        this.project = project;
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.codactorToolWindowService = codactorToolWindowService;
        this.fileReaderService = fileReaderService;
        this.fileOpenerService = fileOpenerService;
        initComponents();
    }

    private void initComponents() {
        modificationList = new JBList<>();
        modificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modificationListScrollPane = new JBScrollPane(modificationList);

        // Add a horizontal line to separate each FileModification
        modificationList.setFixedCellHeight(80);
        modificationList.setCellRenderer(new SeparatorListCellRenderer<>(new QueuedModificationObjectRenderer(project, fileReaderService)));

        jBPopupMenu = new JBPopupMenu();

        JBMenuItem pauseItem = new JBMenuItem("Pause");
        JBMenuItem retryItem = new JBMenuItem("Retry");
        JBMenuItem removeItem = new JBMenuItem("Remove");
        jBPopupMenu.add(pauseItem);
        jBPopupMenu.add(retryItem);
        jBPopupMenu.add(removeItem);

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
                        // ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                        codactorToolWindowService.openProvisionalModificationViewerToolWindow();
                    }
                    fileOpenerService.openFileInEditor(project, fileModification.getFilePath(), fileModification.getRangeMarker().getStartOffset());
                }
            }
        });
        modificationList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean showPause = false;
                boolean showRetry = false;
                boolean showRemove = false;
                QueuedFileModificationObjectHolder queuedFileModificationObjectHolder = null;
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int selectedIndex = modificationList.locationToIndex(e.getPoint());
                    queuedFileModificationObjectHolder = modificationList.getModel().getElementAt(selectedIndex);
                    modificationList.setSelectedIndex(selectedIndex);
                    if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION) {
                        FileModification fileModification = queuedFileModificationObjectHolder.getFileModification();
                        if (!fileModification.isDone()) {
                            if (fileModification.isError()) {
                                showRetry = true;
                            } else {
                                showPause = true;
                            }
                        }
                        showRemove = true;
                    } else if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                        FileModificationSuggestionModification fileModificationSuggestionModification = queuedFileModificationObjectHolder.getFileModificationSuggestionModification();
                        if (fileModificationSuggestionModification.isError()) {
                            showRetry = true;
                        } else {
                            showPause = true;
                        }
                        showRemove = true;
                    } else if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.MULTI_FILE_MODIFICATION) {
                        showRemove = true;
                    }
                    removeItem.setVisible(showRemove);
                    pauseItem.setVisible(showPause);
                    retryItem.setVisible(showRetry);
                    jBPopupMenu.show(modificationList, e.getX(), e.getY());
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
