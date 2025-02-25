package com.translator.view.codactor.viewer.modification;

import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.ai.modification.data.ModificationObjectType;
import com.translator.service.codactor.ai.modification.queued.QueuedFileModificationObjectHolderQueryService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.multi.MultiFileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;
import com.translator.service.codactor.ide.file.FileOpenerService;
import com.translator.service.codactor.ide.file.FileReaderService;
import com.translator.service.codactor.ai.modification.AiFileModificationRestarterService;
import com.translator.service.codactor.io.BackgroundTaskMapperService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;
import com.translator.view.codactor.renderer.ModificationRenderer;
import com.translator.view.codactor.renderer.SeparatorListCellRenderer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ModificationQueueViewer extends JBPanel<ModificationQueueViewer> {

    private JList<FileModificationDataHolder> modificationList;
    private JBScrollPane modificationListScrollPane;
    private JBPopupMenu jBPopupMenu;
    private ProvisionalModificationViewer provisionalModificationViewer;
    private CodactorToolWindowService codactorToolWindowService;
    private QueuedFileModificationObjectHolderQueryService queuedFileModificationObjectHolderQueryService;
    private FileModificationTrackerService fileModificationTrackerService;
    private FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService;
    private MultiFileModificationTrackerService multiFileModificationTrackerService;
    private AiFileModificationRestarterService aiFileModificationRestarterService;
    private FileReaderService fileReaderService;
    private FileOpenerService fileOpenerService;
    private FileModificationErrorDialogFactory fileModificationErrorDialogFactory;
    private BackgroundTaskMapperService backgroundTaskMapperService;
    private Project project;

    @Inject
    public ModificationQueueViewer(Project project,
                                   ProvisionalModificationViewer provisionalModificationViewer,
                                   CodactorToolWindowService codactorToolWindowService,
                                   FileReaderService fileReaderService,
                                   FileOpenerService fileOpenerService,
                                   FileModificationTrackerService fileModificationTrackerService,
                                   FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService,
                                   MultiFileModificationTrackerService multiFileModificationTrackerService,
                                   QueuedFileModificationObjectHolderQueryService queuedFileModificationObjectHolderQueryService,
                                   AiFileModificationRestarterService aiFileModificationRestarterService,
                                   FileModificationErrorDialogFactory fileModificationErrorDialogFactory,
                                   BackgroundTaskMapperService backgroundTaskMapperService) {
        this.project = project;
        this.provisionalModificationViewer = provisionalModificationViewer;
        this.codactorToolWindowService = codactorToolWindowService;
        this.fileReaderService = fileReaderService;
        this.fileOpenerService = fileOpenerService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationSuggestionModificationTrackerService = fileModificationSuggestionModificationTrackerService;
        this.multiFileModificationTrackerService = multiFileModificationTrackerService;
        this.queuedFileModificationObjectHolderQueryService = queuedFileModificationObjectHolderQueryService;
        this.fileModificationTrackerService.addModificationUpdateListener(fileModification -> updateModificationList(queuedFileModificationObjectHolderQueryService.getQueuedFileModificationObjectHolders()));
        this.fileModificationSuggestionModificationTrackerService.addModificationSuggestionModificationListener(fileModificationSuggestionModification -> updateModificationList(queuedFileModificationObjectHolderQueryService.getQueuedFileModificationObjectHolders()));
        this.multiFileModificationTrackerService.addMultiFileModificationListener(multiFileModification -> updateModificationList(queuedFileModificationObjectHolderQueryService.getQueuedFileModificationObjectHolders()));
        this.aiFileModificationRestarterService = aiFileModificationRestarterService;
        this.fileModificationErrorDialogFactory = fileModificationErrorDialogFactory;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        initComponents();
    }

    private void initComponents() {
        modificationList = new JList<>();
        modificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modificationListScrollPane = new JBScrollPane(modificationList);

        // Add a horizontal line to separate each FileModification
        modificationList.setFixedCellHeight(80);
        modificationList.setCellRenderer(new SeparatorListCellRenderer<>(new ModificationRenderer(project, fileReaderService)));

        jBPopupMenu = new JBPopupMenu();

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(modificationListScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );

        updateModificationList(new ArrayList<>());

        modificationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = modificationList.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                FileModificationDataHolder fileModificationDataHolder = modificationList.getModel().getElementAt(index);
                if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION) {
                    FileModification fileModification = fileModificationDataHolder.getFileModification();
                    System.out.println("This gets called 1");
                    if (fileModification.getRangeMarker() != null) {
                        fileOpenerService.openFileInEditor(fileModification.getFilePath(), fileModification.getRangeMarker().getStartOffset());
                    } else {
                        fileOpenerService.openFileInEditor(fileModification.getFilePath());
                    }
                    if (fileModification.isError()) {
                        FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(fileModification.getId(), fileModification.getFilePath(), "", fileModification.getModificationType());
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
                FileModificationDataHolder fileModificationDataHolder = null;
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int selectedIndex = modificationList.locationToIndex(e.getPoint());
                    fileModificationDataHolder = modificationList.getModel().getElementAt(selectedIndex);
                    modificationList.setSelectedIndex(selectedIndex);
                    if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION) {
                        FileModification fileModification = fileModificationDataHolder.getFileModification();
                        if (!fileModification.isDone()) {
                            if (fileModification.isError()) {
                                showRetry = true;
                                retryItem.addActionListener(a -> aiFileModificationRestarterService.restartFileModification(fileModification));
                            } else {
                                showPause = true;
                                pauseItem.addActionListener(a -> fileModificationTrackerService.errorFileModification(fileModification.getId()));
                            }
                        }
                        showRemove = true;
                        removeItem.addActionListener(a -> fileModificationTrackerService.removeModification(fileModification.getId()));
                    } else if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationDataHolder.getFileModificationSuggestionModification();
                        if (fileModificationSuggestionModification.isError()) {
                            showRetry = true;
                        } else {
                            showPause = true;
                        }
                        showRemove = true;
                    } else if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.MULTI_FILE_MODIFICATION) {
                        showRemove = true;
                    }
                    removeItem.setVisible(showRemove);
                    pauseItem.setVisible(showPause);
                    retryItem.setVisible(showRetry);
                    jBPopupMenu.removeAll();
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

    private void updateModificationList(List<FileModificationDataHolder> fileModificationDataHolders) {
        DefaultListModel<FileModificationDataHolder> model = new DefaultListModel<>();
        for (FileModificationDataHolder fileModificationDataHolder : fileModificationDataHolders) {
            model.addElement(fileModificationDataHolder);
        }
        modificationList.setModel(model);
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
