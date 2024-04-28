package com.translator.service.codactor.ai.chat.context;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.history.data.HistoricalObjectDataHolder;

import javax.swing.*;
import java.util.List;

public interface PromptContextService {
    void savePromptContext(List<HistoricalObjectDataHolder> context);

    List<HistoricalObjectDataHolder> getPromptContextData();

    List<HistoricalContextObjectHolder> getPromptContext();

    void clearPromptContext();

    void setStatusLabel(JLabel statusLabel);
}
