package com.translator.service.codactor.modification.multi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.ai.FixImplementableResponse;
import com.translator.model.codactor.ai.LikelihoodResponse;
import com.translator.model.codactor.ai.ModificationImplementableResponse;
import com.translator.model.codactor.ai.ModificationNeededResponse;
import com.translator.model.codactor.api.translator.modification.DesktopCodeModificationRequestResource;
import com.translator.model.codactor.api.translator.modification.DesktopCodeModificationResponseResource;
import com.translator.model.codactor.history.HistoricalContextInquiryHolder;
import com.translator.model.codactor.history.HistoricalContextModificationHolder;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.model.codactor.modification.RecordType;
import com.translator.model.codactor.thread.BooleanWaiter;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.json.JsonExtractorService;
import com.translator.service.codactor.modification.CodeModificationService;
import com.translator.service.codactor.modification.FileModificationRestarterService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.*;

public class MultiFileModificationServiceImpl implements MultiFileModificationService {
    private Project project;
    private InquiryDao inquiryDao;
    private FileModificationTrackerService fileModificationTrackerService;
    private FileModificationRestarterService fileModificationRestarterService;
    private CodeModificationService codeModificationService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private FileModificationErrorDialogFactory fileModificationErrorDialogFactory;
    private Gson gson;

    @Inject
    public MultiFileModificationServiceImpl(Project project,
                                            InquiryDao inquiryDao,
                                            FileModificationTrackerService fileModificationTrackerService,
                                            FileModificationRestarterService fileModificationRestarterService,
                                            CodeModificationService codeModificationService,
                                            CodeSnippetExtractorService codeSnippetExtractorService,
                                            OpenAiApiKeyService openAiApiKeyService,
                                            OpenAiModelService openAiModelService,
                                            FileModificationErrorDialogFactory fileModificationErrorDialogFactory,
                                            Gson gson) {
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationRestarterService = fileModificationRestarterService;
        this.codeModificationService = codeModificationService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.fileModificationErrorDialogFactory = fileModificationErrorDialogFactory;
        this.gson = gson;
    }

    @Override
    public void modifyCodeFiles(List<String> filePaths, String modification, List<HistoricalContextObjectHolder> priorContext) throws InterruptedException {
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
                    //FileEditorManager.getInstance(project).openFile(file, true);
                    String code = codeSnippetExtractorService.getAllText(filePath);
                    String modificationId = fileModificationTrackerService.addModification(filePath, modification, 0, code.length(), ModificationType.MODIFY, priorContext);
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
        Map<String, Boolean> booleanMap = new HashMap<>();
        Map<String, Boolean> activatedMap = new HashMap<>();

        for (String filePath : filePaths) {
            booleanMap.put(filePath, false);
        }
        BooleanWaiter filePathLikelihoodWaiter = new BooleanWaiter(booleanMap);

        for (String filePath : filePaths) {
            Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Multi-File Modification", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    if (activatedMap.containsKey(filePath)) {
                        System.out.println("Duplicate process attempted. Aborted.");
                        return;
                    }
                    activatedMap.put(filePath, true);
                    if (modificationIdMap.containsKey(filePath)) {
                        String code = codeMap.get(filePath);
                        String question = "I'm looking to implement the following modification(s) to my program: \"" + modification + "\". First things first, what is the percentage likelihood that this specific code file has anything to do with this modification and/or will be affected by the provided modification(s): \"" + code + "\". Please provide the answer in the following JSON format: \"{ likelihoodPercentage: Float!, reasoning: String }\" where likelihoodPercentage is from 0 to 100.0";
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
                        filePathLikelihoodWaiter.setTrue(filePath);
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/4) Ranking File Modification Likelihood (" + filePathPercentageMap.values().size() + "/" + filePaths.size() + ")");
                    }
                }
            };
            ProgressManager.getInstance().run(backgroundTask);
        }
        filePathLikelihoodWaiter.waitForAllTrue();

        List<String> sortedFilePaths = new ArrayList<>();
        for (String filePath : filePaths) {
            if (filePathPercentageMap.containsKey(filePath) && filePathPercentageMap.get(filePath) != 0) {
                sortedFilePaths.add(filePath);
            } else {
                fileModificationTrackerService.removeModification(modificationIdMap.get(filePath));
            }
        }
        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification...");
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Multi-File Modification", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                sortedFilePaths.sort((filePath1, filePath2) -> Double.compare(filePathPercentageMap.get(filePath2), filePathPercentageMap.get(filePath1)));
                List<String> utilizedFilePaths = new ArrayList<>();
                String initialFilePath = sortedFilePaths.get(0);
                String code = codeMap.get(initialFilePath);
                String initialQuestion = "I'm looking to implement the following modification to my program: \"" + modification + "\". First things first, Yes or No: Will you be able to achieve this modification completely solely by changing the code I provide here at " + sortedFilePaths.get(0) + ": \"" + code + "\". Please provide the answer in the following JSON format: \"{ modificationAchievable: Boolean!, reasoning: String }\" where reasoning is only optionally required if modificationAchievable is false.";
                Inquiry newInquiry = inquiryDao.createGeneralInquiry(initialQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                InquiryChat mostRecentInquiryChat = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                String json = JsonExtractorService.extractJsonObject(mostRecentInquiryChat.getMessage());
                HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat.getId(), null, false, null);
                HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                priorContext.add(priorContextObject);
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
                    HistoricalContextInquiryHolder inquiryContext2 = new HistoricalContextInquiryHolder(fixInquiry.getId(), fixInquiryChat.getId(), null, false, null);
                    HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(inquiryContext2);
                    priorContext.add(priorContextObject2);
                    modificationImplementableResponse = gson.fromJson(fixJson, ModificationImplementableResponse.class);
                }
                assert modificationImplementableResponse != null;
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification... (1 file processed)");
                utilizedFilePaths.add(initialFilePath);
                if (!modificationImplementableResponse.getModificationAchievable()) {
                    for (int i = 1; i < sortedFilePaths.size(); i++) {
                        String filePath = sortedFilePaths.get(i);
                        code = codeMap.get(filePath);
                        String question = "What about if I add this code at " + filePath + " to that condition: \"" + code + "\". Could I achieve implementing the entire modification now? Yes or No? Please provide the answer in the following JSON format: \"{ modificationAchievable: Boolean!, reasoning: String }\" where reasoning is only optionally required if modificationAchievable is false.";
                        newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                        mostRecentInquiryChat = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        String json2 = JsonExtractorService.extractJsonObject(mostRecentInquiryChat.getMessage());
                        inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat.getId(), null, false, null);
                        priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                        priorContext.add(priorContextObject);
                        ModificationImplementableResponse modificationImplementableResponse2 = null;
                        boolean badJson2 = false;
                        try {
                            modificationImplementableResponse2 = gson.fromJson(json2, ModificationImplementableResponse.class);
                        } catch (JsonSyntaxException jsonSyntaxException) {
                            badJson2 = true;
                        }
                        if (badJson2) {
                            String fixQuestion = "I wasn't able to parse that Json response. Could you provide the answer in the following JSON format: \"{ modificationAchievable: Boolean!, reasoning: String }\" where reasoning is only optionally required if modificationAchievable is false?";
                            Inquiry fixInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), fixQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                            InquiryChat fixInquiryChat = newInquiry.getChats().get(fixInquiry.getChats().size() - 1);
                            String fixJson = JsonExtractorService.extractJsonObject(fixInquiryChat.getMessage());
                            HistoricalContextInquiryHolder inquiryContext2 = new HistoricalContextInquiryHolder(fixInquiry.getId(), fixInquiryChat.getId(), null, false, null);
                            HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(inquiryContext2);
                            priorContext.add(priorContextObject2);
                            modificationImplementableResponse2 = gson.fromJson(fixJson, ModificationImplementableResponse.class);
                        }
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification... (" + i + 1 + " files processed)");
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
                        String suggestionId = desktopCodeModificationResponseResource.getModificationSuggestions().get(0).getId();
                        HistoricalContextModificationHolder modificationContext = new HistoricalContextModificationHolder(suggestionId, RecordType.FILE_MODIFICATION_SUGGESTION, false, null);
                        priorContextObject = new HistoricalContextObjectHolder(modificationContext);
                        priorContext.add(priorContextObject);
                        modificationsPriorContext.add(priorContextObject);
                    } else {
                        fileModificationTrackerService.errorFileModification(modificationIdMap.get(filePath));
                        if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                            FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(null, filePath, "", ModificationType.MODIFY);
                            fileModificationErrorDialog.setVisible(true);
                        } else {
                            FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(null, filePath, desktopCodeModificationResponseResource.getError(), ModificationType.MODIFY);
                            fileModificationErrorDialog.setVisible(true);
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
                }
                for (String filePath : filesToProcess) {
                    String fileCode = codeMap.get(filePath);
                    String question = "Analyze this code: \"" + fileCode + "\" at " + filePath + "\". Does this file need to be changed in order to fulfill the requested modification: \"" + modification + "\"  Yes or No? Please provide the answer in the following JSON format: \"{ modificationNeeded: Boolean!, reasoning: String }\".";
                    Inquiry finalInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), modificationsPriorContext);
                    InquiryChat latestInquiryChat = finalInquiry.getChats().get(finalInquiry.getChats().size() - 1);
                    if (latestInquiryChat.getMessage() == null) {
                        finalInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), modificationsPriorContext);
                        latestInquiryChat = finalInquiry.getChats().get(finalInquiry.getChats().size() - 1);
                    }
                    String json2 = JsonExtractorService.extractJsonObject(latestInquiryChat.getMessage());
                    ModificationNeededResponse modificationNeededResponse = null;
                    boolean badJson2 = false;
                    if (json2 != null) {
                        try {
                            modificationNeededResponse = gson.fromJson(json2, ModificationNeededResponse.class);
                        } catch (JsonSyntaxException jsonSyntaxException) {
                            badJson2 = true;
                        }
                    } else {
                        badJson2 = true;
                    }
                    if (badJson2) {
                        String fixQuestion = "I wasn't able to parse that Json response. Could you provide the answer in the following JSON format: \"{ modificationNeeded: Boolean!, reasoning: String }\"?";
                        Inquiry fixInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), fixQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                        InquiryChat fixInquiryChat = newInquiry.getChats().get(fixInquiry.getChats().size() - 1);
                        String fixJson = JsonExtractorService.extractJsonObject(fixInquiryChat.getMessage());
                        modificationNeededResponse = gson.fromJson(fixJson, ModificationNeededResponse.class);
                    }
                    if (modificationNeededResponse.isModificationNeeded()) {
                        String modificationForThisFile = "The modifications that need to be applied to this code to achieve this modification: (" + modification + ")";
                        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, fileCode, modificationForThisFile, ModificationType.MODIFY, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), new ArrayList<>());
                        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
                        if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                            fileModificationTrackerService.readyFileModificationUpdate(modificationIdMap.get(filePath), desktopCodeModificationResponseResource.getModificationSuggestions());
                        } else {
                            fileModificationTrackerService.errorFileModification(modificationIdMap.get(filePath));
                            if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                                FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(null, filePath, "", ModificationType.MODIFY);
                                fileModificationErrorDialog.setVisible(true);
                            } else {
                                FileModificationErrorDialog fileModificationErrorDialog = fileModificationErrorDialogFactory.create(null, filePath, desktopCodeModificationResponseResource.getError(), ModificationType.MODIFY);
                                fileModificationErrorDialog.setVisible(true);
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

    @Override
    public void fixCodeFiles(List<String> filePaths, String error, List<HistoricalContextObjectHolder> priorContext) throws InterruptedException {
        String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(error);
        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/4) Ranking File Modification Likelihood");
        Map<String, Double> filePathPercentageMap = new HashMap<>();
        Map<String, String> codeMap = new HashMap<>();
        Map<String, String> modificationIdMap = new HashMap<>();
        for (String filePath : filePaths) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (file != null) {
                ApplicationManager.getApplication().invokeAndWait(() -> {
                    //FileEditorManager.getInstance(project).openFile(file, true);
                    String code = codeSnippetExtractorService.getAllText(filePath);
                    String modificationId = fileModificationTrackerService.addModification(filePath, error, 0, code.length(), ModificationType.FIX, priorContext);
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
        Map<String, Boolean> booleanMap = new HashMap<>();
        Map<String, Boolean> activatedMap = new HashMap<>();

        for (String filePath : filePaths) {
            booleanMap.put(filePath, false);
        }
        BooleanWaiter filePathLikelihoodWaiter = new BooleanWaiter(booleanMap);

        for (String filePath : filePaths) {
            Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Multi-File Modification", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    if (activatedMap.containsKey(filePath)) {
                        System.out.println("Duplicate process attempted. Aborted.");
                        return;
                    }
                    activatedMap.put(filePath, true);
                    if (modificationIdMap.containsKey(filePath)) {
                        String code = codeMap.get(filePath);
                        String question = "I'm having the following problem/error with my program: \"" + error + "\". First things first, what is the percentage likelihood that this specific code file has anything to do with this error and/or will needed to be modified to fix the above error?: \"" + code + "\". Please provide the answer in the following JSON format: \"{ likelihoodPercentage: Float!, reasoning: String }\" where likelihoodPercentage is from 0 to 100.0";
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
                        filePathLikelihoodWaiter.setTrue(filePath);
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/4) Ranking File Modification Likelihood (" + filePathPercentageMap.values().size() + "/" + filePaths.size() + ")");
                    }
                }
            };
            ProgressManager.getInstance().run(backgroundTask);
        }
        filePathLikelihoodWaiter.waitForAllTrue();

        List<String> sortedFilePaths = new ArrayList<>();
        for (String filePath : filePaths) {
            if (filePathPercentageMap.containsKey(filePath) && filePathPercentageMap.get(filePath) != 0) {
                sortedFilePaths.add(filePath);
            } else {
                fileModificationTrackerService.removeModification(modificationIdMap.get(filePath));
            }
        }
        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification...");
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Multi-File Modification", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                sortedFilePaths.sort((filePath1, filePath2) -> Double.compare(filePathPercentageMap.get(filePath2), filePathPercentageMap.get(filePath1)));
                List<String> utilizedFilePaths = new ArrayList<>();
                String initialFilePath = sortedFilePaths.get(0);
                String code = codeMap.get(initialFilePath);
                String initialQuestion = "I'm having the following problem/error with my program: \"" + error + "\". First things first, Yes or No: Will you be able to fix this problem/error solely by changing the code I provide here at " + sortedFilePaths.get(0) + "?: \"" + code + "\". Please provide the answer in the following JSON format: \"{ fixAchievable: Boolean!, reasoning: String }\" where reasoning is only optionally required if fixAchievable is false.";
                Inquiry newInquiry = inquiryDao.createGeneralInquiry(initialQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                InquiryChat mostRecentInquiryChat = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                String json = JsonExtractorService.extractJsonObject(mostRecentInquiryChat.getMessage());
                HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat.getId(), null, false, null);
                HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                priorContext.add(priorContextObject);
                FixImplementableResponse fixImplementableResponse = null;
                boolean badJson = false;
                try {
                    fixImplementableResponse = gson.fromJson(json, FixImplementableResponse.class);
                } catch (JsonSyntaxException jsonSyntaxException) {
                    badJson = true;
                }
                if (badJson) {
                    String fixQuestion = "I wasn't able to parse that Json response. Could you provide the answer in the following JSON format: \"{ likelihoodPercentage: Float!, reasoning: String }\" where likelihoodPercentage is from 0 to 100.0?";
                    Inquiry fixInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), fixQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                    InquiryChat fixInquiryChat = newInquiry.getChats().get(fixInquiry.getChats().size() - 1);
                    String fixJson = JsonExtractorService.extractJsonObject(fixInquiryChat.getMessage());
                    HistoricalContextInquiryHolder inquiryContext2 = new HistoricalContextInquiryHolder(fixInquiry.getId(), fixInquiryChat.getId(), null, false, null);
                    HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(inquiryContext2);
                    priorContext.add(priorContextObject2);
                    fixImplementableResponse = gson.fromJson(fixJson, FixImplementableResponse.class);
                }
                assert fixImplementableResponse != null;
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification... (1 file processed)");
                utilizedFilePaths.add(initialFilePath);
                if (!fixImplementableResponse.getFixAchievable()) {
                    for (int i = 1; i < sortedFilePaths.size(); i++) {
                        String filePath = sortedFilePaths.get(i);
                        code = codeMap.get(filePath);
                        String question = "What about if I add this code at " + filePath + " to that condition: \"" + code + "\". Could I achieve implementing the entire modification now? Yes or No? Please provide the answer in the following JSON format: \"{ fixAchievable: Boolean!, reasoning: String }\" where reasoning is only optionally required if fixAchievable is false.";
                        newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                        mostRecentInquiryChat = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        String json2 = JsonExtractorService.extractJsonObject(mostRecentInquiryChat.getMessage());
                        inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat.getId(), null, false, null);
                        priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                        priorContext.add(priorContextObject);
                        FixImplementableResponse fixImplementableResponse2 = null;
                        boolean badJson2 = false;
                        try {
                            fixImplementableResponse2 = gson.fromJson(json2, FixImplementableResponse.class);
                        } catch (JsonSyntaxException jsonSyntaxException) {
                            badJson2 = true;
                        }
                        if (badJson2) {
                            String fixQuestion = "I wasn't able to parse that Json response. Could you provide the answer in the following JSON format: \"{ fixAchievable: Boolean!, reasoning: String }\" where reasoning is only optionally required if fixAchievable is false?";
                            Inquiry fixInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), fixQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                            InquiryChat fixInquiryChat = newInquiry.getChats().get(fixInquiry.getChats().size() - 1);
                            String fixJson = JsonExtractorService.extractJsonObject(fixInquiryChat.getMessage());
                            HistoricalContextInquiryHolder inquiryContext2 = new HistoricalContextInquiryHolder(fixInquiry.getId(), fixInquiryChat.getId(), null, false, null);
                            HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(inquiryContext2);
                            priorContext.add(priorContextObject2);
                            fixImplementableResponse2 = gson.fromJson(fixJson, FixImplementableResponse.class);
                        }
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/4) Determining minimum files necessary to complete modification... (" + i + 1 + " files processed)");
                        utilizedFilePaths.add(initialFilePath);
                        if (fixImplementableResponse2.getFixAchievable()) {
                            break;
                        }
                    }
                }
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/4) Fixing necessary files...");
                List<HistoricalContextObjectHolder> modificationsPriorContext = new ArrayList<>();
                for (int i = 0; i < utilizedFilePaths.size(); i++) {
                    String filePath = utilizedFilePaths.get(i);
                    code = codeMap.get(filePath);
                    DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, ModificationType.MODIFY, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                    DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getFixedCode(desktopCodeModificationRequestResource);
                    if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                        fileModificationTrackerService.readyFileModificationUpdate(modificationIdMap.get(filePath), desktopCodeModificationResponseResource.getModificationSuggestions());
                        String suggestionId = desktopCodeModificationResponseResource.getModificationSuggestions().get(0).getId();
                        HistoricalContextModificationHolder modificationContext = new HistoricalContextModificationHolder(suggestionId, RecordType.FILE_MODIFICATION_SUGGESTION, false, null);
                        priorContextObject = new HistoricalContextObjectHolder(modificationContext);
                        priorContext.add(priorContextObject);
                        modificationsPriorContext.add(priorContextObject);
                    } else {
                        fileModificationTrackerService.errorFileModification(modificationIdMap.get(filePath));
                        if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                            //FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, null, filePath, null, ModificationType.FIX, openAiApiKeyService, openAiModelService, fileModificationTrackerService, fileModificationRestarterService);
                            //fileModificationErrorDialog.setVisible(true);
                        } else {
                            //FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, null, filePath, desktopCodeModificationResponseResource.getError(), ModificationType.FIX, openAiApiKeyService, openAiModelService, fileModificationTrackerService, fileModificationRestarterService);
                            //fileModificationErrorDialog.setVisible(true);
                        }
                    }
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/4) Fixing necessary files... (" + i + 1 + " file(s) modified)");
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
                }
                for (String filePath : filesToProcess) {
                    String fileCode = codeMap.get(filePath);
                    String question = "Analyze this code: \"" + fileCode + "\" at " + filePath + "\". Does this file need to be changed in order to fix the above error/problem: \"" + error + "\"  Yes or No? Please provide the answer in the following JSON format: \"{ modificationNeeded: Boolean!, reasoning: String }\".";
                    Inquiry finalInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), modificationsPriorContext);
                    InquiryChat latestInquiryChat = finalInquiry.getChats().get(finalInquiry.getChats().size() - 1);
                    if (latestInquiryChat.getMessage() == null) {
                        finalInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), modificationsPriorContext);
                        latestInquiryChat = finalInquiry.getChats().get(finalInquiry.getChats().size() - 1);
                    }
                    String json2 = JsonExtractorService.extractJsonObject(latestInquiryChat.getMessage());
                    ModificationNeededResponse modificationNeededResponse = null;
                    boolean badJson2 = false;
                    if (json2 != null) {
                        try {
                            modificationNeededResponse = gson.fromJson(json2, ModificationNeededResponse.class);
                        } catch (JsonSyntaxException jsonSyntaxException) {
                            badJson2 = true;
                        }
                    } else {
                        badJson2 = true;
                    }
                    if (badJson2) {
                        String fixQuestion = "I wasn't able to parse that Json response. Could you provide the answer in the following JSON format: \"{ modificationNeeded: Boolean!, reasoning: String }\"?";
                        Inquiry fixInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat.getId(), fixQuestion, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                        InquiryChat fixInquiryChat = newInquiry.getChats().get(fixInquiry.getChats().size() - 1);
                        String fixJson = JsonExtractorService.extractJsonObject(fixInquiryChat.getMessage());
                        modificationNeededResponse = gson.fromJson(fixJson, ModificationNeededResponse.class);
                    }
                    if (modificationNeededResponse.isModificationNeeded()) {
                        DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, fileCode, error, ModificationType.MODIFY, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), new ArrayList<>());
                        DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getFixedCode(desktopCodeModificationRequestResource);
                        if (desktopCodeModificationResponseResource.getModificationSuggestions() != null && desktopCodeModificationResponseResource.getModificationSuggestions().size() > 0) {
                            fileModificationTrackerService.readyFileModificationUpdate(modificationIdMap.get(filePath), desktopCodeModificationResponseResource.getModificationSuggestions());
                        } else {
                            fileModificationTrackerService.errorFileModification(modificationIdMap.get(filePath));
                            if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                                //FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, null, filePath, null, ModificationType.FIX, openAiApiKeyService, openAiModelService, fileModificationTrackerService, fileModificationRestarterService);
                                //FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, null, filePath, null, ModificationType.FIX, openAiApiKeyService, openAiModelService, fileModificationTrackerService, fileModificationRestarterService);
                                //fileModificationErrorDialog.setVisible(true);
                            } else {
                                //FileModificationErrorDialog fileModificationErrorDialog = new FileModificationErrorDialog(null, null, filePath, desktopCodeModificationResponseResource.getError(), ModificationType.FIX, openAiApiKeyService, openAiModelService, fileModificationTrackerService, fileModificationRestarterService);
                                //fileModificationErrorDialog.setVisible(true);
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
