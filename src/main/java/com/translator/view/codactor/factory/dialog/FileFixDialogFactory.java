package com.translator.view.codactor.factory.dialog;

import com.intellij.openapi.vfs.VirtualFile;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.view.codactor.dialog.FileFixDialog;

import java.util.List;

public interface FileFixDialogFactory {
    FileFixDialog create(PromptContextService promptContextService, List<VirtualFile> selectedFiles);
}
