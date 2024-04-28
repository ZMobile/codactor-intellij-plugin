package com.translator.view.codactor.factory.dialog;

import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.view.codactor.dialog.PromptContextBuilderDialog;

public interface PromptContextBuilderDialogFactory {
    PromptContextBuilderDialog create(PromptContextService promptContextService);
}
