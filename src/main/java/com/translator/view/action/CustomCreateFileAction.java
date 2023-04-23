package com.translator.view.action;

import com.google.inject.Injector;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.translator.CodactorInjector;
import com.translator.service.context.PromptContextService;
import com.translator.service.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.modification.AutomaticCodeModificationService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.dialog.FileCreateDialog;
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
        FileCreateDialog fileCreateDialog = new FileCreateDialog();
        fileCreateDialog.show();

        if (fileCreateDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            String fileName = fileCreateDialog.getFileNameInput().getText();
            String fileDescription = fileCreateDialog.getFileDescription().getText();
            PsiElement createdElement = createFile(project, fileName, fileDescription, directory);
            if (createdElement != null) {
                return new PsiElement[]{createdElement};
            }
        }

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
            PromptContextService promptContextService = injector.getInstance(PromptContextService.class);
            AutomaticCodeModificationServiceFactory automaticCodeModificationServiceFactory = injector.getInstance(AutomaticCodeModificationServiceFactory.class);
            CodactorToolWindowService codactorToolWindowService = injector.getInstance(CodactorToolWindowService.class);
            AutomaticCodeModificationService automaticCodeModificationService = automaticCodeModificationServiceFactory.create(promptContextService);
            String newDescription = "The code for " + fileName + ": " + description;
            automaticCodeModificationService.createAndImplementCode(filePath, newDescription);
            codactorToolWindowService.openModificationQueueViewerToolWindow();
        }
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        VirtualFile newVirtualFile = localFileSystem.refreshAndFindFileByIoFile(file);
        PsiManager psiManager = PsiManager.getInstance(project);

        return psiManager.findFile(newVirtualFile);
        // You will need to create a custom implementation of the handler to process the description
    }
}
