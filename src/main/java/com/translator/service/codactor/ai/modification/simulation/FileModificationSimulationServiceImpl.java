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
    public String simulateFileModification(String modificationId, String suggestedCode) {
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        if (fileModification == null) {
            return null;
        }
        String code = codeSnippetExtractorService.getAllText(fileModification.getFilePath());
        System.out.println("code: " + code);
        System.out.println("Code length: " + code.length());
        System.out.println("start: " + fileModification.getRangeMarker().getStartOffset());
        System.out.println("end: " + fileModification.getRangeMarker().getEndOffset());
        System.out.println("Replacing...." );
        System.out.println("suggested code: " + suggestedCode);
        String replacement =  rangeReplaceService.replaceRange(code, fileModification.getRangeMarker().getStartOffset(), fileModification.getRangeMarker().getEndOffset(), suggestedCode);
        System.out.println("replacement: " + replacement);
        return replacement;
    }
}
