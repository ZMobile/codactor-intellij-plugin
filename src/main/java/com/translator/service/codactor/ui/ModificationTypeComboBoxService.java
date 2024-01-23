package com.translator.service.codactor.ui;

import com.intellij.openapi.ui.ComboBox;

public interface ModificationTypeComboBoxService {
    ComboBox<String> getModificationTypeComboBox();

    void addSelectionListenersToAllOpenEditors();
}
