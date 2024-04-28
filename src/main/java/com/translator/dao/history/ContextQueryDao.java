package com.translator.dao.history;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;

import java.util.List;

public interface ContextQueryDao {
    HistoricalContextObjectHolder queryHistoricalContextObject(HistoricalContextObjectHolder historicalContextObjectHolder);

    List<HistoricalContextObjectHolder> queryHistoricalContextObjects(List<HistoricalContextObjectHolder> historicalContextObjectHolderList);
}
