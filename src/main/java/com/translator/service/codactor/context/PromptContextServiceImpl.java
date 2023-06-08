package com.translator.service.codactor.context;

import com.translator.model.codactor.history.data.HistoricalContextObjectDataHolder;

import javax.inject.Inject;
import javax.swing.*;
import java.util.List;

public class PromptContextServiceImpl implements PromptContextService {
    private List<HistoricalContextObjectDataHolder> context;
    private JLabel statusLabel;

    @Inject
    public PromptContextServiceImpl() {
        this.context = null;
        this.statusLabel = new JLabel();
        updateStatusLabel();
    }

    @Override
    public void savePromptContext(List<HistoricalContextObjectDataHolder> context) {
        this.context = context;
        updateStatusLabel();
    }

    @Override
    public List<HistoricalContextObjectDataHolder> getPromptContext() {
        if (context == null) {
            return null;
        }
        if (context.isEmpty()) {
            return null;
        }
        return context;
    }

    @Override
    public void clearPromptContext() {
        this.context = null;
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        if (context == null || context.isEmpty()) {
            statusLabel.setVisible(false);
        } else {
            statusLabel.setVisible(true);
            if (context.size() == 1) {
                statusLabel.setText("<" + context.size() + " Context Object Added> ");
            } else {
                statusLabel.setText("<" + context.size() + " Context Objects Added> ");
            }
        }
    }

    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
        updateStatusLabel();
    }
}
