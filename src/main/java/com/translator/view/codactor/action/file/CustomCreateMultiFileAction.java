package com.translator.view.codactor.action.file;

import com.google.inject.Injector;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.translator.CodactorInjector;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import org.jetbrains.annotations.NotNull;

public class CustomCreateMultiFileAction extends CreateElementActionBase {

    public CustomCreateMultiFileAction() {
        super("AI Generated Multi File", "Describe a set of files to generate them with AI", null);
    }

    @Override
    protected PsiElement @NotNull [] invokeDialog(Project project, PsiDirectory directory) {
        // Show the custom dialog and create the file
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        MultiFileCreateDialogFactory multiFileCreateDialogFactory = injector.getInstance(MultiFileCreateDialogFactory.class);
        PromptContextService promptContextService = injector.getInstance(PromptContextServiceFactory.class).create();
        OpenAiModelService openAiModelService = injector.getInstance(OpenAiModelService.class);
        VirtualFile directoryVirtualFile = directory.getVirtualFile();
        MultiFileCreateDialog multiFileCreateDialog = multiFileCreateDialogFactory.create(directoryVirtualFile.getPath(), "", promptContextService, openAiModelService);
        multiFileCreateDialog.setVisible(true);

        return PsiElement.EMPTY_ARRAY;
    }

    // (Add other required methods here)

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
}
