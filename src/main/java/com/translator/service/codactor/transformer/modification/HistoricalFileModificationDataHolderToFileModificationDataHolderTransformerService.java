package com.translator.service.codactor.transformer.modification;

import com.translator.model.codactor.ai.history.data.HistoricalFileModificationDataHolder;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;

import java.util.List;

public interface HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerService {
    FileModificationDataHolder convert(HistoricalFileModificationDataHolder historicalFileModificationDataHolder);

    List<FileModificationDataHolder> convert(List<HistoricalFileModificationDataHolder> historicalFileModificationDataHolders);
}
