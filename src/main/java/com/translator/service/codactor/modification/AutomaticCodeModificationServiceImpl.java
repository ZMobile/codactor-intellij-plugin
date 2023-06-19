package com.translator.service.codactor.modification;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestion;
import com.translator.model.codactor.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.model.codactor.task.CancellableRunnable;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.model.codactor.task.CustomBackgroundTask;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.dialog.LoginDialog;
import com.translator.view.codactor.dialog.OpenAiApiKeyDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;

import javax.inject.Inject;
import javax.swing.*;
import java.util.List;

public class AutomaticCodeModificationServiceImpl implements AutomaticCodeModificationService {
    private Project project;
    private FirebaseTokenService firebaseTokenService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private FileModificationTrackerService fileModificationTrackerService;
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private CodeModificationService codeModificationService;
    private BackgroundTaskMapperService backgroundTaskMapperService;
    private FileModificationRestarterService fileModificationRestarterService;
    private FileModificationErrorDialogFactory fileModificationErrorDialogFactory;

    @Inject
    public AutomaticCodeModificationServiceImpl(Project project,
                                                FirebaseTokenService firebaseTokenService,
                                                CodeSnippetExtractorService codeSnippetExtractorService,
                                                FileModificationTrackerService fileModificationTrackerService,
                                                OpenAiApiKeyService openAiApiKeyService,
                                                OpenAiModelService openAiModelService,
                                                CodeModificationService codeModificationService,
                                                BackgroundTaskMapperService backgroundTaskMapperService,
                                                FileModificationRestarterService fileModificationRestarterService,
                                                FileModificationErrorDialogFactory fileModificationErrorDialogFactory) {
        this.project = project;
        this.firebaseTokenService = firebaseTokenService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.codeModificationService = codeModificationService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
        this.fileModificationRestarterService = fileModificationRestarterService;
        this.fileModificationErrorDialogFactory = fileModificationErrorDialogFactory;
    }

    @Override
    public void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getSnippet(filePath, startIndex, endIndex);
        String modificationId = fileModificationTrackerService.addModification(filePath, modification, startIndex, endIndex, modificationType, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, modificationType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, null, modificationType);
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
    public void getModifiedCode(String filePath, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getAllText(filePath);
        String modificationId = fileModificationTrackerService.addModification(filePath, modification, 0, code.length(), modificationType, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, modificationType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, null, modificationType);
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
    public void getModifiedCodeModification(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, modificationType);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModificationSuggestion.getFilePath(), suggestionId, code, modification, modificationType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeModification(desktopCodeModificationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), null, modificationType);
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
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getSnippet(filePath, startIndex, endIndex);
        String modificationId = fileModificationTrackerService.addModification(filePath, error, startIndex, endIndex, modificationType, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, modificationType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getFixedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, null, modificationType);
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
    public void getFixedCode(String filePath, String error, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getAllText(filePath);
        String modificationId = fileModificationTrackerService.addModification(filePath, error, 0, code.length(), modificationType, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, modificationType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getFixedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, null, modificationType);
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
    public void getModifiedCodeFix(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, modificationType);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModificationSuggestion.getFilePath(), suggestionId, code, modification, modificationType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeFix(desktopCodeModificationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), null, modificationType);
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
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, description, 0, 0, ModificationType.CREATE, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
            if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && desktopCodeCreationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, null, ModificationType.CREATE);
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
    public void createAndImplementCode(String filePath, String description, List<HistoricalContextObjectHolder> priorContext) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, description, 0, 0, ModificationType.CREATE, priorContext);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
            if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && desktopCodeCreationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), false);
            } else {
                if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, null, ModificationType.CREATE);
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
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, ModificationType.CREATE);
        CancellableRunnable task = customProgressIndicator -> {
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(fileModificationSuggestion.getFilePath(), description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeCreation(desktopCodeCreationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), null, ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, fileModificationSuggestion.getFilePath(), fileModificationSuggestionModificationRecord.getError(), ModificationType.CREATE);
                    fileModificationErrorDialog.setVisible(true);
                }
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
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
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
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource = new DesktopCodeTranslationRequestResource(filePath, code, newLanguage, newFileType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeTranslationResponseResource desktopCodeTranslationResponseResource = codeModificationService.getTranslatedCode(desktopCodeTranslationRequestResource);
            if (desktopCodeTranslationResponseResource.getModificationSuggestions() != null && desktopCodeTranslationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeTranslationResponseResource.getModificationSuggestions());
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeTranslationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(modificationId, filePath, null, ModificationType.TRANSLATE);
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
