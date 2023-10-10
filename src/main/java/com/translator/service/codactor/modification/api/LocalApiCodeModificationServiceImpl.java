package com.translator.service.codactor.modification.api;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.model.codactor.api.translator.modification.DesktopCodeModificationRequestResource;
import com.translator.model.codactor.api.translator.modification.DesktopCodeModificationResponseResource;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.model.codactor.task.CancellableRunnable;
import com.translator.model.codactor.task.CustomBackgroundTask;
import com.translator.service.codactor.connection.AzureConnectionService;
import com.translator.service.codactor.connection.DefaultConnectionService;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.modification.FileModificationRestarterService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;
import com.translator.view.codactor.settings.CodactorConfigurable;

import javax.inject.Inject;
import java.util.List;

public class LocalApiCodeModificationServiceImpl implements LocalApiCodeModificationService {
    private Project project;
    private FirebaseTokenService firebaseTokenService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private FileModificationTrackerService fileModificationTrackerService;
    private DefaultConnectionService defaultConnectionService;
    private OpenAiModelService openAiModelService;
    private BackgroundTaskMapperService backgroundTaskMapperService;
    private FileModificationRestarterService fileModificationRestarterService;
    private AzureConnectionService azureConnectionService;
    private FileModificationErrorDialogFactory fileModificationErrorDialogFactory;

    @Inject
    public LocalApiCodeModificationServiceImpl(Project project,
                                   FirebaseTokenService firebaseTokenService,
                                   CodeSnippetExtractorService codeSnippetExtractorService,
                                   FileModificationTrackerService fileModificationTrackerService,
                                   DefaultConnectionService defaultConnectionService,
                                   OpenAiModelService openAiModelService,
                                   BackgroundTaskMapperService backgroundTaskMapperService,
                                   FileModificationRestarterService fileModificationRestarterService,
                                   AzureConnectionService azureConnectionService,
                                   FileModificationErrorDialogFactory fileModificationErrorDialogFactory) {
        this.project = project;
        this.firebaseTokenService = firebaseTokenService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.defaultConnectionService = defaultConnectionService;
        this.openAiModelService = openAiModelService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.fileModificationRestarterService = fileModificationRestarterService;
        this.azureConnectionService = azureConnectionService;
        this.fileModificationErrorDialogFactory = fileModificationErrorDialogFactory;
    }

    /*@Override
    public void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getSnippet(filePath, startIndex, endIndex);
        String modificationId = fileModificationTrackerService.addModification(filePath, modification, startIndex, endIndex, modificationType, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            desktopCodeModificationRequestResource.setOverrideCode(overrideCode);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getModifiedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", modificationType);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeModificationResponseResource.getError(), modificationType);
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + modificationType + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getModifiedCode(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getAllText(filePath);
        String modificationId = fileModificationTrackerService.addModification(filePath, modification, 0, code.length(), modificationType, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            desktopCodeModificationRequestResource.setOverrideCode(overrideCode);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getModifiedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", modificationType);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeModificationResponseResource.getError(), modificationType);
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + modificationType + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getSnippet(filePath, startIndex, endIndex);
        String modificationId = fileModificationTrackerService.addModification(filePath, error, startIndex, endIndex, modificationType, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            desktopCodeModificationRequestResource.setOverrideCode(overrideCode);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getFixedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", modificationType);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeModificationResponseResource.getError(), modificationType);
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + modificationType + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getFixedCode(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getAllText(filePath);
        String modificationId = fileModificationTrackerService.addModification(filePath, error, 0, code.length(), modificationType, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            desktopCodeModificationRequestResource.setOverrideCode(overrideCode);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getFixedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", modificationType);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeModificationResponseResource.getError(), modificationType);
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + modificationType + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }*/
}
