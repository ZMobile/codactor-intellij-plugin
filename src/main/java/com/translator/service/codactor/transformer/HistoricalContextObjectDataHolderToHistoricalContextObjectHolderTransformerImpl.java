package com.translator.service.codactor.transformer;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.history.data.HistoricalContextObjectDataHolder;

import java.util.ArrayList;
import java.util.List;

public class HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformerImpl implements HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformer {
    @Override
    public HistoricalContextObjectHolder convert(HistoricalContextObjectDataHolder historicalContextObjectDataHolder) {
        return new HistoricalContextObjectHolder(historicalContextObjectDataHolder);
    }

    @Override
    public List<HistoricalContextObjectHolder> convert(List<HistoricalContextObjectDataHolder> historicalContextObjectDataHolders) {
        List<HistoricalContextObjectHolder> historicalContextObjectHolders = new ArrayList<>();
        if (historicalContextObjectDataHolders != null) {
            for (HistoricalContextObjectDataHolder data : historicalContextObjectDataHolders) {
                historicalContextObjectHolders.add(new HistoricalContextObjectHolder(data));
            }
        }
        return historicalContextObjectHolders;
    }
}
