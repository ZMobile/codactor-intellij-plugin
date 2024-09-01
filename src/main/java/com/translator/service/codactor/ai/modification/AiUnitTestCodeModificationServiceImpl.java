package com.translator.service.codactor.ai.modification;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.io.CancellableRunnable;
import com.translator.model.codactor.io.CustomBackgroundTask;
import com.translator.service.codactor.ai.modification.authorization.VerifyIsTestFileService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.service.codactor.ai.openai.connection.AzureConnectionService;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.io.BackgroundTaskMapperService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;
import com.translator.view.codactor.settings.CodactorConfigurable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class AiUnitTestCodeModificationServiceImpl implements AiUnitTestCodeModificationService {
    private final Project project;
    private final CodeModificationDao codeModificationDao;
    private final FirebaseTokenService firebaseTokenService;
    private final VerifyIsTestFileService verifyIsTestFileService;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final DefaultConnectionService defaultConnectionService;
    private final OpenAiModelService openAiModelService;
    private final BackgroundTaskMapperService backgroundTaskMapperService;
    private final AiFileModificationRestarterService aiFileModificationRestarterService;
    private final AzureConnectionService azureConnectionService;
    private final FileModificationErrorDialogFactory fileModificationErrorDialogFactory;

    @Inject
    public AiUnitTestCodeModificationServiceImpl(Project project,
                                                 CodeModificationDao codeModificationDao,
                                                 FirebaseTokenService firebaseTokenService,
                                                 VerifyIsTestFileService verifyIsTestFileService,
                                                 CodeSnippetExtractorService codeSnippetExtractorService,
                                                 FileModificationTrackerService fileModificationTrackerService,
                                                 DefaultConnectionService defaultConnectionService,
                                                 OpenAiModelService openAiModelService,
                                                 BackgroundTaskMapperService backgroundTaskMapperService,
                                                 AiFileModificationRestarterService aiFileModificationRestarterService,
                                                 AzureConnectionService azureConnectionService,
                                                 FileModificationErrorDialogFactory fileModificationErrorDialogFactory) {
        this.project = project;
        this.codeModificationDao = codeModificationDao;
        this.firebaseTokenService = firebaseTokenService;
        this.verifyIsTestFileService = verifyIsTestFileService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.defaultConnectionService = defaultConnectionService;
        this.openAiModelService = openAiModelService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.aiFileModificationRestarterService = aiFileModificationRestarterService;
        this.azureConnectionService = azureConnectionService;
        this.fileModificationErrorDialogFactory = fileModificationErrorDialogFactory;
    }

    @Override
    public String getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        if (!verifyIsTestFileService.isTestFile(project, filePath)) {
            throw new IllegalArgumentException("File is not a test file");
        }
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return null;
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
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getModifiedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && !desktopCodeModificationResponseResource.getModificationSuggestions().isEmpty()) {
                fileModificationTrackerService.implementModification(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
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
        return modificationId;
    }

    @Override
    public String getModifiedCode(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        if (!verifyIsTestFileService.isTestFile(project, filePath)) {
            throw new IllegalArgumentException("File is not a test file");
        }
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return null;
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
                fileModificationTrackerService.implementModification(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
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
        return modificationId;
    }

    @Override
    public String getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        if (!verifyIsTestFileService.isTestFile(project, filePath)) {
            throw new IllegalArgumentException("File is not a test file");
        }
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return null;
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
                fileModificationTrackerService.implementModification(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
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
        return modificationId;
    }

    @Override
    public String getFixedCode(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        if (!verifyIsTestFileService.isTestFile(project, filePath)) {
            throw new IllegalArgumentException("File is not a test file");
        }
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return null;
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
                fileModificationTrackerService.implementModification(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
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
        return modificationId;
    }

    @Override
    public String getCreatedCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        if (!verifyIsTestFileService.isTestFile(project, filePath)) {
            throw new IllegalArgumentException("File is not a test file");
        }
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return null;
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
                fileModificationTrackerService.implementModification(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
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
        return modificationId;
    }

    @Override
    public String getCreatedCodeFile(String filePath, String description, String overrideCode) {
        if (!verifyIsTestFileService.isTestFile(project, filePath)) {
            throw new IllegalArgumentException("File is not a test file");
        }
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return null;
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
                fileModificationTrackerService.implementModification(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
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
        return modificationId;
    }

    @Override
    public String getTranslatedCode(String filePath, String newLanguage, String newFileType, List<HistoricalContextObjectHolder> priorContext, String overrideCode) {
        if (!verifyIsTestFileService.isTestFile(project, filePath)) {
            throw new IllegalArgumentException("File is not a test file");
        }
        String model = openAiModelService.getSelectedOpenAiModel();
        if (firebaseTokenService.getFirebaseToken() == null) {
            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return null;
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
                fileModificationTrackerService.implementModification(modificationId, desktopCodeTranslationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
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
        return modificationId;
    }
}
