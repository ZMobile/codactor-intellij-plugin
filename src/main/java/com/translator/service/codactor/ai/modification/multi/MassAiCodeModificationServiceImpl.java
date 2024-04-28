package com.translator.service.codactor.ai.modification.multi;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.service.codactor.ai.modification.AiCodeModificationService;

import javax.inject.Inject;
import java.util.List;

public class MassAiCodeModificationServiceImpl implements MassAiCodeModificationService {
    private Project project;
    private AiCodeModificationService aiCodeModificationService;
    private FileModificationManagementService fileModificationManagementService;

    @Inject
    public MassAiCodeModificationServiceImpl(Project project,
                                             AiCodeModificationService aiCodeModificationService,
                                             FileModificationManagementService fileModificationManagementService) {
        this.project = project;
        this.aiCodeModificationService = aiCodeModificationService;
        this.fileModificationManagementService = fileModificationManagementService;
    }

    @Override
    public void getModifiedCode(List<String> filePaths, String modification, List<HistoricalContextObjectHolder> priorContext) {
        String multiFileModificationId = fileModificationManagementService.addMultiFileModification(modification);
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
                        aiCodeModificationService.getModifiedCode(filePath, startIndex, endIndex, modification, ModificationType.MODIFY, priorContext);
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
        fileModificationManagementService.removeMultiFileModification(multiFileModificationId);
    }

    @Override
    public void getFixedCode(List<String> filePaths, String error, List<HistoricalContextObjectHolder> priorContext) {
        String multiFileModificationId = fileModificationManagementService.addMultiFileModification(error);
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
                        aiCodeModificationService.getModifiedCode(filePath, startIndex, endIndex, error, ModificationType.FIX, priorContext);
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
        fileModificationManagementService.removeMultiFileModification(multiFileModificationId);
    }

    @Override
    public void getTranslatedCode(List<String> filePaths, String newLanguage, String newFileType, List<HistoricalContextObjectHolder> priorContext) {
        String multiFileModificationId = fileModificationManagementService.addMultiFileModification("Translate files to " + newLanguage);
        for (String filePath : filePaths) {
            //Get the document to find the start and end index. It needs to be some read action
            ApplicationManager.getApplication().invokeLater(() -> {
                aiCodeModificationService.getTranslatedCode(filePath, newLanguage, newFileType, priorContext);
            });
            try {
                Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interruption
                e.printStackTrace();
            }
        }
        fileModificationManagementService.removeMultiFileModification(multiFileModificationId);
    }
}
