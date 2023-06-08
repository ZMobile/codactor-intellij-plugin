package com.translator.view.codactor.viewer;

import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.model.codactor.modification.*;
import com.translator.service.codactor.file.FileOpenerService;
import com.translator.service.codactor.file.FileReaderService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.renderer.QueuedModificationObjectRenderer;
import com.translator.view.codactor.renderer.SeparatorListCellRenderer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private FileModificationTrackerService fileModificationTrackerService;
    private FileReaderService fileReaderService;
    private FileOpenerService fileOpenerService;
    private BackgroundTaskMapperService backgroundTaskMapperService;
    private Project project;

    @Inject
    public ModificationQueueViewer(Project project,
                                   ProvisionalModificationViewer provisionalModificationViewer,
                                   CodactorToolWindowService codactorToolWindowService,
                                   FileReaderService fileReaderService,
                                   FileOpenerService fileOpenerService,
                                   OpenAiApiKeyService openAiApiKeyService,
                                   OpenAiModelService openAiModelService,
                                   FileModificationTrackerService fileModificationTrackerService,
                                   BackgroundTaskMapperService backgroundTaskMapperService) {
        this.project = project;
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.codactorToolWindowService = codactorToolWindowService;
        this.fileReaderService = fileReaderService;
        this.fileOpenerService = fileOpenerService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
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
                    fileOpenerService.openFileInEditor(fileModification.getFilePath(), fileModification.getRangeMarker().getStartOffset());
                    if (fileModification.isError()) {
                        FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, fileModification.getId(), fileModification.getFilePath(), null, fileModification.getModificationType(), openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                        fileModificationErrorDialog.setVisible(true);
                    }
                    if (fileModification.isDone()) {
                        provisionalModificationViewer.updateModificationList(fileModification);

                        // Replace the content of the tool window with an instance of CodeSnippetListViewer
                        // ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                        codactorToolWindowService.openProvisionalModificationViewerToolWindow();
                    }
                }
            }
        });
        modificationList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean showPause = false;
                boolean showRetry = false;
                boolean showRemove = false;
                JBMenuItem pauseItem = new JBMenuItem("Pause");
                JBMenuItem retryItem = new JBMenuItem("Retry");
                JBMenuItem removeItem = new JBMenuItem("Remove");
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
                                retryItem.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent a) {
                                        fileModificationTrackerService.removeModification(fileModification.getId());
                                        fileModificationTrackerService.addModification(fileModification.getFilePath(), fileModification.getRangeMarker().getStartOffset(), fileModification.getRangeMarker().getEndOffset(), fileModification.getModificationType());
                                    }
                                });
                            } else {
                                showPause = true;
                                pauseItem.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent a) {
                                        fileModificationTrackerService.errorFileModification(fileModification.getId());
                                    }
                                });
                            }
                        }
                        showRemove = true;
                        removeItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent a) {
                                fileModificationTrackerService.removeModification(fileModification.getId());
                            }
                        });
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
                    if (showPause) {
                        jBPopupMenu.add(pauseItem);
                    }
                    if (showRetry) {
                        jBPopupMenu.add(retryItem);
                    }
                    if (showRemove) {
                        jBPopupMenu.add(removeItem);
                    }
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
