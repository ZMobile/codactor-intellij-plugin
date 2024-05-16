package com.translator.service.codactor.ai.modification.tracking.suggestion.modification;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.editor.GuardedBlockService;
import com.translator.service.codactor.ide.editor.RangeReplaceService;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;

import java.util.List;

public class FileModificationSuggestionModificationServiceImpl implements FileModificationSuggestionModificationService {
    private final CodeHighlighterService codeHighlighterService;
    private final GuardedBlockService guardedBlockService;
    private final RangeReplaceService rangeReplaceService;
    private final DiffEditorGeneratorService diffEditorGeneratorService;

    public FileModificationSuggestionModificationServiceImpl(CodeHighlighterService codeHighlighterService,
                                                             GuardedBlockService guardedBlockService,
                                                             RangeReplaceService rangeReplaceService,
                                                             DiffEditorGeneratorService diffEditorGeneratorService) {
        this.codeHighlighterService = codeHighlighterService;
        this.guardedBlockService = guardedBlockService;
        this.rangeReplaceService = rangeReplaceService;
        this.diffEditorGeneratorService = diffEditorGeneratorService;
    }

    @Override
    public FileModificationSuggestionModification addModificationSuggestionModification(FileModificationSuggestion fileModificationSuggestion, Editor editor, int startIndex, int endIndex, ModificationType modificationType) {
        Document document = fileModificationSuggestion.getSuggestedCodeEditor().getDocument();
        RangeMarker rangeMarker = document.createRangeMarker(startIndex, endIndex);
        FileModificationSuggestionModification fileModificationSuggestionModification = new FileModificationSuggestionModification(editor, fileModificationSuggestion.getFilePath(), fileModificationSuggestion.getModificationId(), fileModificationSuggestion.getId(), rangeMarker, fileModificationSuggestion.getBeforeCode(), modificationType);
        guardedBlockService.addFileModificationSuggestionModificationGuardedBlock(fileModificationSuggestionModification, startIndex, endIndex);
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModification);
        return fileModificationSuggestionModification;
    }

    @Override
    public void removeModificationSuggestionModification(FileModificationSuggestionModification fileModificationSuggestionModification) {
        guardedBlockService.removeFileModificationSuggestionModificationGuardedBlock(fileModificationSuggestionModification.getId());
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModification);
    }


    public void implementModification(FileModificationSuggestion fileModificationSuggestion, FileModificationSuggestionModification fileModificationSuggestionModification, String modification) {
        RangeMarker rangeMarker = fileModificationSuggestionModification.getRangeMarker();
        if (rangeMarker != null && rangeMarker.isValid()) {
            int formerStartIndex = fileModificationSuggestionModification.getRangeMarker().getStartOffset();
            int formerEndIndex = fileModificationSuggestionModification.getRangeMarker().getEndOffset();
            rangeReplaceService.replaceRange(fileModificationSuggestionModification.getEditor(), formerStartIndex, formerEndIndex, modification);
        }
        diffEditorGeneratorService.updateDiffEditor(fileModificationSuggestion.getDiffEditor(), fileModificationSuggestion.getBeforeCode(), modification);
    }
}
