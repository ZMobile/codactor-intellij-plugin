package com.translator.service.codactor.transformer;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.history.data.HistoricalContextObjectDataHolder;

import java.util.List;

public interface HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformer {
    HistoricalContextObjectHolder convert(HistoricalContextObjectDataHolder historicalContextObjectDataHolder);

    List<HistoricalContextObjectHolder> convert(List<HistoricalContextObjectDataHolder> historicalContextObjectDataHolders);
}
