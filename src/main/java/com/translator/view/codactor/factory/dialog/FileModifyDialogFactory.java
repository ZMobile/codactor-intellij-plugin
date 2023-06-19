package com.translator.view.codactor.factory.dialog;

import com.intellij.openapi.vfs.VirtualFile;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.view.codactor.dialog.FileModifyDialog;

import java.util.List;

public interface FileModifyDialogFactory {
    FileModifyDialog create(PromptContextService promptContextService, List<VirtualFile> selectedFiles);
}
