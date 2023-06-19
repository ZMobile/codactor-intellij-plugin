package com.translator.service.codactor.file;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.modification.AutomaticCodeModificationService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;

import java.io.File;
import java.io.IOException;

public class CodeFileGeneratorServiceImpl implements CodeFileGeneratorService {
    private final Project project;
    private final AutomaticCodeModificationService automaticCodeModificationService;
    private final CodactorToolWindowService codactorToolWindowService;
    private final PromptContextService promptContextService;

    @Inject
    public CodeFileGeneratorServiceImpl(Project project,
                                        AutomaticCodeModificationService automaticCodeModificationService,
                                        CodactorToolWindowService codactorToolWindowService,
                                        @Assisted PromptContextService promptContextService) {
        this.project = project;
        this.automaticCodeModificationService = automaticCodeModificationService;
        this.codactorToolWindowService = codactorToolWindowService;
        this.promptContextService = promptContextService;
    }

    public PsiElement createCodeFile(String fileName, String description, PsiDirectory directory) {
        VirtualFile directoryVirtualFile = directory.getVirtualFile();
        String directoryPath = directoryVirtualFile.getPath();
        String filePath = directoryPath + "/" + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!description.isEmpty()) {
            String newDescription = "The code for " + fileName + ": " + description;
            automaticCodeModificationService.createAndImplementCode(filePath, newDescription, promptContextService.getPromptContext());
            promptContextService.clearPromptContext();
            codactorToolWindowService.openModificationQueueViewerToolWindow();
        }
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        VirtualFile newVirtualFile = localFileSystem.refreshAndFindFileByIoFile(file);
        PsiManager psiManager = PsiManager.getInstance(project);

        return psiManager.findFile(newVirtualFile);
    }
}
