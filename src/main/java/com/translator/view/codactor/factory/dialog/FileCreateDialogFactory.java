package com.translator.view.codactor.factory.dialog;

import com.intellij.psi.PsiDirectory;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.view.codactor.dialog.FileCreateDialog;

public interface FileCreateDialogFactory {
    FileCreateDialog create(PsiDirectory directory, PromptContextService promptContextService);
}
