package com.translator.service.codactor.transformer.modification;

import com.google.inject.Inject;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationTracker;
import com.translator.model.codactor.modification.data.FileModificationRangeData;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.editor.CodeSnippetIndexGetterService;

import java.util.ArrayList;
import java.util.List;

public class FileModificationTrackerToFileModificationRangeDataTransformerServiceImpl implements FileModificationTrackerToFileModificationRangeDataTransformerService {
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;

    @Inject
    public FileModificationTrackerToFileModificationRangeDataTransformerServiceImpl(CodeSnippetExtractorService codeSnippetExtractorService, CodeSnippetIndexGetterService codeSnippetIndexGetterService) {
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
    }

    @Override
    public List<FileModificationRangeData> convert(FileModificationTracker fileModificationTracker) {
        List<FileModification> fileModifications = fileModificationTracker.getModifications();
        List<FileModificationRangeData> fileModificationRangeDataList = new ArrayList<>();
        for (FileModification fileModification : fileModifications) {
            String filePath = fileModificationTracker.getFilePath();
            int startLine = codeSnippetIndexGetterService.getLineAtIndexInFilePath(filePath, fileModification.getRangeMarker().getStartOffset());
            String startRangeCode = codeSnippetExtractorService.getCurrentAndNextLineCodeAfterIndex(filePath, fileModification.getRangeMarker().getStartOffset());
            int endLine = codeSnippetIndexGetterService.getLineAtIndexInFilePath(filePath, fileModification.getRangeMarker().getEndOffset());
            String endRangeCode = codeSnippetExtractorService.getCurrentAndNextLineCodeAfterIndex(filePath, fileModification.getRangeMarker().getEndOffset());
            FileModificationRangeData fileModificationRangeData = new FileModificationRangeData(fileModification.getId(), fileModification.getSubjectLine(), startLine, startRangeCode, endLine, endRangeCode);
            fileModificationRangeDataList.add(fileModificationRangeData);
        }
        return fileModificationRangeDataList;
    }
}
