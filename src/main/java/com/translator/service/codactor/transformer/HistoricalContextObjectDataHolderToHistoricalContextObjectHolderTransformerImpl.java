package com.translator.service.codactor.transformer;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.history.data.HistoricalObjectDataHolder;

import java.util.ArrayList;
import java.util.List;

public class HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformerImpl implements HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformer {
    @Override
    public HistoricalContextObjectHolder convert(HistoricalObjectDataHolder historicalObjectDataHolder) {
        return new HistoricalContextObjectHolder(historicalObjectDataHolder);
    }

    @Override
    public List<HistoricalContextObjectHolder> convert(List<HistoricalObjectDataHolder> historicalObjectDataHolders) {
        List<HistoricalContextObjectHolder> historicalContextObjectHolders = new ArrayList<>();
        if (historicalObjectDataHolders != null) {
            for (HistoricalObjectDataHolder data : historicalObjectDataHolders) {
                historicalContextObjectHolders.add(new HistoricalContextObjectHolder(data));
            }
        }
        return historicalContextObjectHolders;
    }
}
