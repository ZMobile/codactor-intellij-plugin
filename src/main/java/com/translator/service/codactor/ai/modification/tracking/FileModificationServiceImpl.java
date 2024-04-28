package com.translator.service.codactor.ai.modification.tracking;

import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.*;
import com.translator.service.codactor.ai.modification.tracking.suggestion.FileModificationSuggestionService;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.GuardedBlockService;
import com.translator.service.codactor.ide.editor.RangeReplaceService;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.ide.file.FileCreatorService;
import com.translator.service.codactor.ide.file.FileTranslatorService;
import com.translator.service.codactor.io.BackgroundTaskMapperService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FileModificationServiceImpl implements FileModificationService {
    private final Project project;
    private final FileModificationSuggestionService fileModificationSuggestionService;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final RangeReplaceService rangeReplaceService;
    private final CodeRangeTrackerService codeRangeTrackerService;
    private final CodeHighlighterService codeHighlighterService;
    private final GuardedBlockService guardedBlockService;
    private final BackgroundTaskMapperService backgroundTaskMapperService;
    private final FileCreatorService fileCreatorService;
    private final FileTranslatorService fileTranslatorService;

    @Inject
    public FileModificationServiceImpl(Project project,
                                       FileModificationSuggestionService fileModificationSuggestionService,
                                       CodeSnippetExtractorService codeSnippetExtractorService,
                                       RangeReplaceService rangeReplaceService,
                                       CodeRangeTrackerService codeRangeTrackerService,
                                       CodeHighlighterService codeHighlighterService,
                                        GuardedBlockService guardedBlockService,
                                       BackgroundTaskMapperService backgroundTaskMapperService,
                                       FileCreatorService fileCreatorService,
                                       FileTranslatorService fileTranslatorService) {
        this.project = project;
        this.fileModificationSuggestionService = fileModificationSuggestionService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.rangeReplaceService = rangeReplaceService;
        this.codeRangeTrackerService = codeRangeTrackerService;
        this.codeHighlighterService = codeHighlighterService;
        this.guardedBlockService = guardedBlockService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.fileCreatorService = fileCreatorService;
        this.fileTranslatorService = fileTranslatorService;
    }

    public FileModification addModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        String beforeText = codeSnippetExtractorService.getSnippet(filePath, startIndex, endIndex);
        RangeMarker rangeMarker = codeRangeTrackerService.createRangeMarker(filePath, startIndex, endIndex);
        FileModification fileModification = new FileModification(filePath, modification, rangeMarker, beforeText, modificationType, priorContext);
        if (modificationType != ModificationType.CREATE) {
            guardedBlockService.addFileModificationGuardedBlock(fileModification.getId(), startIndex, endIndex);
            codeHighlighterService.highlightTextArea(fileModification);
        }
        return fileModification;
    }

    public void removeModification(FileModification fileModification) {
        if (fileModification != null) {
            if (fileModification.getRangeMarker() != null) {
                fileModification.getRangeMarker().dispose();
            }
            guardedBlockService.removeFileModificationGuardedBlock(fileModification.getId());
            codeHighlighterService.highlightTextArea(fileModification);
            if (backgroundTaskMapperService.hasTask(fileModification.getId())) {
                backgroundTaskMapperService.cancelTask(fileModification.getId());
            }
        }
    }

    public void implementModification(FileModification fileModification, String modification, boolean silent) {
        RangeMarker rangeMarker = fileModification.getRangeMarker();
        if (rangeMarker != null && rangeMarker.isValid()) {
            int formerStartIndex = rangeMarker.getStartOffset();
            int formerEndIndex = rangeMarker.getEndOffset();
            rangeReplaceService.replaceRange(fileModification.getFilePath(), formerStartIndex, formerEndIndex, modification, silent);
            rangeMarker.dispose(); // Dispose the RangeMarker after it's no longer needed
        } else if (fileModification.getModificationType() == ModificationType.CREATE) {
            PsiFile createdFile = fileCreatorService.createAndReturnPsiFile(fileModification.getFilePath());
            if (createdFile != null) {
                FileEditorManager.getInstance(project).openFile(createdFile.getVirtualFile(), true);
            }
            rangeReplaceService.replaceRange(fileModification.getFilePath(), 0, 0, modification, silent);
        }
        if (fileModification.getModificationType() == ModificationType.TRANSLATE) {
            fileTranslatorService.translateFile(fileModification.getFilePath(), fileModification.getNewFileType().trim().toLowerCase());
        }
    }

    public void readyFileModificationUpdate(FileModification fileModification, String subjectLine, List<FileModificationSuggestionRecord> modificationOptions) {
        if (fileModification != null) {
            fileModification.setSubjectLine(subjectLine);
            fileModification.setDone(true);
            fileModification.setModificationRecordId(modificationOptions.get(0).getModificationId());
            fileModificationSuggestionService.createFileModificationSuggestions(fileModification, modificationOptions);
            if (fileModification.getModificationType() != ModificationType.CREATE) {
                codeHighlighterService.highlightTextArea(fileModification);
            }
        }
    }

    public void undoReadyFileModification(FileModification fileModification) {
        if (fileModification != null) {
            fileModification.setDone(false);
            fileModification.getModificationOptions().clear();
        }
        codeHighlighterService.highlightTextArea(fileModification);
    }

    @Override
    public void errorFileModification(FileModification fileModification) {
        if (backgroundTaskMapperService.hasTask(fileModification.getId())) {
            backgroundTaskMapperService.cancelTask(fileModification.getId());
        }
        fileModification.setError(true);
        codeHighlighterService.highlightTextArea(fileModification);
    }
}
