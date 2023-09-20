package com.translator.view.codactor.action.file;

import com.google.inject.Injector;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.translator.CodactorInjector;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.modification.CodeModificationService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.FileCreateDialog;
import com.translator.view.codactor.factory.dialog.FileCreateDialogFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CustomCreateFileAction extends CreateElementActionBase {

    public CustomCreateFileAction() {
        super("AI Generated File", "Describe a file to generate it with AI", null);
    }

    @Override
    protected PsiElement @NotNull [] invokeDialog(Project project, PsiDirectory directory) {
        // Show the custom dialog and create the file
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        FileCreateDialogFactory fileCreateDialogFactory = injector.getInstance(FileCreateDialogFactory.class);
        PromptContextService promptContextService = injector.getInstance(PromptContextServiceFactory.class).create();
        FileCreateDialog fileCreateDialog = fileCreateDialogFactory.create(directory, promptContextService);
        fileCreateDialog.setVisible(true);
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    protected @NotNull PsiElement @NotNull [] create(@NotNull String newName, @NotNull PsiDirectory directory) throws Exception {
        return new PsiElement[0];
    }

    @Override
    protected @NlsContexts.DialogTitle String getErrorTitle() {
        return null;
    }


    @NotNull
    @Override
    protected String getActionName(PsiDirectory directory, String newName) {
        return "Create Custom File";
    }

    // Create the file using the selected file type, name, and description
    private PsiElement createFile(Project project, String fileName, String description, PsiDirectory directory) {
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
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            CodeModificationService codeModificationService = injector.getInstance(CodeModificationService.class);
            CodactorToolWindowService codactorToolWindowService = injector.getInstance(CodactorToolWindowService.class);
            PromptContextService promptContextService = injector.getInstance(PromptContextService.class);
            String newDescription = "The code for " + fileName + ": " + description;
            codeModificationService.createAndImplementCode(filePath, newDescription, promptContextService.getPromptContext());
            promptContextService.clearPromptContext();
            codactorToolWindowService.openModificationQueueViewerToolWindow();
        }
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        VirtualFile newVirtualFile = localFileSystem.refreshAndFindFileByIoFile(file);
        PsiManager psiManager = PsiManager.getInstance(project);

        return psiManager.findFile(newVirtualFile);
    }
}
