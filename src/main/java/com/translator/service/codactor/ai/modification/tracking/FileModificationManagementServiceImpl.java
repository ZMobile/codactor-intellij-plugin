package com.translator.service.codactor.ai.modification.tracking;

import com.google.inject.Injector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.translator.CodactorInjector;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.*;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.GuardedBlockService;
import com.translator.service.codactor.ide.editor.RangeReplaceService;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.ide.file.FileCreatorService;
import com.translator.service.codactor.ide.file.FileRemoverService;
import com.translator.service.codactor.ide.file.RenameFileService;
import com.translator.service.codactor.ide.handler.EditorClickHandlerService;
import com.translator.service.codactor.io.BackgroundTaskMapperService;
import com.translator.view.codactor.dialog.modification.ProvisionalModificationCustomizerDialog;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;

import javax.inject.Inject;
import javax.swing.*;
import java.util.*;

public class FileModificationManagementServiceImpl implements FileModificationManagementService {
    private Project project;
    private List<MultiFileModification> activeMultiFileModifications;
    private FileModificationTrackerService fileModificationTrackerService;
    private FileModificationService fileModificationService;
    private FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService;
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
    public FileModificationManagementServiceImpl(Project project,
                                                 FileModificationTrackerService fileModificationTrackerService,
                                                 FileModificationService fileModificationService,
                                                 FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService,
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
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationService = fileModificationService;
        this.fileModificationSuggestionModificationTrackerService = fileModificationSuggestionModificationTrackerService;
        this.activeMultiFileModifications = new ArrayList<>();
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
        String fileModificationId = fileModificationTrackerService.addModification(filePath, modification, startIndex, endIndex, modificationType, priorContext);
        if (fileModificationId == null || fileModificationId.startsWith("Error")) {
            JOptionPane.showMessageDialog(null, "Can't modify code that is already being modified", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return fileModificationId;
        }
        if (modificationQueueViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        return fileModificationId;
    }

    public String addModificationSuggestionModification(Editor editor, String filePath, String suggestionId, int startIndex, int endIndex, ModificationType modificationType) {
        FileModificationSuggestion fileModificationSuggestion = getModificationSuggestion(suggestionId);
        String fileModificationSuggestionModificationId = fileModificationSuggestionModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion, editor, startIndex, endIndex, modificationType);
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
        fileModificationTrackerService.removeModification(modificationId);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public void removeModificationSuggestionModification(String modificationSuggestionModificationId) {
        fileModificationSuggestionModificationTrackerService.removeModificationSuggestionModification(modificationSuggestionModificationId);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
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
        fileModificationTrackerService.implementModification(modificationId, modification, silent);
        disposeProvisionalModificationCustomizers(fileModification);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    @Override
    public void implementModificationSuggestionModificationUpdate(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord) {
        fileModificationSuggestionModificationTrackerService.implementModification(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId(), fileModificationSuggestionModificationRecord.getEditedCode());
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
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
        fileModificationTrackerService.readyFileModificationUpdate(modificationId, subjectLine, modificationOptions);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public void undoReadyFileModification(String modificationId) {
        fileModificationTrackerService.undoReadyFileModification(modificationId);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public List<FileModificationSuggestionModification> getAllFileModificationSuggestionModifications() {
        List<FileModificationSuggestionModification> fileModificationSuggestionModifications = new ArrayList<>();
        for (FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker : fileModificationSuggestionModificationTrackerService.getActiveModificationSuggestionModifications().values()) {
            fileModificationSuggestionModifications.addAll(fileModificationSuggestionModificationTracker.getModifications());
        }
        return fileModificationSuggestionModifications;
    }


    public void setModificationQueueViewer(ModificationQueueViewer modificationQueueViewer) {
        this.modificationQueueViewer = modificationQueueViewer;
    }

    @Override
    public void errorFileModification(String modificationId) {
        fileModificationTrackerService.errorFileModification(modificationId);
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
