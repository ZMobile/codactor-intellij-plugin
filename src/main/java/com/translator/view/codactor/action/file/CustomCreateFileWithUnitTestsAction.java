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
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.ai.modification.AiCodeModificationService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.test.FileCreateWithUnitTestsDialog;
import com.translator.view.codactor.factory.dialog.FileCreateWithUnitTestsDialogFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CustomCreateFileWithUnitTestsAction extends CreateElementActionBase {

    public CustomCreateFileWithUnitTestsAction() {
        super("Create AI File With Unit Tests", "Describe a file to generate it with AI", null);
    }

    @Override
    protected PsiElement @NotNull [] invokeDialog(Project project, PsiDirectory directory) {
        // Show the custom dialog and create the file
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        FileCreateWithUnitTestsDialogFactory fileCreateWithUnitTestsDialogFactory = injector.getInstance(FileCreateWithUnitTestsDialogFactory.class);
        //PromptContextService promptContextService = injector.getInstance(PromptContex7tServiceFactory.class).create();
        FileCreateWithUnitTestsDialog fileCreateWithUnitTestsDialog = fileCreateWithUnitTestsDialogFactory.create(directory/*, promptContextService*/);
        fileCreateWithUnitTestsDialog.setVisible(true);
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
            AiCodeModificationService aiCodeModificationService = injector.getInstance(AiCodeModificationService.class);
            CodactorToolWindowService codactorToolWindowService = injector.getInstance(CodactorToolWindowService.class);
            PromptContextService promptContextService = injector.getInstance(PromptContextService.class);
            String newDescription = "The code for " + fileName + ": " + description;
            aiCodeModificationService.createAndImplementCode(filePath, newDescription, promptContextService.getPromptContext());
            promptContextService.clearPromptContext();
            codactorToolWindowService.openModificationQueueViewerToolWindow();
        }
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        VirtualFile newVirtualFile = localFileSystem.refreshAndFindFileByIoFile(file);
        PsiManager psiManager = PsiManager.getInstance(project);

        return psiManager.findFile(newVirtualFile);
    }
}
