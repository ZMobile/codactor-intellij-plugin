package com.translator.service.codactor.context;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.history.data.HistoricalObjectDataHolder;

import javax.swing.*;
import java.util.List;

public interface PromptContextService {
    void savePromptContext(List<HistoricalObjectDataHolder> context);

    List<HistoricalObjectDataHolder> getPromptContextData();

    List<HistoricalContextObjectHolder> getPromptContext();

    void clearPromptContext();

    void setStatusLabel(JLabel statusLabel);
}
