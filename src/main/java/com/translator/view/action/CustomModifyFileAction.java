package com.translator.view.action;

import com.google.inject.Injector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.CodactorInjector;
import com.translator.service.context.PromptContextServiceImpl;
import com.translator.service.factory.AutomaticMassCodeModificationServiceFactory;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.dialog.FileModifyDialog;
import com.translator.view.factory.PromptContextBuilderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomModifyFileAction extends AnAction {
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
                collectFiles(virtualFile, allFiles);
            } else {
                if (!allFiles.contains(virtualFile)) {
                    allFiles.add(virtualFile);
                }
            }
        }

        Injector injector = CodactorInjector.getInstance().getInjector(project);
        CodactorToolWindowService codactorToolWindowService = injector.getInstance(CodactorToolWindowService.class);
        PromptContextBuilderFactory promptContextBuilderFactory = injector.getInstance(PromptContextBuilderFactory.class);
        AutomaticMassCodeModificationServiceFactory automaticMassCodeModificationServiceFactory = injector.getInstance(AutomaticMassCodeModificationServiceFactory.class);
        OpenAiModelService openAiModelService = injector.getInstance(OpenAiModelService.class);

        // Show the custom dialog and modify the selected files
        FileModifyDialog fileModifyDialog = new FileModifyDialog(project, codactorToolWindowService, new PromptContextServiceImpl(), promptContextBuilderFactory, automaticMassCodeModificationServiceFactory, openAiModelService, allFiles);
        fileModifyDialog.setVisible(true);
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