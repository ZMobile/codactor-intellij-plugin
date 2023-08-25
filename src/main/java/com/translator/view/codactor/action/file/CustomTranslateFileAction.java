package com.translator.view.codactor.action.file;

import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.CodactorInjector;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.view.codactor.dialog.FileTranslateDialog;
import com.translator.view.codactor.factory.dialog.FileTranslateDialogFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomTranslateFileAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        VirtualFile[] virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (virtualFiles == null) {
            return;
        }

        List<VirtualFile> allFiles = new ArrayList<>();
        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.isDirectory()) {
                //Make sure none of the files selected already lie inside of this directory:
                boolean skip = false;
                for (VirtualFile file : virtualFiles) {
                    if (!file.getPath().equals(virtualFile.getPath()) && file.getPath().startsWith(virtualFile.getPath())) {
                        skip = true;
                    }
                }
                if (!skip) {
                    continue;
                }
                collectFiles(virtualFile, allFiles);
            } else {
                if (!allFiles.contains(virtualFile)) {
                    allFiles.add(virtualFile);
                }
            }
        }

        Injector injector = CodactorInjector.getInstance().getInjector(project);
        FileTranslateDialogFactory fileTranslateDialogFactory = injector.getInstance(FileTranslateDialogFactory.class);
        PromptContextService promptContextService = injector.getInstance(PromptContextServiceFactory.class).create();
        FileTranslateDialog fileTranslateDialog = fileTranslateDialogFactory.create(promptContextService, allFiles);
        fileTranslateDialog.setVisible(true);
    }

    private void collectFiles(VirtualFile directory, List<VirtualFile> fileList) {
        for (VirtualFile file : directory.getChildren()) {
            if (file.isDirectory()) {
                collectFiles(file, fileList);
            } else {
                if (!fileList.contains(file)) {
                    fileList.add(file);
                }
            }
        }
    }
}