package com.translator.service.code;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestion;
import com.translator.model.modification.FileModificationSuggestionModification;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.modification.tracking.listener.UneditableSegmentListenerService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuardedBlockServiceImpl implements GuardedBlockService {
    private final Project project;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final Map<String, RangeMarker> guardedBlocks;

    @Inject
    public GuardedBlockServiceImpl(Project project,
                                   FileModificationTrackerService fileModificationTrackerService) {
        this.project = project;
        this.fileModificationTrackerService = fileModificationTrackerService;
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

        Document document = PsiDocumentManager.getInstance(project).getDocument(Objects.requireNonNull(PsiManager.getInstance(project).findFile(virtualFile)));
        if (document == null) {
            throw new IllegalStateException("Could not get document for file: " + filePath);
        }
            int newEndOffset = Math.min(endOffset, document.getTextLength());
            RangeMarker guardedBlock = document.createGuardedBlock(startOffset, newEndOffset);
            guardedBlocks.put(fileModification.getId(), guardedBlock);
        });
        //uneditableSegmentListenerService.addUneditableFileModificationSegmentListener(fileModificationId);
    }

    public void removeFileModificationGuardedBlock(String fileModificationId) {
        RangeMarker guardedBlock = guardedBlocks.get(fileModificationId);
        if (guardedBlock != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                        guardedBlocks.remove(fileModificationId);
                        guardedBlock.dispose();
            });
            //uneditableSegmentListenerService.removeUneditableFileModificationSegmentListener(fileModificationId);
        }
    }

    public void addFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId, int startOffset, int endOffset) {
        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationTrackerService.getModificationSuggestionModification(fileModificationSuggestionModificationId);
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(fileModificationSuggestionModification.getSuggestionId());

        Document document = fileModificationSuggestion.getSuggestedCode().getDocument();

        RangeMarker guardedBlock = document.createGuardedBlock(startOffset, endOffset);
        guardedBlocks.put(fileModificationSuggestionModificationId, guardedBlock);
        //uneditableSegmentListenerService.addUneditableFileModificationSuggestionModificationSegmentListener(fileModificationSuggestionModificationId);
    }

    public void removeFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId) {
        RangeMarker guardedBlock = guardedBlocks.get(fileModificationSuggestionModificationId);
        if (guardedBlock != null) {
            guardedBlock.dispose();
            guardedBlocks.remove(fileModificationSuggestionModificationId);
            //uneditableSegmentListenerService.removeUneditableFileModificationSuggestionModificationSegmentListener(fileModificationSuggestionModificationId);
        }
    }
}
