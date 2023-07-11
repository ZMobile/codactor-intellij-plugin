package com.translator.service.codactor.transformer.modification;

import com.translator.model.codactor.history.data.HistoricalFileModificationDataHolder;
import com.translator.model.codactor.modification.data.FileModificationDataHolder;

import java.util.List;

public interface HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerService {
    FileModificationDataHolder convert(HistoricalFileModificationDataHolder historicalFileModificationDataHolder);

    List<FileModificationDataHolder> convert(List<HistoricalFileModificationDataHolder> historicalFileModificationDataHolders);
}
