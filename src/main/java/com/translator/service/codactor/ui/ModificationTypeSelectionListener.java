package com.translator.service.codactor.ui;

import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.NotNull;

public class ModificationTypeSelectionListener implements SelectionListener {
    private ModificationTypeComboBoxService modificationTypeComboBoxService;

    public ModificationTypeSelectionListener(ModificationTypeComboBoxService modificationTypeComboBoxService) {
        this.modificationTypeComboBoxService = modificationTypeComboBoxService;
    }

    @Override
    public void selectionChanged(@NotNull SelectionEvent e) {
        ComboBox<String> modificationTypeComboBox = modificationTypeComboBoxService.getModificationTypeComboBox();
        String selectedItem = (String) modificationTypeComboBox.getSelectedItem();

        if (!e.getNewRange().isEmpty()) {
            // This is when some text selected
            if ("Modify".equals(selectedItem)) {
                modificationTypeComboBox.setSelectedItem("Modify Selected");
            } else if ("Fix".equals(selectedItem)) {
                modificationTypeComboBox.setSelectedItem("Fix Selected");
            } else if ("Inquire".equals(selectedItem)) {
                modificationTypeComboBox.setSelectedItem("Inquire Selected");
            }
        } else {
            // This is when selection is removed
            if ("Modify Selected".equals(selectedItem)) {
                modificationTypeComboBox.setSelectedItem("Modify");
            } else if ("Fix Selected".equals(selectedItem)) {
                modificationTypeComboBox.setSelectedItem("Fix");
            } else if ("Inquire Selected".equals(selectedItem)) {
                modificationTypeComboBox.setSelectedItem("Inquire");
            }
        }
        SelectionListener.super.selectionChanged(e);
    }
}
