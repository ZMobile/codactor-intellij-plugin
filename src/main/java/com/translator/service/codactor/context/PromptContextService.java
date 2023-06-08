package com.translator.service.codactor.context;

import com.translator.model.codactor.history.data.HistoricalContextObjectDataHolder;

import javax.swing.*;
import java.util.List;

public interface PromptContextService {
    void savePromptContext(List<HistoricalContextObjectDataHolder> context);

    List<HistoricalContextObjectDataHolder> getPromptContext();

    void clearPromptContext();

    void setStatusLabel(JLabel statusLabel);
}
