package com.translator.service.codactor.ai.modification;

import com.google.inject.Inject;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.model.codactor.io.CancellableRunnable;
import com.translator.model.codactor.io.CustomBackgroundTask;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.openai.connection.AzureConnectionService;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.service.codactor.io.BackgroundTaskMapperService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;

import java.util.ArrayList;
import java.util.List;

public class AiFileModificationRestarterServiceImpl implements AiFileModificationRestarterService {
    private final Project project;
    private final CodeModificationDao codeModificationDao;
    private final FirebaseTokenService firebaseTokenService;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final DefaultConnectionService defaultConnectionService;
    private final OpenAiModelService openAiModelService;
    private final BackgroundTaskMapperService backgroundTaskMapperService;
    private final AzureConnectionService azureConnectionService;
    private final FileModificationErrorDialogFactory fileModificationErrorDialogFactory;

    @Inject
    public AiFileModificationRestarterServiceImpl(Project project,
                                                  CodeModificationDao codeModificationDao,
                                                  FirebaseTokenService firebaseTokenService,
                                                  FileModificationTrackerService fileModificationTrackerService,
                                                  DefaultConnectionService defaultConnectionService,
                                                  OpenAiModelService openAiModelService,
                                                  BackgroundTaskMapperService backgroundTaskMapperService,
                                                  AzureConnectionService azureConnectionService,
                                                  FileModificationErrorDialogFactory fileModificationErrorDialogFactory) {
        this.project = project;
        this.codeModificationDao = codeModificationDao;
        this.firebaseTokenService = firebaseTokenService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.defaultConnectionService = defaultConnectionService;
        this.openAiModelService = openAiModelService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.azureConnectionService = azureConnectionService;
        this.fileModificationErrorDialogFactory = fileModificationErrorDialogFactory;
    }

    @Override
    public void restartFileModification(FileModification fileModification) {
        String model = openAiModelService.getSelectedOpenAiModel();
        fileModificationTrackerService.undoReadyFileModification(fileModification.getId());
        fileModificationTrackerService.retryFileModification(fileModification.getId());
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            List<FileModificationSuggestionRecord> fileModificationSuggestionRecords = null;
            String error = null;
            if (fileModification.getModificationType() == ModificationType.MODIFY || fileModification.getModificationType() == ModificationType.MODIFY_SELECTION) {
                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModification.getFilePath(), fileModification.getBeforeText(), fileModification.getModification(), fileModification.getModificationType(), openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), fileModification.getPriorContext());
                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getModifiedCode(desktopCodeModificationRequestResource);
                if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                    fileModificationSuggestionRecords = desktopCodeModificationResponseResource.getModificationSuggestions();
                } else {
                    error = desktopCodeModificationResponseResource.getError();
                }
            } else if (fileModification.getModificationType() == ModificationType.FIX || fileModification.getModificationType() == ModificationType.FIX_SELECTION) {
                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModification.getFilePath(), fileModification.getBeforeText(), fileModification.getModification(), fileModification.getModificationType(), openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), fileModification.getPriorContext());
                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getFixedCode(desktopCodeModificationRequestResource);
                if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
                    fileModificationSuggestionRecords = desktopCodeModificationResponseResource.getModificationSuggestions();
                } else {
                    error = desktopCodeModificationResponseResource.getError();
                }
            } else if (fileModification.getModificationType() == ModificationType.CREATE) {
                DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(fileModification.getFilePath(), fileModification.getModification(), openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), fileModification.getPriorContext());
                DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
                if (desktopCodeCreationResponseResource.getModificationSuggestions() != null && !desktopCodeCreationResponseResource.getModificationSuggestions().isEmpty()) {
                    fileModificationSuggestionRecords = desktopCodeCreationResponseResource.getModificationSuggestions();
                } else {
                    error = desktopCodeCreationResponseResource.getError();
                }
            } else if (fileModification.getModificationType() == ModificationType.TRANSLATE) {
                DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource = new DesktopCodeTranslationRequestResource(fileModification.getFilePath(), fileModification.getBeforeText(), fileModification.getNewLanguage(), fileModification.getNewFileType(), openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), fileModification.getPriorContext());
                DesktopCodeTranslationResponseResource desktopCodeTranslationResponseResource = codeModificationDao.getTranslatedCode(desktopCodeTranslationRequestResource);
                if (desktopCodeTranslationResponseResource.getModificationSuggestions() != null && !desktopCodeTranslationResponseResource.getModificationSuggestions().isEmpty()) {
                    fileModificationSuggestionRecords = desktopCodeTranslationResponseResource.getModificationSuggestions();
                } else {
                    error = desktopCodeTranslationResponseResource.getError();
                }
            } else if (fileModification.getModificationType() == ModificationType.DELETE) {
                fileModification.setFileDeletionAtFilePathOnAcceptance(true);
                fileModificationSuggestionRecords = new ArrayList<>();
                FileModificationSuggestionRecord fileModificationSuggestionRecord = new FileModificationSuggestionRecord(firebaseTokenService.getFirebaseToken().getUserId(), fileModification.getId(), ModificationType.DELETE, fileModification.getFilePath(), "Delete this code file", fileModification.getBeforeText(), "Delete this code file", "");
                fileModificationSuggestionRecords.add(fileModificationSuggestionRecord);
                fileModificationTrackerService.readyFileModificationUpdate(fileModification.getId(), "Delete this code file", fileModificationSuggestionRecords);
            }
            if (fileModificationSuggestionRecords != null && fileModificationSuggestionRecords.size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(fileModification.getId(), fileModification.getSubjectLine(), fileModificationSuggestionRecords);
            } else {
                fileModificationTrackerService.errorFileModification(fileModification.getId());
                if (error.equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(fileModification.getId(), fileModification.getFilePath(), "", fileModification.getModificationType());
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(fileModification.getId(), fileModification.getFilePath(), error, fileModification.getModificationType());
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(fileModification.getId());
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + fileModification.getModificationType() + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(fileModification.getId(), backgroundTask);
    }
}
