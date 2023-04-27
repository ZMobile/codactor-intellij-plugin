package com.translator.service.modification;

import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.modification.ModificationType;
import com.translator.service.context.PromptContextService;
import com.translator.service.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;

public class AutomaticMassCodeModificationServiceImpl implements AutomaticMassCodeModificationService {
    private Project project;
    private AutomaticCodeModificationService automaticCodeModificationService;
    private FileModificationTrackerService fileModificationTrackerService;

    @Inject
    public AutomaticMassCodeModificationServiceImpl(Project project,
                                                AutomaticCodeModificationServiceFactory automaticCodeModificationServiceFactory,
                                                @Assisted PromptContextService promptContextService,
                                                    FileModificationTrackerService fileModificationTrackerService) {
        this.project = project;
        this.automaticCodeModificationService = automaticCodeModificationServiceFactory.create(promptContextService);
        this.fileModificationTrackerService = fileModificationTrackerService;
    }

    @Override
    public void getModifiedCode(List<String> filePaths, String modification) {
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(modification);
        for (String filePath : filePaths) {
            //Get the document to find the start and end index. It needs to be some read action
            ApplicationManager.getApplication().invokeLater(() -> {
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);

                if (virtualFile != null) {
                    // Get the Document instance corresponding to the VirtualFile
                    Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

                    if (document != null) {
                        String code = document.getText();
                        int startIndex = 0;
                        int endIndex = code.length();
                        automaticCodeModificationService.getModifiedCode(filePath, startIndex, endIndex, modification, ModificationType.MODIFY);
                    }
                }
            });
            try {
                Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interruption
                e.printStackTrace();
            }
        }
        fileModificationTrackerService.removeMultiFileModification(multiFileModificationId);
    }

    @Override
    public void getFixedCode(List<String> filePaths, String error) {
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(error);
        for (String filePath : filePaths) {
            //Get the document to find the start and end index. It needs to be some read action
            ApplicationManager.getApplication().invokeLater(() -> {
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);

                if (virtualFile != null) {
                    // Get the Document instance corresponding to the VirtualFile
                    Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

                    if (document != null) {
                        String code = document.getText();
                        int startIndex = 0;
                        int endIndex = code.length();
                        automaticCodeModificationService.getModifiedCode(filePath, startIndex, endIndex, error, ModificationType.FIX);
                    }
                }
            });
            try {
                Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interruption
                e.printStackTrace();
            }
        }
        fileModificationTrackerService.removeMultiFileModification(multiFileModificationId);
    }

    @Override
    public void getTranslatedCode(List<String> filePaths, String newLanguage, String newFileType) {
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification("Translate files to " + newLanguage);
        for (String filePath : filePaths) {
            //Get the document to find the start and end index. It needs to be some read action
            ApplicationManager.getApplication().invokeLater(() -> {
                automaticCodeModificationService.getTranslatedCode(filePath, newLanguage, newFileType);
            });
            try {
                Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interruption
                e.printStackTrace();
            }
        }
        fileModificationTrackerService.removeMultiFileModification(multiFileModificationId);
    }
}
