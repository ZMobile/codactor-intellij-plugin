package com.translator.service.codactor.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.ComboBox;
import com.translator.service.codactor.ide.editor.EditorService;

import javax.inject.Inject;

public class ModificationTypeComboBoxServiceImpl implements ModificationTypeComboBoxService {
    private final ComboBox<String> modificationTypeComboBox;
    private final EditorService editorService;

    @Inject
    public ModificationTypeComboBoxServiceImpl(EditorService editorService) {
        this.modificationTypeComboBox = new ComboBox<>(new String[]{"Modify", "Modify Selected", "Fix", "Fix Selected", "Create", "Create Files", "Inquire", "Inquire Selected", "Translate"});
        this.editorService = editorService;
    }

    @Override
    public ComboBox<String> getModificationTypeComboBox() {
        return modificationTypeComboBox;
    }

    @Override
    public void addSelectionListenersToAllOpenEditors() {
        for (Editor editor : editorService.getAllEditors()) {
            editor.getSelectionModel().addSelectionListener(new ModificationTypeSelectionListener(this));
        }
    }
}
