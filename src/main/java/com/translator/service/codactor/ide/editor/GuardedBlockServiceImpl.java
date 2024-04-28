package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class GuardedBlockServiceImpl implements GuardedBlockService {
    private final Project project;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final Map<String, RangeMarker> guardedBlocks;

    @Inject
    public GuardedBlockServiceImpl(Project project,
                                   CodeSnippetExtractorService codeSnippetExtractorService,
                                   FileModificationTrackerService fileModificationTrackerService,
                                   FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService) {
        this.project = project;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationSuggestionModificationTrackerService = fileModificationSuggestionModificationTrackerService;
        this.guardedBlocks = new HashMap<>();
    }

    public void addFileModificationGuardedBlock(String fileModificationId, int startOffset, int endOffset) {
        FileModification fileModification = fileModificationTrackerService.getModification(fileModificationId);
        String filePath = fileModification.getFilePath();
        ApplicationManager.getApplication().invokeLater(() -> {
        VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
        if (virtualFile == null) {
            return;
        }

        Document document = codeSnippetExtractorService.getDocument(filePath);
        if (document == null) {
            throw new IllegalStateException("Could not get document for file: " + filePath);
        }
            int newEndOffset = Math.min(endOffset, document.getText().length());
            RangeMarker guardedBlock = document.createGuardedBlock(startOffset, newEndOffset);
            guardedBlocks.put(fileModification.getId(), guardedBlock);
        });
        //uneditableSegmentListenerService.addUneditableFileModificationSegmentListener(fileModificationId);
    }

    public void removeFileModificationGuardedBlock(String fileModificationId) {
        RangeMarker guardedBlock = guardedBlocks.get(fileModificationId);
        if (guardedBlock != null) {
            Document document = guardedBlock.getDocument();
            ApplicationManager.getApplication().invokeLater(() -> {
                        document.removeGuardedBlock(guardedBlock);
                        guardedBlocks.remove(fileModificationId);
            });
            //uneditableSegmentListenerService.removeUneditableFileModificationSegmentListener(fileModificationId);
        }
    }

    public void addFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId, int startOffset, int endOffset) {
        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationSuggestionModificationTrackerService.getModificationSuggestionModification(fileModificationSuggestionModificationId);
        ApplicationManager.getApplication().invokeLater(() -> {
                    Document document = fileModificationSuggestionModification.getEditor().getDocument();
                    RangeMarker guardedBlock = document.createGuardedBlock(startOffset, endOffset);
                    guardedBlocks.put(fileModificationSuggestionModificationId, guardedBlock);
                });
        //uneditableSegmentListenerService.addUneditableFileModificationSuggestionModificationSegmentListener(fileModificationSuggestionModificationId);
    }

    public void removeFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId) {
        RangeMarker guardedBlock = guardedBlocks.get(fileModificationSuggestionModificationId);
        if (guardedBlock != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                        Document document = guardedBlock.getDocument();
                        guardedBlocks.remove(fileModificationSuggestionModificationId);
                        document.removeGuardedBlock(guardedBlock);
                    });
            //uneditableSegmentListenerService.removeUneditableFileModificationSuggestionModificationSegmentListener(fileModificationSuggestionModificationId);
        }
    }
}
