package com.translator.service.modification.multi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.ai.LikelihoodResponse;
import com.translator.model.ai.ModificationImplementableResponse;
import com.translator.model.api.translator.modification.DesktopCodeModificationRequestResource;
import com.translator.model.api.translator.modification.DesktopCodeModificationResponseResource;
import com.translator.model.history.HistoricalContextInquiryHolder;
import com.translator.model.history.HistoricalContextModificationHolder;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;
import com.translator.model.modification.ModificationType;
import com.translator.model.modification.RecordType;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.json.JsonExtractorService;
import com.translator.service.modification.CodeModificationService;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.task.CustomBackgroundTask;
import com.translator.view.dialog.FileModificationErrorDialog;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public class MultiFileModificationServiceImpl implements MultiFileModificationService {
    private Project project;
    private InquiryDao inquiryDao;
    private FileModificationTrackerService fileModificationTrackerService;
    private CodeModificationService codeModificationService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private Gson gson;

    @Inject
    public MultiFileModificationServiceImpl(Project project,
                                            InquiryDao inquiryDao,
                                            FileModificationTrackerService fileModificationTrackerService,
                                            CodeModificationService codeModificationService,
                                            CodeSnippetExtractorService codeSnippetExtractorService,
                                            OpenAiApiKeyService openAiApiKeyService,
                                            OpenAiModelService openAiModelService,
                                            Gson gson) {
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.codeModificationService = codeModificationService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.gson = gson;
    }

    @Override
    public void modifyCodeFiles(List<String> filePaths, String modification, List<HistoricalContextObjectDataHolder> priorContextData) {
        //String testoPath = filePaths.get(0);
        //String testoCode = codeSnippetExtractorService.getAllText(testoPath);
        //String modificationIdTesto = fileModificationTrackerService.addModification(filePaths.get(0), 0, testoCode.length(), ModificationType.MODIFY);
        System.out.println("This gets called 0000");
        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
        if (priorContextData != null) {
            for (HistoricalContextObjectDataHolder data : priorContextData) {
                priorContext.add(new HistoricalContextObjectHolder(data));
            }
        }

        String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(modification);
        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/4) Ranking File Modification Likelihood");
        Map<String, Double> filePathPercentageMap = new HashMap<>();
        Map<String, String> codeMap = new HashMap<>();
        Map<String, String> modificationIdMap = new HashMap<>();
        for (String filePath : filePaths) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (file != null) {
                ApplicationManager.getApplication().invokeAndWait(() -> {
                    FileEditorManager.getInstance(project).openFile(file, true);
                    String code = codeSnippetExtractorService.getAllText(filePath);
                    String modificationId = fileModificationTrackerService.addModification(filePath, 0, code.length(), ModificationType.MODIFY);
                    modificationIdMap.put(filePath, modificationId);
                    codeMap.put(filePath, code);
                });
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Multi-File Modification", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                for (String filePath : filePaths) {
                    if (modificationIdMap.containsKey(filePath)) {
                        String code = codeMap.get(filePath);
                        String question = "I'm looking to implement the following change(s) to my program: \"" + modification + "\". First things first, what is the percentage likelihood that this specific code file has anything to do with this modification and/or will be affected by the above change(s)?: \"" + code + "\". Please provide the answer in the following JSON format: \"{ likelihoodPercentage: Float!, reasoning: String }\" where likelihoodPercentage is from 0 to 100.0";
                        Inquiry newInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                        InquiryChat latestInquiryChat = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        String json = JsonExtractorService.extractJsonObject(latestInquiryChat.getMessage());
                        if (filePathPercentageMap.containsKey(filePath)) {
                            System.out.println("ERROR: duplicate: " + filePath);
                        }
                        if (json == null) {
                            filePathPercentageMap.put(filePath, 0.0);
                        } else {
                            LikelihoodResponse likelihoodResponse = null;
                            boolean badJson = false;
                            try {
                                likelihoodResponse = gson.fromJson(json, LikelihoodResponse.class);
                            } catch (JsonSyntaxException jsonSyntaxException) {
                                badJson = true;
                            }
                            if (badJson) {
                                String fixQuestion = "I wasn't able to parse that Json response. Could you provide the answer in the following JSON format: \"{ likelihoodPercentage: Float!, reasoning: String }\" where likelihoodPercentage is from 0 to 100.0?";
                                Inquiry fixInquiry = inquiryDao.continueInquiry(latestInquiryChat.getId(), fixQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                                InquiryChat fixInquiryChat = newInquiry.getChats().get(fixInquiry.getChats().size() - 1);
                                String fixJson = JsonExtractorService.extractJsonObject(fixInquiryChat.getMessage());
                                likelihoodResponse = gson.fromJson(fixJson, LikelihoodResponse.class);
                            }
                            assert likelihoodResponse != null;
                            filePathPercentageMap.put(filePath, likelihoodResponse.getLikelihoodPercentage());
                        }
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/4) Ranking File Modification Likelihood (" + filePathPercentageMap.values().size() + "/" + filePaths.size() + ")");
                    }
                }

                List<String> sortedFilePaths = new ArrayList<>();
                for (String filePath : filePaths) {
                    if (filePathPercentageMap.containsKey(filePath) && filePathPercentageMap.get(filePath) != 0) {
                        sortedFilePaths.add(filePath);
                    } else {
                        fileModificationTrackerService.removeModification(modificationIdMap.get(filePath));
                    }
                }
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification...");
                sortedFilePaths.sort((filePath1, filePath2) -> Double.compare(filePathPercentageMap.get(filePath2), filePathPercentageMap.get(filePath1)));
                List<String> utilizedFilePaths = new ArrayList<>();
                String initialFilePath = sortedFilePaths.get(0);
                String code = codeMap.get(initialFilePath);
                String initialQuestion = "I'm looking to implement the following modification to my program: \"" + modification + "\". First things first, Yes or No: Will you be able to achieve this modification solely by changing the code I provide here at " + sortedFilePaths.get(0) + "?: \"" + code + "\". Please provide the answer in the following JSON format: \"{ modificationAchievable: Boolean!, reasoning: String }\" where reasoning is only optionally required if modificationAchievable is false.";
                Inquiry newInquiry = inquiryDao.createGeneralInquiry(initialQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                InquiryChat mostRecentInquiryChat = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                String json = JsonExtractorService.extractJsonObject(mostRecentInquiryChat.getMessage());
                ModificationImplementableResponse modificationImplementableResponse = null;
                boolean badJson = false;
                try {
                    modificationImplementableResponse = gson.fromJson(json, ModificationImplementableResponse.class);
                } catch (JsonSyntaxException jsonSyntaxException) {
                    badJson = true;
                }
                if (badJson) {
                    String fixQuestion = "I wasn't able to parse that Json response. Could you provide the answer in the following JSON format: \"{ likelihoodPercentage: Float!, reasoning: String }\" where likelihoodPercentage is from 0 to 100.0?";
                    Inquiry fixInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), fixQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                    InquiryChat fixInquiryChat = newInquiry.getChats().get(fixInquiry.getChats().size() - 1);
                    String fixJson = JsonExtractorService.extractJsonObject(fixInquiryChat.getMessage());
                    modificationImplementableResponse = gson.fromJson(fixJson, ModificationImplementableResponse.class);
                }
                assert modificationImplementableResponse != null;
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification... (1 file processed)");
                HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat.getId(), null, false, null);
                HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                priorContext.add(priorContextObject);
                utilizedFilePaths.add(initialFilePath);
                if (!modificationImplementableResponse.getModificationAchievable()) {
                    for (int i = 1; i < sortedFilePaths.size(); i++) {
                        String filePath = sortedFilePaths.get(i);
                        code = codeMap.get(filePath);
                        String question = "What about if I add this code at " + filePath + " to that condition: \"" + code + "\". Could I achieve implementing the entire modification now? Yes or No? Please provide the answer in the following JSON format: \"{ modificationAchievable: Boolean!, reasoning: String }\" where reasoning is only optionally required if modificationAchievable is false.";
                        newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                        mostRecentInquiryChat = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        String json2 = JsonExtractorService.extractJsonObject(mostRecentInquiryChat.getMessage());
                        ModificationImplementableResponse modificationImplementableResponse2 = gson.fromJson(json2, ModificationImplementableResponse.class);
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification... (" + i + 1 + " files processed)");
                        inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat.getId(), null, false, null);
                        priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                        priorContext.add(priorContextObject);
                        utilizedFilePaths.add(initialFilePath);
                        if (modificationImplementableResponse2.getModificationAchievable()) {
                            break;
                        }
                    }
                }
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/4) Modifying necessary files...");
                List<HistoricalContextObjectHolder> modificationsPriorContext = new ArrayList<>();
                for (int i = 0; i < utilizedFilePaths.size(); i++) {
                    String filePath = utilizedFilePaths.get(i);
                    code = codeMap.get(filePath);
                    String modificationForThisFile = "The modifications that need to be applied to this code to achieve the above modification: (" + modification + ")";
                    DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modificationForThisFile, ModificationType.MODIFY, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                    DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
                    if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                        fileModificationTrackerService.readyFileModificationUpdate(modificationIdMap.get(filePath), desktopCodeModificationResponseResource.getModificationSuggestions());
                        HistoricalContextModificationHolder modificationContext = new HistoricalContextModificationHolder(desktopCodeModificationRequestResource.getSuggestionId(), RecordType.FILE_MODIFICATION_SUGGESTION, false, null);
                        priorContextObject = new HistoricalContextObjectHolder(modificationContext);
                        priorContext.add(priorContextObject);
                        modificationsPriorContext.add(priorContextObject);
                    } else {
                        fileModificationTrackerService.errorFileModification(modificationIdMap.get(filePath));
                        if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                            FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, filePath, null, ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                        } else {
                            FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, filePath, desktopCodeModificationResponseResource.getError(), ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                        }
                    }
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/4) Modifying necessary files... (" + i + 1 + " file(s) modified)");
                    try {
                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                    } catch (InterruptedException e) {
                        // Handle the interruption
                        e.printStackTrace();
                    }
                }
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(4/4) Checking for missed files...");
                List<String> finalProcessedFiles = new ArrayList<>();
                List<String> filesToProcess = new ArrayList<>();
                for (String filePath : sortedFilePaths) {
                    if (!utilizedFilePaths.contains(filePath)) {
                        filesToProcess.add(filePath);
                    }
                    String fileCode = codeMap.get(filePath);
                    String question = "Analyze this code: \"" + fileCode + "\" at " + filePath + "\"? Does this file need to be changed in order to fulfill the requested modification: \"" + modification + "\"  Yes or No?";
                    Inquiry finalInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), modificationsPriorContext);
                    InquiryChat latestInquiryChat = finalInquiry.getChats().get(finalInquiry.getChats().size() - 1);
                    if (latestInquiryChat.getMessage().toLowerCase().startsWith("yes")) {
                        String modificationForThisFile = "The modifications that need to be applied to this code to achieve the above modification: (" + modification + ")";
                        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, fileCode, modificationForThisFile, ModificationType.MODIFY, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), new ArrayList<>());
                        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
                        if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                            fileModificationTrackerService.readyFileModificationUpdate(modificationIdMap.get(filePath), desktopCodeModificationResponseResource.getModificationSuggestions());
                        } else {
                            fileModificationTrackerService.errorFileModification(modificationIdMap.get(filePath));
                            if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                                FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, filePath, null, ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                            } else {
                                FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, filePath, desktopCodeModificationResponseResource.getError(), ModificationType.TRANSLATE, openAiApiKeyService, openAiModelService, fileModificationTrackerService);
                            }
                        }
                    } else {
                        fileModificationTrackerService.removeModification(modificationIdMap.get(filePath));
                    }
                    finalProcessedFiles.add(filePath);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(4/4) Checking for missed files... (" + finalProcessedFiles.size() + "/" + filesToProcess.size() + " files checked)");
                }
                fileModificationTrackerService.removeMultiFileModification(multiFileModificationId);
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
    }
}
