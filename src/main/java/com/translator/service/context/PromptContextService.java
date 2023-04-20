package com.translator.service.context;

import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.view.viewer.InquiryViewer;
import com.translator.view.viewer.ModificationQueueViewer;

import javax.swing.*;
import java.util.List;

public interface PromptContextService {
    void savePromptContext(List<HistoricalContextObjectDataHolder> context);

    List<HistoricalContextObjectDataHolder> getPromptContext();

    void clearPromptContext();

    void setStatusLabel(JLabel statusLabel);
}
