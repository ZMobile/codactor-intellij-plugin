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
import com.translator.view.codactor.dialog.FileFixDialog;
import com.translator.view.codactor.factory.dialog.FileFixDialogFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomFixFileAction extends AnAction {
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
        Set<String> selectedFilePaths = Arrays.stream(virtualFiles).map(VirtualFile::getPath).collect(Collectors.toSet());

        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.isDirectory()) {
                boolean anyFileInsideSelected = false;
                for (VirtualFile file : virtualFiles) {
                    if (!file.getPath().equals(virtualFile.getPath()) && file.getPath().startsWith(virtualFile.getPath())) {
                        anyFileInsideSelected = true;
                        break;
                    }
                }
                if (!anyFileInsideSelected) {
                    collectFiles(virtualFile, allFiles, selectedFilePaths);
                }
            } else {
                if (!allFiles.contains(virtualFile)) {
                    allFiles.add(virtualFile);
                }
            }
        }

        Injector injector = CodactorInjector.getInstance().getInjector(project);
        FileFixDialogFactory fileFixDialogFactory = injector.getInstance(FileFixDialogFactory.class);
        PromptContextService promptContextService = injector.getInstance(PromptContextServiceFactory.class).create();
        FileFixDialog fileFixDialog = fileFixDialogFactory.create(promptContextService, Arrays.asList(virtualFiles));
        fileFixDialog.setVisible(true);
    }

    private void collectFiles(VirtualFile directory, List<VirtualFile> fileList, Set<String> selectedFilePaths) {
        for (VirtualFile file : directory.getChildren()) {
            if (file.isDirectory()) {
                collectFiles(file, fileList, selectedFilePaths);
            } else {
                if (!fileList.contains(file) && !selectedFilePaths.contains(file.getPath())) {
                    fileList.add(file);
                }
            }
        }
    }
}
