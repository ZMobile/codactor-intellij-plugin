package com.translator.view.codactor.factory.dialog;

import com.intellij.openapi.vfs.VirtualFile;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.view.codactor.dialog.FileTranslateDialog;

import java.util.List;

public interface FileTranslateDialogFactory {
    FileTranslateDialog create(PromptContextService promptContextService, List<VirtualFile> selectedFiles);
}
