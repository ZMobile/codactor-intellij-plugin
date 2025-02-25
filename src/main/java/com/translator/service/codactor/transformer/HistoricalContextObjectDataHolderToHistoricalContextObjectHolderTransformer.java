package com.translator.service.codactor.transformer;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.history.data.HistoricalObjectDataHolder;

import java.util.List;

public interface HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformer {
    HistoricalContextObjectHolder convert(HistoricalObjectDataHolder historicalObjectDataHolder);

    List<HistoricalContextObjectHolder> convert(List<HistoricalObjectDataHolder> historicalObjectDataHolders);
}
