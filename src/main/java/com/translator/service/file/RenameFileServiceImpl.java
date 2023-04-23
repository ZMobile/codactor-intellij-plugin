package com.translator.service.file;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.RenameRefactoring;

import javax.inject.Inject;

public class RenameFileServiceImpl implements RenameFileService {
    private Project project;

    @Inject
    public RenameFileServiceImpl(Project project) {
        this.project = project;
    }

    public void renameFile(String filePath, String newFileName) {
        // Get VirtualFile from file path
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (virtualFile == null) {
            throw new IllegalStateException("File not found: " + filePath);
        }

        // Convert VirtualFile to PsiFile
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile psiFile = psiManager.findFile(virtualFile);

        if (psiFile == null) {
            return;
        }

        // Perform the rename operation
        ApplicationManager.getApplication().invokeLater(() -> {
            // Create the rename refactoring
            RefactoringFactory refactoringFactory = RefactoringFactory.getInstance(project);
            RenameRefactoring renameRefactoring = refactoringFactory.createRename(psiFile, newFileName);

            // Execute the refactoring operation using a TransactionGuard
            TransactionGuard.getInstance().submitTransactionAndWait(() -> {
                renameRefactoring.run();
            });
        });
    }
}
