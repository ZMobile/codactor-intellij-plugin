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
import com.translator.service.codactor.context.PromptContextServiceImpl;
import com.translator.service.codactor.file.CodeFileGeneratorService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;
import com.translator.view.codactor.factory.PromptContextBuilderFactory;
import org.jetbrains.annotations.NotNull;

public class CustomCreateMultiFileAction extends CreateElementActionBase {

    public CustomCreateMultiFileAction() {
        super("AI Generated Multi File", "Describe a set of files to generate them with AI", null);
    }

    @Override
    protected PsiElement @NotNull [] invokeDialog(Project project, PsiDirectory directory) {
        // Show the custom dialog and create the file
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        OpenAiModelService openAiModelService = injector.getInstance(OpenAiModelService.class);
        CodactorToolWindowService codactorToolWindowService = injector.getInstance(CodactorToolWindowService.class);
        CodeFileGeneratorService codeFileGeneratorService = injector.getInstance(CodeFileGeneratorService.class);
        PromptContextService promptContextService = new PromptContextServiceImpl();
        PromptContextBuilderFactory promptContextBuilderFactory = injector.getInstance(PromptContextBuilderFactory.class);
        VirtualFile directoryVirtualFile = directory.getVirtualFile();
        MultiFileCreateDialog multiFileCreateDialog = new MultiFileCreateDialog(directoryVirtualFile.getPath(), null, openAiModelService, codactorToolWindowService, codeFileGeneratorService, promptContextService, promptContextBuilderFactory);
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
