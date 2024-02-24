package com.translator.service.codactor.modification.tracking;

import com.google.inject.Injector;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.translator.CodactorInjector;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.*;
import com.translator.model.codactor.modification.data.FileModificationDataHolder;
import com.translator.service.codactor.editor.CodeHighlighterService;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.editor.GuardedBlockService;
import com.translator.service.codactor.editor.RangeReplaceService;
import com.translator.service.codactor.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.file.FileCreatorService;
import com.translator.service.codactor.file.FileRemoverService;
import com.translator.service.codactor.file.RenameFileService;
import com.translator.service.codactor.modification.tracking.listener.EditorClickHandlerService;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.view.codactor.dialog.ProvisionalModificationCustomizerDialog;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;

import javax.inject.Inject;
import javax.swing.*;
import java.io.File;
import java.util.*;

public class FileModificationTrackerServiceImpl implements FileModificationTrackerService {
    private Project project;
    private Map<String, FileModificationTracker> activeModificationFiles;
    private Map<String, FileModificationSuggestionModificationTracker> activeModificationSuggestionModifications;
    private Map<String, List<ProvisionalModificationCustomizerDialog>> provisionalModificationCustomizerMap;
    private List<MultiFileModification> activeMultiFileModifications;
    private CodeHighlighterService codeHighlighterService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private CodeRangeTrackerService codeRangeTrackerService;
    private GuardedBlockService guardedBlockService;
    private RangeReplaceService rangeReplaceService;
    private EditorClickHandlerService editorClickHandlerService;
    private RenameFileService renameFileService;
    private BackgroundTaskMapperService backgroundTaskMapperService;
    private DiffEditorGeneratorService diffEditorGeneratorService;
    private FileCreatorService fileCreatorService;
    private FileRemoverService fileRemoverService;
    private ModificationQueueViewer modificationQueueViewer;

    @Inject
    public FileModificationTrackerServiceImpl(Project project,
                                              CodeHighlighterService codeHighlighterService,
                                              CodeSnippetExtractorService codeSnippetExtractorService,
                                              CodeRangeTrackerService codeRangeTrackerService,
                                              GuardedBlockService guardedBlockService,
                                              RangeReplaceService rangeReplaceService,
                                              EditorClickHandlerService editorClickHandlerService,
                                              RenameFileService renameFileService,
                                              BackgroundTaskMapperService backgroundTaskMapperService,
                                              DiffEditorGeneratorService diffEditorGeneratorService,
                                              FileCreatorService fileCreatorService,
                                              FileRemoverService fileRemoverService) {
        this.project = project;
        this.activeModificationFiles = new HashMap<>();
        this.activeModificationSuggestionModifications = new HashMap<>();
        this.activeMultiFileModifications = new ArrayList<>();
        this.provisionalModificationCustomizerMap = new HashMap<>();
        this.codeHighlighterService = codeHighlighterService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codeRangeTrackerService = codeRangeTrackerService;
        this.guardedBlockService = guardedBlockService;
        this.rangeReplaceService = rangeReplaceService;
        this.editorClickHandlerService = editorClickHandlerService;
        this.renameFileService = renameFileService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.diffEditorGeneratorService = diffEditorGeneratorService;
        this.fileCreatorService = fileCreatorService;
        this.fileRemoverService = fileRemoverService;
    }

    public String addModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        String newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        FileModificationTracker fileModificationTracker;
        if (activeModificationFiles.containsKey(newFilePath)) {
            fileModificationTracker = activeModificationFiles.get(newFilePath);
        } else {
            fileModificationTracker = new FileModificationTracker(project, newFilePath, codeSnippetExtractorService, rangeReplaceService, codeRangeTrackerService, diffEditorGeneratorService);
            activeModificationFiles.put(newFilePath, fileModificationTracker);
        }
        String fileModificationId = fileModificationTracker.addModification(modification, startIndex, endIndex, modificationType, priorContext);
        if (fileModificationId == null || fileModificationId.startsWith("Error")) {
            JOptionPane.showMessageDialog(null, "Can't modify code that is already being modified", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return fileModificationId;
        }
        if (modificationType != ModificationType.CREATE) {
            editorClickHandlerService.addEditorClickHandler(newFilePath);
            guardedBlockService.addFileModificationGuardedBlock(fileModificationId, startIndex, endIndex);
            codeHighlighterService.highlightTextArea(fileModificationTracker);
        }
        if (modificationQueueViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        return fileModificationId;
    }

    public String addModificationSuggestionModification(Editor editor, String filePath, String suggestionId, int startIndex, int endIndex, ModificationType modificationType) {
        String newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        FileModificationSuggestion fileModificationSuggestion = getModificationSuggestion(suggestionId);
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker;
        if (activeModificationSuggestionModifications.containsKey(suggestionId)) {
            fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.get(suggestionId);
        } else {
            fileModificationSuggestionModificationTracker = new FileModificationSuggestionModificationTracker(fileModificationSuggestion, rangeReplaceService);
            activeModificationSuggestionModifications.put(suggestionId, fileModificationSuggestionModificationTracker);
        }
        String fileModificationSuggestionModificationId = fileModificationSuggestionModificationTracker.addModificationSuggestionModification(editor, newFilePath, startIndex, endIndex, modificationType);
        if (fileModificationSuggestionModificationId == null || fileModificationSuggestionModificationId.startsWith("Error")) {
            JOptionPane.showMessageDialog(null, "Can't modify code that is already being modified", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return fileModificationSuggestionModificationId;
        }
        if (modificationQueueViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        guardedBlockService.addFileModificationSuggestionModificationGuardedBlock(fileModificationSuggestionModificationId, startIndex, endIndex);
        //jTreeHighlighterService.repaint();
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModificationTracker, fileModificationSuggestionModificationId);
        return fileModificationSuggestionModificationId;
    }

    public String addMultiFileModification(String description, String language, String fileExtension, String filePath) {
        MultiFileModification multiFileModification = new MultiFileModification(description, language, fileExtension, filePath);
        activeMultiFileModifications.add(multiFileModification);
        if (modificationQueueViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        return multiFileModification.getId();
    }

    public String addMultiFileModification(String description) {
        MultiFileModification multiFileModification = new MultiFileModification(description);
        activeMultiFileModifications.add(multiFileModification);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        return multiFileModification.getId();
    }

    public void removeModification(String modificationId) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElseThrow();
        FileModification fileModification = fileModificationTracker.getModification(modificationId);
        for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
            ApplicationManager.getApplication().invokeLater(fileModificationSuggestion::dispose);
            FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = getModificationSuggestionModificationTracker(fileModificationSuggestion.getId());
            if (fileModificationSuggestionModificationTracker == null) {
                continue;
            }
            for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModificationTracker.getModifications()) {
                removeModificationSuggestionModification(fileModificationSuggestionModification.getId());
            }
        }
        fileModificationTracker.removeModification(modificationId);
        if (fileModificationTracker.getModifications().isEmpty()) {
            activeModificationFiles.values().remove(fileModificationTracker);
            editorClickHandlerService.removeEditorClickHandler(fileModificationTracker.getFilePath());
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        guardedBlockService.removeFileModificationGuardedBlock(modificationId);
        codeHighlighterService.highlightTextArea(fileModificationTracker);
        disposeProvisionalModificationCustomizers(fileModification);
        if (backgroundTaskMapperService.hasTask(modificationId)) {
            backgroundTaskMapperService.cancelTask(modificationId);
        }
    }

    public void removeModificationSuggestionModification(String modificationSuggestionModificationId) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.values().stream()
                .filter(m -> m.hasModificationSuggestionModification(modificationSuggestionModificationId))
                .findFirst()
                .orElseThrow();
        fileModificationSuggestionModificationTracker.removeModificationSuggestionModification(modificationSuggestionModificationId);
        if (fileModificationSuggestionModificationTracker.getModifications().isEmpty()) {
            activeModificationSuggestionModifications.values().remove(fileModificationSuggestionModificationTracker);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        guardedBlockService.removeFileModificationSuggestionModificationGuardedBlock(modificationSuggestionModificationId);
        //jTreeHighlighterService.repaint();
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModificationTracker, modificationSuggestionModificationId);
    }

    public void removeMultiFileModification(String multiFileModificationId) {
        MultiFileModification multiFileModification = activeMultiFileModifications.stream()
                .filter(m -> m.getId().equals(multiFileModificationId))
                .findFirst()
                .orElseThrow();
        activeMultiFileModifications.remove(multiFileModification);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public void setMultiFileModificationStage(String multiFileModificationId, String stage) {
        MultiFileModification multiFileModification = activeMultiFileModifications.stream()
                .filter(m -> m.getId().equals(multiFileModificationId))
                .findFirst()
                .orElseThrow();
        multiFileModification.setStage(stage);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    @Override
    public void implementModificationUpdate(String modificationId, String modification, boolean silent) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationTracker == null) {
            return;
        }
        FileModification fileModification = fileModificationTracker.getModification(modificationId);
        if (fileModification.isFileCreationAtFilePathOnAcceptance()) {
            PsiFile createdFile = fileCreatorService.createAndReturnPsiFile(fileModification.getFilePath());
            if (createdFile != null) {
                FileEditorManager.getInstance(project).openFile(createdFile.getVirtualFile(), true);
            }
        }
        for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
            ApplicationManager.getApplication().invokeLater(fileModificationSuggestion::dispose);
            FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = getModificationSuggestionModificationTracker(fileModificationSuggestion.getId());
            if (fileModificationSuggestionModificationTracker == null) {
                continue;
            }
            for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModificationTracker.getModifications()) {
                removeModificationSuggestionModification(fileModificationSuggestionModification.getId());
            }
        }
        guardedBlockService.removeFileModificationGuardedBlock(modificationId);
        fileModificationTracker.implementModification(modificationId, modification, silent);
        if (fileModificationTracker.getModifications().isEmpty()) {
            activeModificationFiles.values().remove(fileModificationTracker);
            editorClickHandlerService.removeEditorClickHandler(fileModificationTracker.getFilePath());
        }
        codeHighlighterService.highlightTextArea(fileModificationTracker);
        disposeProvisionalModificationCustomizers(fileModification);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        if (fileModification.getModificationType() == ModificationType.TRANSLATE) {
            File file = new File(fileModification.getFilePath());
            String fileNameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.'));
            String newFileType;
            if (fileModification.getNewFileType().startsWith(".")){
                newFileType = fileModification.getNewFileType();
            } else {
                newFileType = "." + fileModification.getNewFileType();
            }
            String newFileName = fileNameWithoutExtension + newFileType;
            renameFileService.renameFile(fileModification.getFilePath(), newFileName);
        }
        if (fileModification.isFileDeletionAtFilePathOnAcceptance()) {
            fileRemoverService.deleteCodeFile(fileModification.getFilePath());
        }
    }

    @Override
    public void implementModificationSuggestionModificationUpdate(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.values().stream()
                .filter(m -> m.hasModificationSuggestionModification(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId()))
                .findFirst()
                .orElseThrow();
        guardedBlockService.removeFileModificationSuggestionModificationGuardedBlock(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId());
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModificationTracker, fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId());
        fileModificationSuggestionModificationTracker.implementModification(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId(), fileModificationSuggestionModificationRecord.getEditedCode());
        if (fileModificationSuggestionModificationTracker.getModifications().isEmpty()) {
            activeModificationSuggestionModifications.values().remove(fileModificationSuggestionModificationTracker);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        FileModificationSuggestion fileModificationSuggestion = fileModificationSuggestionModificationTracker.getFileModificationSuggestion();
        diffEditorGeneratorService.updateDiffEditor(fileModificationSuggestion.getDiffEditor(), fileModificationSuggestion.getBeforeCode(), fileModificationSuggestionModificationRecord.getEditedCode());
    }

    public Map<String, FileModificationTracker> getActiveModificationFiles() {
        return activeModificationFiles;
    }

    public Map<String, FileModificationSuggestionModificationTracker> getActiveModificationSuggestionModifications() {
        return activeModificationSuggestionModifications;
    }

    public FileModification getModification(String modificationId) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationTracker == null) {
            return null;
        }
        return fileModificationTracker.getModification(modificationId);
    }

    public FileModificationSuggestion getModificationSuggestion(String suggestionId) {
        List<FileModification> fileModifications = getAllFileModifications();
        for (FileModification fileModification : fileModifications) {
            FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().stream()
                    .filter(m -> m.getId().equals(suggestionId))
                    .findFirst()
                    .orElse(null);
            if (fileModificationSuggestion != null) {
                return fileModificationSuggestion;
            }
        }
        return null;
    }

    public FileModificationTracker getModificationTracker(String filePath) {
        return activeModificationFiles.get(filePath);
    }

    public FileModificationSuggestionModificationTracker getModificationSuggestionModificationTracker(String suggestionId) {
        return activeModificationSuggestionModifications.get(suggestionId);
    }

    public FileModificationSuggestionModification getModificationSuggestionModification(String modificationSuggestionModificationId) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = getActiveModificationSuggestionModifications().values().stream()
                .filter(m -> m.hasModificationSuggestionModification(modificationSuggestionModificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationSuggestionModificationTracker == null) {
            return null;
        }
        return fileModificationSuggestionModificationTracker.getModificationSuggestionModification(modificationSuggestionModificationId);
    }

    public void readyFileModificationUpdate(String modificationId, String subjectLine, List<FileModificationSuggestionRecord> modificationOptions) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationTracker == null) {
            return;
        }
        fileModificationTracker.readyFileModificationUpdate(modificationId, subjectLine, modificationOptions);
        codeHighlighterService.highlightTextArea(fileModificationTracker);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public void undoReadyFileModification(String modificationId) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationTracker == null) {
            return;
        }
        fileModificationTracker.undoReadyFileModification(modificationId);
        codeHighlighterService.highlightTextArea(fileModificationTracker);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public List<FileModification> getAllFileModifications() {
        List<FileModification> fileModifications = new ArrayList<>();
        for (FileModificationTracker fileModificationTracker : activeModificationFiles.values()) {
            fileModifications.addAll(fileModificationTracker.getModifications());
        }
        return fileModifications;
    }

    public List<FileModificationSuggestionModification> getAllFileModificationSuggestionModifications() {
        List<FileModificationSuggestionModification> fileModificationSuggestionModifications = new ArrayList<>();
        for (FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker : activeModificationSuggestionModifications.values()) {
            fileModificationSuggestionModifications.addAll(fileModificationSuggestionModificationTracker.getModifications());
        }
        return fileModificationSuggestionModifications;
    }

    public List<FileModificationDataHolder> getQueuedFileModificationObjectHolders() {
        List<FileModificationDataHolder> fileModificationDataHolders = new ArrayList<>();
        List<FileModification> fileModifications = new ArrayList<>(getAllFileModifications());
        for (FileModification fileModification : fileModifications) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(fileModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        List<FileModificationSuggestionModification> fileModificationSuggestionModifications = new ArrayList<>(getAllFileModificationSuggestionModifications());
        for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModifications) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(fileModificationSuggestionModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        for (MultiFileModification multiFileModification : activeMultiFileModifications) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(multiFileModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        return fileModificationDataHolders;
    }


    public void setModificationQueueViewer(ModificationQueueViewer modificationQueueViewer) {
        this.modificationQueueViewer = modificationQueueViewer;
    }

    public void addProvisionalModificationCustomizer(ProvisionalModificationCustomizerDialog provisionalModificationCustomizerDialog) {
        List<ProvisionalModificationCustomizerDialog> provisionalModificationCustomizerDialogList = provisionalModificationCustomizerMap.get(provisionalModificationCustomizerDialog.getFileModificationSuggestion().getId());
        if (provisionalModificationCustomizerDialogList == null) {
            provisionalModificationCustomizerDialogList = new ArrayList<>();
        }
        provisionalModificationCustomizerDialogList.add(provisionalModificationCustomizerDialog);
        provisionalModificationCustomizerMap.put(provisionalModificationCustomizerDialog.getFileModificationSuggestion().getId(), provisionalModificationCustomizerDialogList);
    }

    private void disposeProvisionalModificationCustomizers(FileModification fileModification) {
        for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
            List<ProvisionalModificationCustomizerDialog> provisionalModificationCustomizerDialogList = provisionalModificationCustomizerMap.get(fileModificationSuggestion.getId());
            if (provisionalModificationCustomizerDialogList != null) {
                for (ProvisionalModificationCustomizerDialog provisionalModificationCustomizerDialog : provisionalModificationCustomizerDialogList) {
                    provisionalModificationCustomizerDialog.dispose();
                }
            }
            provisionalModificationCustomizerMap.remove(fileModificationSuggestion.getId());
        }
    }

    @Override
    public void errorFileModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        if (backgroundTaskMapperService.hasTask(modificationId)) {
            backgroundTaskMapperService.cancelTask(modificationId);
        }
        if (fileModification == null) {
            return;
        }
        fileModification.setError(true);
        codeHighlighterService.highlightTextArea(getModificationTracker(fileModification.getFilePath()));
    }

    @Override
    public void retryFileModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        if (fileModification == null) {
            return;
        }
        fileModification.setError(false);
    }
}
