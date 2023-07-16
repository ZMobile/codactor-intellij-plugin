package com.translator.service.codactor.modification;

import com.google.inject.Inject;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.model.codactor.task.CancellableRunnable;
import com.translator.model.codactor.task.CustomBackgroundTask;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;

import java.util.ArrayList;
import java.util.List;

public class FileModificationRestarterServiceImpl implements FileModificationRestarterService {
    private final Project project;
    private final FirebaseTokenService firebaseTokenService;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final OpenAiApiKeyService openAiApiKeyService;
    private final OpenAiModelService openAiModelService;
    private final CodeModificationService codeModificationService;
    private final BackgroundTaskMapperService backgroundTaskMapperService;
    private final FileModificationErrorDialogFactory fileModificationErrorDialogFactory;

    @Inject
    public FileModificationRestarterServiceImpl(Project project,
                                                FirebaseTokenService firebaseTokenService,
                                                FileModificationTrackerService fileModificationTrackerService,
                                                OpenAiApiKeyService openAiApiKeyService,
                                                OpenAiModelService openAiModelService,
                                                CodeModificationService codeModificationService,
                                                BackgroundTaskMapperService backgroundTaskMapperService,
                                                FileModificationErrorDialogFactory fileModificationErrorDialogFactory) {
        this.project = project;
        this.firebaseTokenService = firebaseTokenService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.codeModificationService = codeModificationService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.fileModificationErrorDialogFactory = fileModificationErrorDialogFactory;
    }

    @Override
    public void restartFileModification(FileModification fileModification) {
        fileModificationTrackerService.undoReadyFileModification(fileModification.getId());
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            List<FileModificationSuggestionRecord> fileModificationSuggestionRecords = null;
            String error = null;
            if (fileModification.getModificationType() == ModificationType.MODIFY || fileModification.getModificationType() == ModificationType.MODIFY_SELECTION) {
                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModification.getFilePath(), fileModification.getBeforeText(), fileModification.getModification(), fileModification.getModificationType(), openAiApiKey, openAiModelService.getSelectedOpenAiModel(), fileModification.getPriorContext());
                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
                if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                    fileModificationSuggestionRecords = desktopCodeModificationResponseResource.getModificationSuggestions();
                } else {
                    error = desktopCodeModificationResponseResource.getError();
                }
            } else if (fileModification.getModificationType() == ModificationType.FIX || fileModification.getModificationType() == ModificationType.FIX_SELECTION) {
                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModification.getFilePath(), fileModification.getBeforeText(), fileModification.getModification(), fileModification.getModificationType(), openAiApiKey, openAiModelService.getSelectedOpenAiModel(), fileModification.getPriorContext());
                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getFixedCode(desktopCodeModificationRequestResource);
                if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                    fileModificationSuggestionRecords = desktopCodeModificationResponseResource.getModificationSuggestions();
                } else {
                    error = desktopCodeModificationResponseResource.getError();
                }
            } else if (fileModification.getModificationType() == ModificationType.CREATE) {
                DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(fileModification.getFilePath(), fileModification.getModification(), openAiApiKey, openAiModelService.getSelectedOpenAiModel(), fileModification.getPriorContext());
                DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
                if (desktopCodeCreationResponseResource.getModificationSuggestions() != null && desktopCodeCreationResponseResource.getModificationSuggestions().size() > 0) {
                    fileModificationSuggestionRecords = desktopCodeCreationResponseResource.getModificationSuggestions();
                } else {
                    error = desktopCodeCreationResponseResource.getError();
                }
            } else if (fileModification.getModificationType() == ModificationType.TRANSLATE) {
                DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource = new DesktopCodeTranslationRequestResource(fileModification.getFilePath(), fileModification.getBeforeText(), fileModification.getNewLanguage(), fileModification.getNewFileType(), openAiApiKey, openAiModelService.getSelectedOpenAiModel(), fileModification.getPriorContext());
                DesktopCodeTranslationResponseResource desktopCodeTranslationResponseResource = codeModificationService.getTranslatedCode(desktopCodeTranslationRequestResource);
                if (desktopCodeTranslationResponseResource.getModificationSuggestions() != null && desktopCodeTranslationResponseResource.getModificationSuggestions().size() > 0) {
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
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(fileModification.getId(), fileModification.getFilePath(), "", ModificationType.TRANSLATE);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(fileModification.getId(), fileModification.getFilePath(), error, ModificationType.TRANSLATE);
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(fileModification.getId());
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + ModificationType.TRANSLATE + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(fileModification.getId(), backgroundTask);
    }
}
