package com.translator.service.codactor.transformer.modification;

import com.google.inject.Inject;
import com.translator.model.codactor.ai.history.data.HistoricalFileModificationDataHolder;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.RecordType;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;

import java.util.ArrayList;
import java.util.List;

public class HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerServiceImpl implements HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerService {
    private final FileModificationSuggestionRecordToFileModificationTransformerService fileModificationSuggestionRecordToFileModificationTransformerService;
    private final FileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService fileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService;

    @Inject
    public HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerServiceImpl(FileModificationSuggestionRecordToFileModificationTransformerService fileModificationSuggestionRecordToFileModificationTransformerService, FileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService fileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService) {
        this.fileModificationSuggestionRecordToFileModificationTransformerService = fileModificationSuggestionRecordToFileModificationTransformerService;
        this.fileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService = fileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService;
    }

    @Override
    public FileModificationDataHolder convert(HistoricalFileModificationDataHolder historicalFileModificationDataHolder) {
        if (historicalFileModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
            FileModification fileModification = fileModificationSuggestionRecordToFileModificationTransformerService.convert(historicalFileModificationDataHolder.getFileModificationSuggestionRecord());
            return new FileModificationDataHolder(fileModification);
        } else {
            FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService.convert(historicalFileModificationDataHolder.getFileModificationSuggestionModificationRecord());
            return new FileModificationDataHolder(fileModificationSuggestionModification);
        }
    }

    @Override
    public List<FileModificationDataHolder> convert(List<HistoricalFileModificationDataHolder> historicalFileModificationDataHolders) {
        List<FileModificationDataHolder> fileModificationDataHolders = new ArrayList<>();
        for (HistoricalFileModificationDataHolder historicalFileModificationDataHolder : historicalFileModificationDataHolders) {
            fileModificationDataHolders.add(convert(historicalFileModificationDataHolder));
        }
        return fileModificationDataHolders;
    }
}
