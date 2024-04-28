package com.translator.service.codactor.ai.modification;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.*;
import com.translator.model.codactor.io.CancellableRunnable;
import com.translator.model.codactor.io.CustomBackgroundTask;
import com.translator.service.codactor.ai.openai.connection.AzureConnectionService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.view.codactor.settings.CodactorConfigurable;
import com.translator.service.codactor.io.BackgroundTaskMapperService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;

import javax.inject.Inject;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AiCodeModificationServiceImpl implements AiCodeModificationService {
    private Project project;
    private CodeModificationDao codeModificationDao;
    private FirebaseTokenService firebaseTokenService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private FileModificationManagementService fileModificationManagementService;
    private DefaultConnectionService defaultConnectionService;
    private OpenAiModelService openAiModelService;
    private BackgroundTaskMapperService backgroundTaskMapperService;
    private AiFileModificationRestarterService aiFileModificationRestarterService;
    private AzureConnectionService azureConnectionService;
    private FileModificationErrorDialogFactory fileModificationErrorDialogFactory;

    @Inject
    public AiCodeModificationServiceImpl(Project project,
                                         CodeModificationDao codeModificationDao,
                                         FirebaseTokenService firebaseTokenService,
                                         CodeSnippetExtractorService codeSnippetExtractorService,
                                         FileModificationManagementService fileModificationManagementService,
                                         DefaultConnectionService defaultConnectionService,
                                         OpenAiModelService openAiModelService,
                                         BackgroundTaskMapperService backgroundTaskMapperService,
                                         AiFileModificationRestarterService aiFileModificationRestarterService,
                                         AzureConnectionService azureConnectionService,
                                         FileModificationErrorDialogFactory fileModificationErrorDialogFactory) {
        this.project = project;
        this.codeModificationDao = codeModificationDao;
        this.firebaseTokenService = firebaseTokenService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationManagementService = fileModificationManagementService;
        this.defaultConnectionService = defaultConnectionService;
        this.openAiModelService = openAiModelService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.aiFileModificationRestarterService = aiFileModificationRestarterService;
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
        String modificationId = fileModificationManagementService.addModification(filePath, modification, startIndex, endIndex, modificationType, priorContext);
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
                fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, modification, startIndex, endIndex, modificationType, priorContext);
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getModifiedCode(desktopCodeModificationRequestResource);
        if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
            fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
        } else {
            fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, modification, 0, code.length(), modificationType, priorContext);
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
                fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, modification, 0, code.length(), modificationType, priorContext);
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getModifiedCode(desktopCodeModificationRequestResource);
        if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
            fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
        } else {
            fileModificationManagementService.errorFileModification(modificationId);
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
    public void getModifiedCodeModification(Editor editor, String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationManagementService.getModificationSuggestion(suggestionId);
        String fileModificationSuggestionModificationId  = fileModificationManagementService.addModificationSuggestionModification(editor, fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, modificationType);
        if (fileModificationSuggestionModificationId == null || fileModificationSuggestionModificationId.startsWith("Error")) {
            JOptionPane.showMessageDialog(null, "Can't modify code that is already being modified", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(fileModificationSuggestionModificationId);
                fileModificationManagementService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(fileModificationSuggestionModificationId, fileModificationSuggestion.getFilePath(), "", modificationType);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(fileModificationSuggestionModificationId, fileModificationSuggestion.getFilePath(), fileModificationSuggestionModificationRecord.getError(), modificationType);
                    fileModificationErrorDialog.setVisible(true);
                }
                fileModificationManagementService.removeModificationSuggestionModification(fileModificationSuggestionModificationId);
            }
            backgroundTaskMapperService.removeTask(fileModificationSuggestionModificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification Suggestion Modification (" + modificationType + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(fileModificationSuggestionModificationId, backgroundTask);
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
        String modificationId = fileModificationManagementService.addModification(filePath, error, startIndex, endIndex, modificationType, priorContext);
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
                fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, error, startIndex, endIndex, modificationType, priorContext);
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getFixedCode(desktopCodeModificationRequestResource);
        if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
            fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
        } else {
            fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, error, 0, code.length(), modificationType, priorContext);
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
                fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, error, 0, code.length(), modificationType, priorContext);
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, modificationType, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getFixedCode(desktopCodeModificationRequestResource);
        if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
            fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getSubjectLine(), desktopCodeModificationResponseResource.getModificationSuggestions());
        } else {
            fileModificationManagementService.errorFileModification(modificationId);
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
    public void getModifiedCodeFix(Editor editor, String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationManagementService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationManagementService.addModificationSuggestionModification(editor, fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, modificationType);
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
                fileModificationManagementService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), "", modificationType);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), fileModificationSuggestionModificationRecord.getError(), modificationType);
                    fileModificationErrorDialog.setVisible(true);
                }
                fileModificationManagementService.removeModificationSuggestionModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, description, 0, 0, ModificationType.CREATE, priorContext);
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
                fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getSubjectLine(), desktopCodeCreationResponseResource.getModificationSuggestions());
            } else {
                fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, description, 0, 0, ModificationType.CREATE, priorContext);
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
        DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
        if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && !desktopCodeCreationResponseResource.getModificationSuggestions().isEmpty()) {
            fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getSubjectLine(), desktopCodeCreationResponseResource.getModificationSuggestions());
        } else {
            fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, "Delete this code file", 0, 0, ModificationType.DELETE, new ArrayList<>());
        FileModification fileModification = fileModificationManagementService.getModification(modificationId);
        fileModification.setFileDeletionAtFilePathOnAcceptance(true);
        List<FileModificationSuggestionRecord> fileModificationSuggestionRecords = new ArrayList<>();
        FileModificationSuggestionRecord fileModificationSuggestionRecord = new FileModificationSuggestionRecord(firebaseTokenService.getFirebaseToken().getUserId(), fileModification.getId(), ModificationType.DELETE, filePath, "Delete this code file", fileModification.getBeforeText(), "Delete this code file", "");
        fileModificationSuggestionRecords.add(fileModificationSuggestionRecord);
        fileModificationManagementService.readyFileModificationUpdate(modificationId, "Delete this code file", fileModificationSuggestionRecords);
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
        String modificationId = fileModificationManagementService.addModification(filePath, description, 0, 0, ModificationType.CREATE, new ArrayList<>());
        FileModification fileModification = fileModificationManagementService.getModification(modificationId);
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
                fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getSubjectLine(), desktopCodeCreationResponseResource.getModificationSuggestions());
            } else {
                fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, description, 0, 0, ModificationType.CREATE, new ArrayList<>());
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), new ArrayList<>());
        DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
        if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && !desktopCodeCreationResponseResource.getModificationSuggestions().isEmpty()) {
            fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getSubjectLine(), desktopCodeCreationResponseResource.getModificationSuggestions());
        } else {
            fileModificationManagementService.errorFileModification(modificationId);
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
        String modificationId = fileModificationManagementService.addModification(filePath, description, 0, 0, ModificationType.CREATE, priorContext);
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
                fileModificationManagementService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), false);
            } else {
                if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, "", ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, desktopCodeCreationResponseResource.getError(), ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                }
                fileModificationManagementService.removeModification(modificationId);
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification (" + ModificationType.CREATE + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getModifiedCodeCreation(Editor editor, String suggestionId, int startIndex, int endIndex, String description, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationManagementService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationManagementService.addModificationSuggestionModification(editor, fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, ModificationType.CREATE);
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
                fileModificationManagementService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
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
        String modificationId = fileModificationManagementService.addModification(filePath, null, 0, code.length(), ModificationType.TRANSLATE, priorContext);
        FileModification fileModification = fileModificationManagementService.getModification(modificationId);
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
                fileModificationManagementService.readyFileModificationUpdate(modificationId, desktopCodeTranslationResponseResource.getSubjectLine(), desktopCodeTranslationResponseResource.getModificationSuggestions());
            } else {
                fileModificationManagementService.errorFileModification(modificationId);
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
