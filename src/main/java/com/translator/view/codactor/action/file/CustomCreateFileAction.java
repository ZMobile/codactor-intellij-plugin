package com.translator.view.codactor.action.file;

import com.google.inject.Injector;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.translator.CodactorInjector;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.context.PromptContextServiceImpl;
import com.translator.service.codactor.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.codactor.modification.AutomaticCodeModificationService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.FileCreateDialog;
import com.translator.view.codactor.factory.PromptContextBuilderFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class CustomCreateFileAction extends CreateElementActionBase {
    private PromptContextService promptContextService;

    public CustomCreateFileAction() {
        super("AI Generated File", "Describe a file to generate it with AI", null);
    }

    @Override
    protected PsiElement @NotNull [] invokeDialog(Project project, PsiDirectory directory) {
        // Show the custom dialog and create the file
        promptContextService = new PromptContextServiceImpl();
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        OpenAiModelService openAiModelService = injector.getInstance(OpenAiModelService.class);
        PromptContextService promptContextService = new PromptContextServiceImpl();
        PromptContextBuilderFactory promptContextBuilderFactory = injector.getInstance(PromptContextBuilderFactory.class);
        ActionListener okActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton okButton = (JButton) e.getSource();
                FileCreateDialog fileCreateDialog = (FileCreateDialog) SwingUtilities.getWindowAncestor(okButton);

                String fileName = fileCreateDialog.getFileNameInput().getText();
                if (!fileName.contains(".") || fileName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(fileCreateDialog, "Please enter a valid file name with an extension.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String fileDescription = fileCreateDialog.getFileDescription().getText();
                    PsiElement createdElement = createFile(project, fileName, fileDescription, directory);
                    if (createdElement != null) {
                        if (createdElement instanceof PsiFile) {
                            FileEditorManager.getInstance(project).openFile(((PsiFile) createdElement).getVirtualFile(), true);
                        }
                        fileCreateDialog.setVisible(false);
                    }
                }
            }
        };

        FileCreateDialog fileCreateDialog = new FileCreateDialog(openAiModelService, promptContextService, promptContextBuilderFactory, okActionListener);
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
    }
}
