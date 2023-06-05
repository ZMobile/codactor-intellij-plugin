package com.translator.service.modification;

import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.model.api.translator.modification.*;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestion;
import com.translator.model.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.modification.ModificationType;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.context.PromptContextService;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.task.BackgroundTaskMapperService;
import com.translator.service.task.CustomBackgroundTask;
import com.translator.view.dialog.FileModificationErrorDialog;
import com.translator.view.dialog.LoginDialog;
import com.translator.view.dialog.OpenAiApiKeyDialog;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AutomaticCodeModificationServiceImpl implements AutomaticCodeModificationService {
    private Project project;
    private FirebaseTokenService firebaseTokenService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private FileModificationTrackerService fileModificationTrackerService;
    private PromptContextService promptContextService;
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private CodeModificationService codeModificationService;
    private BackgroundTaskMapperService backgroundTaskMapperService;

    @Inject
    public AutomaticCodeModificationServiceImpl(Project project,
                                                FirebaseTokenService firebaseTokenService,
                                                CodeSnippetExtractorService codeSnippetExtractorService,
                                                FileModificationTrackerService fileModificationTrackerService,
                                                @Assisted PromptContextService promptContextService,
                                                OpenAiApiKeyService openAiApiKeyService,
                                                OpenAiModelService openAiModelService,
                                                CodeModificationService codeModificationService,
                                                BackgroundTaskMapperService backgroundTaskMapperService) {
        this.project = project;
        this.firebaseTokenService = firebaseTokenService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.promptContextService = promptContextService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.codeModificationService = codeModificationService;
        this.backgroundTaskMapperService = backgroundTaskMapperService;
    }

    @Override
    public void getModifiedCode(String filePath, int startIndex, int endIndex, String modification, ModificationType modificationType) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getSnippet(filePath, startIndex, endIndex);
        String modificationId = fileModificationTrackerService.addModification(filePath, startIndex, endIndex, modificationType);
        Runnable task = () -> {
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
            if (priorContextData != null) {
                for (HistoricalContextObjectDataHolder data : priorContextData) {
                    priorContext.add(new HistoricalContextObjectHolder(data));
                }
            }
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, ModificationType.MODIFY, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
                promptContextService.clearPromptContext();
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, modificationId, filePath, null, ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, modificationId, filePath, desktopCodeModificationResponseResource.getError(), ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
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
    public void getModifiedCodeModification(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, modificationType);
        Runnable task = () -> {
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
            if (priorContextData != null) {
                for (HistoricalContextObjectDataHolder data : priorContextData) {
                    priorContext.add(new HistoricalContextObjectHolder(data));
                }
            }
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModificationSuggestion.getFilePath(), suggestionId, code, modification, modificationType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeModification(desktopCodeModificationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
                promptContextService.clearPromptContext();
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                    //openAiApiKeyDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, fileModificationSuggestionModificationRecord.getError(), "Error",
                            JOptionPane.ERROR_MESSAGE);
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
    public void getFixedCode(String filePath, int startIndex, int endIndex, String error, ModificationType modificationType) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getSnippet(filePath, startIndex, endIndex);
        String modificationId = fileModificationTrackerService.addModification(filePath, 0, code.length(), modificationType);
        Runnable task = () -> {
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
            if (priorContextData != null) {
                for (HistoricalContextObjectDataHolder data : priorContextData) {
                    priorContext.add(new HistoricalContextObjectHolder(data));
                }
            }
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, ModificationType.FIX, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getFixedCode(desktopCodeModificationRequestResource);
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
                promptContextService.clearPromptContext();
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, modificationId, filePath, null, ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, modificationId, filePath, desktopCodeModificationResponseResource.getError(), ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
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
    public void getModifiedCodeFix(String suggestionId, String code, int startIndex, int endIndex, String modification, ModificationType modificationType) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, startIndex, endIndex, modificationType);
        Runnable task = () -> {
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
            if (priorContextData != null) {
                for (HistoricalContextObjectDataHolder data : priorContextData) {
                    priorContext.add(new HistoricalContextObjectHolder(data));
                }
            }
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(fileModificationSuggestion.getFilePath(), suggestionId, code, modification, modificationType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeFix(desktopCodeModificationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
                promptContextService.clearPromptContext();
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                    //openAiApiKeyDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, fileModificationSuggestionModificationRecord.getError(), "Error",
                            JOptionPane.ERROR_MESSAGE);
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
    public void getCreatedCode(String filePath, String description) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, 0, 0, ModificationType.CREATE);
        Runnable task = () -> {
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
            if (priorContextData != null) {
                for (HistoricalContextObjectDataHolder data : priorContextData) {
                    priorContext.add(new HistoricalContextObjectHolder(data));
                }
            }
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
            if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && desktopCodeCreationResponseResource.getModificationSuggestions().size() > 0) {
                System.out.println("Making sure this gets called: " + desktopCodeCreationResponseResource.getModificationSuggestions().get(0));
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions());
                promptContextService.clearPromptContext();
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, modificationId, filePath, null, ModificationType.CREATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, modificationId, filePath, desktopCodeCreationResponseResource.getError(), ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
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
    public void createAndImplementCode(String filePath, String description) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String modificationId = fileModificationTrackerService.addModification(filePath, 0, 0, ModificationType.CREATE);
        Runnable task = () -> {
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
            if (priorContextData != null) {
                for (HistoricalContextObjectDataHolder data : priorContextData) {
                    priorContext.add(new HistoricalContextObjectHolder(data));
                }
            }
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
            if (desktopCodeCreationResponseResource.getModificationSuggestions() != null  && desktopCodeCreationResponseResource.getModificationSuggestions().size() > 0) {
                System.out.println("Creator testo 3");
                fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), false);
                System.out.println("Creator testo 4");
                promptContextService.clearPromptContext();
            } else {
                if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                    OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                    //openAiApiKeyDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, desktopCodeCreationResponseResource.getError(), "Error",
                            JOptionPane.ERROR_MESSAGE);
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
    public void getModifiedCodeCreation(String suggestionId, int startIndex, int endIndex, String description) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(suggestionId);
        String modificationId = fileModificationTrackerService.addModificationSuggestionModification(fileModificationSuggestion.getFilePath(), suggestionId, 0, 0, ModificationType.CREATE);
        Runnable task = () -> {
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
            if (priorContextData != null) {
                for (HistoricalContextObjectDataHolder data : priorContextData) {
                    priorContext.add(new HistoricalContextObjectHolder(data));
                }
            }
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(fileModificationSuggestion.getFilePath(), description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationService.getModifiedCodeCreation(desktopCodeCreationRequestResource);
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                fileModificationSuggestionModificationRecord.setModificationSuggestionModificationId(modificationId);
                fileModificationTrackerService.implementModificationSuggestionModificationUpdate(fileModificationSuggestionModificationRecord);
                promptContextService.clearPromptContext();
            } else {
                if (fileModificationSuggestionModificationRecord.getError().equals("null: null")) {
                    OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                    //openAiApiKeyDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, fileModificationSuggestionModificationRecord.getError(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
            }
            backgroundTaskMapperService.removeTask(modificationId);
        };
        Runnable cancelTask = () -> {};
        CustomBackgroundTask backgroundTask = new CustomBackgroundTask(project, "File Modification Suggestion Modification (" + ModificationType.CREATE + ")", task, cancelTask);
        ProgressManager.getInstance().run(backgroundTask);
        backgroundTaskMapperService.addTask(modificationId, backgroundTask);
    }

    @Override
    public void getTranslatedCode(String filePath, String newLanguage, String newFileType) {
        if (firebaseTokenService.getFirebaseToken() == null) {
            LoginDialog loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                return;
            }
        }
        String code = codeSnippetExtractorService.getAllText(filePath);
        String modificationId = fileModificationTrackerService.addModification(filePath, 0, code.length(), ModificationType.TRANSLATE);
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        fileModification.setNewLanguage(newLanguage);
        fileModification.setNewFileType(newFileType);
        Runnable task = () -> {
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
            if (priorContextData != null) {
                for (HistoricalContextObjectDataHolder data : priorContextData) {
                    priorContext.add(new HistoricalContextObjectHolder(data));
                }
            }
            String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
            DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource = new DesktopCodeTranslationRequestResource(filePath, code, newLanguage, newFileType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
            DesktopCodeTranslationResponseResource desktopCodeTranslationResponseResource = codeModificationService.getTranslatedCode(desktopCodeTranslationRequestResource);
            if (desktopCodeTranslationResponseResource.getModificationSuggestions() != null && desktopCodeTranslationResponseResource.getModificationSuggestions().size() > 0) {
                fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeTranslationResponseResource.getModificationSuggestions());
                promptContextService.clearPromptContext();
            } else {
                fileModificationTrackerService.errorFileModification(modificationId);
                if (desktopCodeTranslationResponseResource.getError().equals("null: null")) {
                    FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, modificationId, filePath, null, ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                    fileModificationErrorDialog.setVisible(true);
                } else {
                    FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, modificationId, filePath, desktopCodeTranslationResponseResource.getError(), ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
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
