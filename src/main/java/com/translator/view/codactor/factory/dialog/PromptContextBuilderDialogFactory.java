package com.translator.view.codactor.factory.dialog;

import com.translator.view.codactor.dialog.PromptContextBuilderDialog;
import com.translator.service.codactor.context.PromptContextService;

public interface PromptContextBuilderDialogFactory {
    PromptContextBuilderDialog create(PromptContextService promptContextService);
}
