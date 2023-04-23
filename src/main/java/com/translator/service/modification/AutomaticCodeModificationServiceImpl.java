package com.translator.service.modification;

import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.model.api.translator.modification.*;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestion;
import com.translator.model.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.modification.ModificationType;
import com.translator.service.code.CodeHighlighterService;
import com.translator.service.context.PromptContextService;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.view.dialog.LoginDialog;
import com.translator.view.dialog.OpenAiApiKeyDialog;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.swing.*;
import java.io.IOException;
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

    @Inject
    public AutomaticCodeModificationServiceImpl(Project project,
                                                FirebaseTokenService firebaseTokenService,
                                                CodeSnippetExtractorService codeSnippetExtractorService,
                                                FileModificationTrackerService fileModificationTrackerService,
                                                @Assisted PromptContextService promptContextService,
                                                OpenAiApiKeyService openAiApiKeyService,
                                                OpenAiModelService openAiModelService,
                                                CodeModificationService codeModificationService) {
        this.project = project;
        this.firebaseTokenService = firebaseTokenService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.promptContextService = promptContextService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.codeModificationService = codeModificationService;
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
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "File Modification (" + modificationType + ")", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
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
                    if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                        //openAiApiKeyDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(super.getParentComponent(), desktopCodeModificationResponseResource.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    fileModificationTrackerService.removeModification(modificationId);
                }
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
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
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "File Modification Suggestion Modification (" + modificationType + ")", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
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
                        JOptionPane.showMessageDialog(super.getParentComponent(), fileModificationSuggestionModificationRecord.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
                }
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
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
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "File Modification (" + modificationType + ")", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
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
                    if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                        //openAiApiKeyDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(super.getParentComponent(), desktopCodeModificationResponseResource.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    fileModificationTrackerService.removeModification(modificationId);
                }
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
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
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "File Modification Suggestion Modification (" + modificationType + ")", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
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
                        JOptionPane.showMessageDialog(super.getParentComponent(), fileModificationSuggestionModificationRecord.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
                }
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
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
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "File Modification (" + ModificationType.CREATE + ")", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
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
                    fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions());
                    promptContextService.clearPromptContext();
                } else {
                    if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                        //openAiApiKeyDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(super.getParentComponent(), desktopCodeCreationResponseResource.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    fileModificationTrackerService.removeModification(modificationId);
                }
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
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
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "File Modification Suggestion Modification (" + ModificationType.CREATE + ")", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
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
                        JOptionPane.showMessageDialog(super.getParentComponent(), fileModificationSuggestionModificationRecord.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    fileModificationTrackerService.removeModificationSuggestionModification(modificationId);
                }
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
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
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "File Modification (" + ModificationType.TRANSLATE + ")", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
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
                    /*VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
                    assert virtualFile != null;
                    String fileName = virtualFile.getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        String newFileName = fileName.substring(0, dotIndex) + "." + newFileType;
                        try {
                            virtualFile.rename(this, newFileName);
                        } catch (IOException ex) {
                            // Handle the exception if necessary
                        }
                    }*/
                    fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeTranslationResponseResource.getModificationSuggestions());
                    promptContextService.clearPromptContext();
                } else {
                    if (desktopCodeTranslationResponseResource.getError().equals("null: null")) {
                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                        //openAiApiKeyDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(super.getParentComponent(), desktopCodeTranslationResponseResource.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    fileModificationTrackerService.removeModification(modificationId);
                }
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
    }
}
