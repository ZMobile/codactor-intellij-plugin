package com.translator.service.codactor.modification;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.*;
import com.translator.model.codactor.task.CancellableRunnable;
import com.translator.model.codactor.task.CustomBackgroundTask;
import com.translator.service.codactor.connection.AzureConnectionService;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.connection.DefaultConnectionService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.settings.CodactorConfigurable;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CodeModificationServiceImpl implements CodeModificationService {
    private Project project;
    private CodeModificationDao codeModificationDao;
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
    public CodeModificationServiceImpl(Project project,
                                       CodeModificationDao codeModificationDao,
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
        this.codeModificationDao = codeModificationDao;
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

    @Override
    public void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
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
            System.out.println("Testo:");
            System.out.println(desktopCodeModificationRequestResource.isAzure());
            System.out.println(desktopCodeModificationRequestResource.getAzureResource());
            System.out.println(desktopCodeModificationRequestResource.getAzureDeployment());
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
    public void getModifiedCodeAndWait(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
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
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
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
    }

    @Override
    public void getModifiedCode(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
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
    public void getModifiedCodeAndWait(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
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
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
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
    }

    @Override
    public void getModifiedCodeModification(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, modificationType);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModificationSuggestion.getFilePath(), suggestionId, code, modification, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationDao.getModifiedCodeModification(desktopCodeModificationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), "", modificationType);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), fileModificationSuggestionModificationRecord.getError(), modificationType);
                    fileModificationErrorDialog.setVisible(true);
                }
                fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification Suggestion Modification (" + modificationType + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
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
    public void getFixedCodeAndWait(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
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
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
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
    }

    @Override
    public void getFixedCode(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
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
    public void getFixedCodeAndWait(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
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
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
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
    }

    @Override
    public void getModifiedCodeFix(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, modificationType);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModificationSuggestion.getFilePath(), suggestionId, code, modification, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationDao.getModifiedCodeFix(desktopCodeModificationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), "", modificationType);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), fileModificationSuggestionModificationRecord.getError(), modificationType);
                    fileModificationErrorDialog.setVisible(true);
                }
                fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification Suggestion Modification (" + modificationType + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getCreatedCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, description, 0, 0, ModificationType.CREATE, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
            if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && !desktopCodeCreationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getSubjectLine(), desktopCodeCreationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeCreationResponseResource.getError(), ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + ModificationType.CREATE + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getCreatedCodeAndWait(String filePath, String description, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, description, 0, 0, ModificationType.CREATE, priorContext);
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
        DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
        if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && !desktopCodeCreationResponseResource.getModificationSuggestions().isEmpty()) {
            fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getSubjectLine(), desktopCodeCreationResponseResource.getModificationSuggestions());
        } else {
            fileModificationTrackerService.errorFileModification(modificationId);
            if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", ModificationType.CREATE);
                fileModificationErrorDialog.setVisible(true);
            } else {
                FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeCreationResponseResource.getError(), ModificationType.CREATE);
                fileModificationErrorDialog.setVisible(true);
            }
        }
    }

    @Override
    public void getDeletedCodeFile(String filePath) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, "Delete this code file", 0, 0, ModificationType.DELETE, new ArrayList<>());
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        fileModification.setFileDeletionAtFilePathOnAcceptance(true);
        List<FileModificationSuggestionRecord> fileModificationSuggestionRecords = new ArrayList<>();
        FileModificationSuggestionRecord fileModificationSuggestionRecord = new FileModificationSuggestionRecord(firebaseTokenService.getFirebaseToken().getUserId(), fileModification.getId(), ModificationType.DELETE, filePath, "Delete this code file", fileModification.getBeforeText(), "Delete this code file", "");
        fileModificationSuggestionRecords.add(fileModificationSuggestionRecord);
        fileModificationTrackerService.readyFileModificationUpdate(modificationId, "Delete this code file", fileModificationSuggestionRecords);
    }

    @Override
    public void getCreatedCodeFile(String filePath, String description) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, description, 0, 0, ModificationType.CREATE, new ArrayList<>());
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        fileModification.setFileCreationAtFilePathOnAcceptance(true);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), new ArrayList<>());
            DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
            if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && !desktopCodeCreationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getSubjectLine(), desktopCodeCreationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeCreationResponseResource.getError(), ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + ModificationType.CREATE + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getCreatedCodeFileAndWait(String filePath, String description) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, description, 0, 0, ModificationType.CREATE, new ArrayList<>());
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), new ArrayList<>());
        DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
        if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && !desktopCodeCreationResponseResource.getModificationSuggestions().isEmpty()) {
            fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getSubjectLine(), desktopCodeCreationResponseResource.getModificationSuggestions());
        } else {
            fileModificationTrackerService.errorFileModification(modificationId);
            if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", ModificationType.CREATE);
                fileModificationErrorDialog.setVisible(true);
            } else {
                FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeCreationResponseResource.getError(), ModificationType.CREATE);
                fileModificationErrorDialog.setVisible(true);
            }
        }
    }

    @Override
    public void createAndImplementCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, description, 0, 0, ModificationType.CREATE, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
            if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && !desktopCodeCreationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), false);
            } else {
                if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeCreationResponseResource.getError(), ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                }
                fileModificationTrackerService.removeModification(modificationId);
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + ModificationType.CREATE + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getModifiedCodeCreation(String suggestionId, int startIndex, int endIndex, String description, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, ModificationType.CREATE);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(fileModificationSuggestion.getFilePath(), description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationDao.getModifiedCodeCreation(desktopCodeCreationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
            } else {
                FileModificationErrorDialog fileModificationErrorDialog;
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), "", ModificationType.CREATE);
                } else {
                    fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), fileModificationSuggestionModificationRecord.getError(), ModificationType.CREATE);
                }
                fileModificationErrorDialog.setVisible(true);
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification Suggestion Modification (" + ModificationType.CREATE + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getTranslatedCode(String filePath, String newLanguage, String newFileType, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getAllText(filePath);
        String modificationId = fileModificationTrackerService.addModification(filePath, null, 0, code.length(), ModificationType.TRANSLATE, priorContext);
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        fileModification.setNewLanguage(newLanguage);
        fileModification.setNewFileType(newFileType);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
            DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource = new DesktopCodeTranslationRequestResource(filePath, code, newLanguage, newFileType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
            DesktopCodeTranslationResponseResource desktopCodeTranslationResponseResource = codeModificationDao.getTranslatedCode(desktopCodeTranslationRequestResource);
            if (desktopCodeTranslationResponseResource.getModificationSuggestions() != null && !desktopCodeTranslationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeTranslationResponseResource.getSubjectLine(), desktopCodeTranslationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeTranslationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", ModificationType.TRANSLATE);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeTranslationResponseResource.getError(), ModificationType.TRANSLATE);
                    fileModificationErrorDialog.setVisible(true);
                }
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + ModificationType.TRANSLATE + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }
}
