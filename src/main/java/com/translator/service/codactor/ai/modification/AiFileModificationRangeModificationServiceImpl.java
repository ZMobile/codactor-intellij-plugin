package com.translator.service.codactor.ai.modification;

import com.intellij.openapi.editor.RangeMarker;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.service.codactor.ai.modification.tracking.CodeRangeTrackerService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerServiceImpl;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.editor.GuardedBlockService;

import javax.inject.Inject;

public class AiFileModificationRangeModificationServiceImpl implements AiFileModificationRangeModificationService {
    private final CodeRangeTrackerService codeRangeTrackerService;
    private final GuardedBlockService guardedBlockService;
    private final CodeHighlighterService codeHighlighterService;
    private final FileModificationTrackerService fileModificationTrackerService;

    @Inject
    public AiFileModificationRangeModificationServiceImpl(CodeRangeTrackerService codeRangeTrackerService, GuardedBlockService guardedBlockService, CodeHighlighterService codeHighlighterService, FileModificationTrackerService fileModificationTrackerService) {
        this.codeRangeTrackerService = codeRangeTrackerService;
        this.guardedBlockService = guardedBlockService;
        this.codeHighlighterService = codeHighlighterService;
        this.fileModificationTrackerService = fileModificationTrackerService;
    }

    @Override
    public void modifyFileModificationRange(FileModification fileModification, int newStartIndex, int newEndIndex) {
        RangeMarker rangeMarker = codeRangeTrackerService.createRangeMarker(fileModification.getFilePath(), newStartIndex, newEndIndex);
        fileModification.setRangeMarker(rangeMarker);
        guardedBlockService.removeFileModificationGuardedBlock(fileModification.getId());
        guardedBlockService.addFileModificationGuardedBlock(fileModification, newStartIndex, newEndIndex);
        fileModificationTrackerService.updateFileModificationListeners(fileModification);
        codeHighlighterService.highlightTextArea(fileModification);
    }
}
