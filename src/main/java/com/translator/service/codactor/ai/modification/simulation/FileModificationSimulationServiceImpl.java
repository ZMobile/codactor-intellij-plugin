package com.translator.service.codactor.ai.modification.simulation;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.RangeReplaceService;

import javax.inject.Inject;

public class FileModificationSimulationServiceImpl implements FileModificationSimulationService {
    private final FileModificationTrackerService fileModificationTrackerService;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final RangeReplaceService rangeReplaceService;

    @Inject
    public FileModificationSimulationServiceImpl(FileModificationTrackerService fileModificationTrackerService,
                                                 CodeSnippetExtractorService codeSnippetExtractorService,
                                                 RangeReplaceService rangeReplaceService) {
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.rangeReplaceService = rangeReplaceService;
    }

    @Override
    public String simulateFileModification(String modificationId) {
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        if (fileModification == null) {
            return null;
        }
        String code = codeSnippetExtractorService.getAllText(fileModification.getFilePath());
        return rangeReplaceService.replaceRange(code, fileModification.getRangeMarker().getStartOffset(), fileModification.getRangeMarker().getEndOffset(), fileModification.getModificationOptions().get(0).getSuggestedCode());
    }
}
