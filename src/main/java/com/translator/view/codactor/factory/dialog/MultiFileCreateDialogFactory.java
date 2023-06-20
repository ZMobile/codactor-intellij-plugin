package com.translator.view.codactor.factory.dialog;

import com.google.inject.assistedinject.Assisted;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;

public interface MultiFileCreateDialogFactory {
    MultiFileCreateDialog create(@Assisted("filePath") String filePath, @Assisted("description") String description, PromptContextService promptContextService, OpenAiModelService openAiModelService);
}
