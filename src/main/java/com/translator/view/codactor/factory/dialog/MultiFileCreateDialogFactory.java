package com.translator.view.codactor.factory.dialog;

import com.translator.service.codactor.context.PromptContextService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;

public interface MultiFileCreateDialogFactory {
    MultiFileCreateDialog create(String filePath, String description, PromptContextService promptContextService);
}
