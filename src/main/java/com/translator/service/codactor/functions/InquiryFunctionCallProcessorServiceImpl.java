package com.translator.service.codactor.functions;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.editor.psi.error.ErrorResult;
import com.translator.model.codactor.editor.psi.implementation.ImplementationResultsResource;
import com.translator.model.codactor.editor.psi.usage.UsageResultsResource;
import com.translator.model.codactor.functions.search.SearchResponseResource;
import com.translator.model.codactor.history.HistoricalContextInquiryHolder;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.data.InquiryDataReferenceHolder;
import com.translator.model.codactor.inquiry.function.ChatGptFunctionCall;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationTracker;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.model.codactor.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.modification.data.FileModificationDataReferenceHolder;
import com.translator.model.codactor.modification.data.FileModificationRangeData;
import com.translator.service.codactor.directory.FileDirectoryStructureQueryService;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.editor.CodeSnippetIndexGetterService;
import com.translator.service.codactor.editor.psi.FindErrorService;
import com.translator.service.codactor.editor.psi.FindImplementationsService;
import com.translator.service.codactor.editor.psi.FindUsagesService;
import com.translator.service.codactor.file.FileOpenerService;
import com.translator.service.codactor.file.SelectedFileFetcherService;
import com.translator.service.codactor.functions.search.ProjectSearchService;
import com.translator.service.codactor.json.JsonExtractorService;
import com.translator.service.codactor.modification.CodeModificationService;
import com.translator.service.codactor.modification.CodeRecorderService;
import com.translator.service.codactor.modification.FileModificationRestarterService;
import com.translator.service.codactor.modification.history.FileModificationHistoryService;
import com.translator.service.codactor.modification.json.FileModificationDataHolderJsonCompatibilityService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.runner.CodeRunnerService;
import com.translator.service.codactor.transformer.FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService;
import com.translator.service.codactor.transformer.modification.FileModificationTrackerToFileModificationRangeDataTransformerService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class InquiryFunctionCallProcessorServiceImpl implements InquiryFunctionCallProcessorService {
    private final Gson gson;
    private final Project project;
    private final InquiryDao inquiryDao;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final FileModificationHistoryService fileModificationHistoryService;
    private final FileModificationRestarterService fileModificationRestarterService;
    private final CodeRecorderService codeRecorderService;
    private final CodeModificationService codeModificationService;
    private final FileDirectoryStructureQueryService fileDirectoryStructureQueryService;
    private final CodeRunnerService codeRunnerService;
    private final SelectedFileFetcherService selectedFileFetcherService;
    private final FileOpenerService fileOpenerService;
    private final FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService;
    private final FileModificationTrackerToFileModificationRangeDataTransformerService fileModificationTrackerToFileModificationRangeDataTransformerService;
    private final FileModificationDataHolderJsonCompatibilityService fileModificationDataHolderJsonCompatibilityService;
    private final ProjectSearchService projectSearchService;
    private final FindUsagesService findUsagesService;
    private final FindImplementationsService findImplementationsService;
    private final FindErrorService findErrorService;

    @Inject
    public InquiryFunctionCallProcessorServiceImpl(Gson gson,
                                                   Project project,
                                                   InquiryDao inquiryDao,
                                                   CodeSnippetExtractorService codeSnippetExtractorService,
                                                   CodeSnippetIndexGetterService codeSnippetIndexGetterService,
                                                   FileModificationTrackerService fileModificationTrackerService,
                                                   FileModificationHistoryService fileModificationHistoryService,
                                                   FileModificationRestarterService fileModificationRestarterService,
                                                   CodeRecorderService codeRecorderService,
                                                   CodeModificationService codeModificationService,
                                                   FileDirectoryStructureQueryService fileDirectoryStructureQueryService,
                                                   CodeRunnerService codeRunnerService,
                                                   SelectedFileFetcherService selectedFileFetcherService,
                                                   FileOpenerService fileOpenerService,
                                                   FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService,
                                                   FileModificationTrackerToFileModificationRangeDataTransformerService fileModificationTrackerToFileModificationRangeDataTransformerService,
                                                   FileModificationDataHolderJsonCompatibilityService fileModificationDataHolderJsonCompatibilityService,
                                                   ProjectSearchService projectSearchService,
                                                   FindUsagesService findUsagesService,
                                                   FindImplementationsService findImplementationsService,
                                                   FindErrorService findErrorService) {
        this.gson = gson;
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationHistoryService = fileModificationHistoryService;
        this.fileModificationRestarterService = fileModificationRestarterService;
        this.codeRecorderService = codeRecorderService;
        this.codeModificationService = codeModificationService;
        this.fileDirectoryStructureQueryService = fileDirectoryStructureQueryService;
        this.codeRunnerService = codeRunnerService;
        this.selectedFileFetcherService = selectedFileFetcherService;
        this.fileOpenerService = fileOpenerService;
        this.fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService = fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService;
        this.fileModificationTrackerToFileModificationRangeDataTransformerService = fileModificationTrackerToFileModificationRangeDataTransformerService;
        this.fileModificationDataHolderJsonCompatibilityService = fileModificationDataHolderJsonCompatibilityService;
        this.projectSearchService = projectSearchService;
        this.findUsagesService = findUsagesService;
        this.findImplementationsService = findImplementationsService;
        this.findErrorService = findErrorService;
    }

    @Override
    public String processFunctionCall(ChatGptFunctionCall chatGptFunctionCall, String inquiryId) {

        try {
            if (chatGptFunctionCall.getName().equals("get_project_base_path")) {
                return project.getBasePath();
            } else if (chatGptFunctionCall.getName().equals("read_current_selected_file_in_editor")) {
                VirtualFile[] virtualFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
                if (virtualFiles.length == 0) {
                    return "Error: No file selected in editor.";
                }
                String filePath = virtualFiles[0].getPath();
                String content = codeSnippetExtractorService.getAllText(filePath);
                if (content == null) {
                    //content = codeSnippetExtractorService.decompileClassFile(virtualFiles[0]);
                }
                Map<String, Object> contentMap = new HashMap<>();
                contentMap.put("filePath", filePath);
                contentMap.put("content", content);
                FileModificationTracker fileModificationTracker = fileModificationTrackerService.getModificationTracker(filePath);
                if (fileModificationTracker != null) {
                    List<FileModificationRangeData> fileModificationRangeData = fileModificationTrackerToFileModificationRangeDataTransformerService.convert(fileModificationTracker);
                    contentMap.put("currentActiveModificationsInThisFile", fileModificationRangeData);
                }
                return gson.toJson(contentMap);
            } else if (chatGptFunctionCall.getName().equals("read_current_selected_files_in_tree_view")) {
                VirtualFile[] virtualFiles = selectedFileFetcherService.getSelectedFilesInTreeView();
                List<String> filePaths = new ArrayList<>();
                for (VirtualFile virtualFile : virtualFiles) {
                    filePaths.add(virtualFile.getPath());
                }
                Map<String, Object> contentMap = new HashMap<>();
                contentMap.put("filePaths", filePaths);
                return gson.toJson(contentMap);
            } else if (chatGptFunctionCall.getName().equals("read_file_at_package")) {
                String packageName = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "package");
                VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                if (virtualFile != null) {
                    String content;
                    Path path = Paths.get(virtualFile.getPath());
                    Map<String, Object> contentMap = new HashMap<>();
                    if (!virtualFile.isDirectory()) {
                        try {
                            content = Files.readString(path, StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        contentMap.put("content", content);
                    } else {
                        contentMap.put("content", fileDirectoryStructureQueryService.getDirectoryStructureAsJson(virtualFile.getPath(), 1));
                    }
                    contentMap.put("filePath", virtualFile.getPath());
                    contentMap.put("filePackage", packageName);
                    FileModificationTracker fileModificationTracker = fileModificationTrackerService.getModificationTracker(virtualFile.getPath());
                    if (fileModificationTracker != null) {
                        List<FileModificationRangeData> fileModificationRangeData = fileModificationTrackerToFileModificationRangeDataTransformerService.convert(fileModificationTracker);
                        contentMap.put("currentActiveModificationsInThisFile", fileModificationRangeData);
                    }
                    return gson.toJson(contentMap);
                }
            } else if (chatGptFunctionCall.getName().equals("read_file_at_path")) {
                String filePath = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                String content = codeSnippetExtractorService.getAllText(filePath);
                Map<String, Object> contentMap = new HashMap<>();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        String packageName = filePath.replaceAll("/", ".");
                        VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                        if (virtualFile != null) {
                            filePath = virtualFile.getPath();
                            file = new File(filePath);
                        }
                    }
                    if (file.isDirectory()) {
                        contentMap.put("content", fileDirectoryStructureQueryService.getDirectoryStructureAsJson(filePath, 1));
                    } else {
                        contentMap.put("content", content);
                        FileModificationTracker fileModificationTracker = fileModificationTrackerService.getModificationTracker(filePath);
                        if (fileModificationTracker != null) {
                            List<FileModificationRangeData> fileModificationRangeData = fileModificationTrackerToFileModificationRangeDataTransformerService.convert(fileModificationTracker);
                            contentMap.put("currentActiveModificationsInThisFile", fileModificationRangeData);
                        }
                        if (content == null) {
                            contentMap.put("content", fileDirectoryStructureQueryService.getDirectoryStructureAsJson(filePath, 1));
                        } else {
                            contentMap.put("content", content);
                        }
                    }
                }
                contentMap.put("filePath", filePath);
                return gson.toJson(contentMap);
            } else if (chatGptFunctionCall.getName().equals("open_file_at_path_for_user")) {
                String filePath = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                fileOpenerService.openFileInEditor(filePath);
                return "File opened in editor.";
            } else if (chatGptFunctionCall.getName().equals("read_directory_structure_at_path")) {
                String filePath = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                String depth = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "depth");
                assert depth != null;
                int depthInt = Integer.parseInt(depth);
                return fileDirectoryStructureQueryService.getDirectoryStructureAsJson(filePath, depthInt);
            } else if (chatGptFunctionCall.getName().equals("get_recent_historical_modifications")) {
                List<FileModificationDataHolder> fileModificationDataHolderList = fileModificationHistoryService.getRecentHistoricalFileModifications();
                List<FileModificationDataReferenceHolder> fileModificationDataReferenceHolderList = fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService.convert(fileModificationDataHolderList);
                return gson.toJson(fileModificationDataReferenceHolderList);
            } else if (chatGptFunctionCall.getName().equals("read_modification")) {
                String id = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "id");
                FileModificationDataHolder fileModificationDataHolder = fileModificationHistoryService.getModification(id);
                FileModificationDataHolder newFileModificationDataHolder = fileModificationDataHolderJsonCompatibilityService.makeFileModificationDataHolderCompatibleWithJson(fileModificationDataHolder);
                return gson.toJson(newFileModificationDataHolder);
            } else if (chatGptFunctionCall.getName().equals("get_queued_modifications")) {
                List<FileModificationDataHolder> fileModificationDataHolderList = fileModificationTrackerService.getQueuedFileModificationObjectHolders();
                List<FileModificationDataReferenceHolder> fileModificationDataReferenceHolderList = fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService.convert(fileModificationDataHolderList);
                return gson.toJson(fileModificationDataReferenceHolderList);
            } else if (chatGptFunctionCall.getName().equals("read_modification_in_queue_at_position")) {
                String position = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "position");
                List<FileModificationDataHolder> fileModificationDataHolderList = fileModificationTrackerService.getQueuedFileModificationObjectHolders();
                assert position != null;
                if (fileModificationDataHolderList.size() <= Integer.parseInt(position)) {
                    return "Error: Invalid position";
                }
                FileModificationDataHolder fileModificationDataHolder = fileModificationDataHolderList.get(Integer.parseInt(position));
                FileModificationDataHolder newFileModificationDataHolder = fileModificationDataHolderJsonCompatibilityService.makeFileModificationDataHolderCompatibleWithJson(fileModificationDataHolder);
                return gson.toJson(newFileModificationDataHolder);
            } else if (chatGptFunctionCall.getName().equals("get_recent_historical_inquiries")) {
                List<Inquiry> historicalInquiryList = inquiryDao.getRecentInquiries();
                List<InquiryDataReferenceHolder> inquiryDataReferenceHolders = new ArrayList<>();
                for (Inquiry inquiry : historicalInquiryList) {
                    InquiryDataReferenceHolder inquiryDataReferenceHolder = new InquiryDataReferenceHolder(inquiry);
                    inquiryDataReferenceHolders.add(inquiryDataReferenceHolder);
                }
                return gson.toJson(inquiryDataReferenceHolders);
            } else if (chatGptFunctionCall.getName().equals("read_inquiry")) {
                String id = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "id");
                Inquiry inquiry = inquiryDao.getInquiry(id);
                return gson.toJson(inquiry);
            } else if (chatGptFunctionCall.getName().equals("retry_modification_in_queue")) {
                String id = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "id");
                FileModification fileModification = fileModificationTrackerService.getModification(id);
                fileModificationRestarterService.restartFileModification(fileModification);
                return "Modification restarted.";
            } else if (chatGptFunctionCall.getName().equals("remove_modification_in_queue")) {
                String id = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "id");
                FileModification fileModification = fileModificationTrackerService.getModification(id);
                fileModificationTrackerService.removeModification(fileModification.getId());
                return "Modification removed.";
            } else if (chatGptFunctionCall.getName().equals("request_file_modification")) {
                String path = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                if (path == null) {
                    String packageName = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "package");
                    if (packageName != null) {
                        VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                        if (virtualFile != null) {
                            path = virtualFile.getPath();
                        }
                    }
                }
                if (path == null) {
                    return "Error: File not found.";
                }
                String description = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "description");
                String replacementCodeSnippetString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "replacementCodeSnippet");
                if (replacementCodeSnippetString == null) {
                    return "Error: You need to provide your modification as a replacement code snippet to mark what will be replacing the code snippet you selected.";
                }
                int startIndex;
                int endIndex;
                String code = codeSnippetExtractorService.getAllText(path);
                if (code == null) {
                    String packageName = path.replaceAll("/", ".");
                    VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                    if (virtualFile != null) {
                        path = virtualFile.getPath();
                    }
                    code = codeSnippetExtractorService.getAllText(path);
                }
                if (code == null) {
                    return "Error: File not found";
                }
                String codeSnippetString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "codeSnippet");
                String startSnippetString = null;
                String endSnippetString = null;
                if (codeSnippetString != null) {
                    startIndex = codeSnippetIndexGetterService.getStartIndex(code, codeSnippetString);
                    endIndex = codeSnippetIndexGetterService.getEndIndex(code, startIndex, codeSnippetString);
                } else {
                    startSnippetString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "startBoundary");

                    if (startSnippetString != null) {
                        try {
                            startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                        } catch (NumberFormatException e) {
                            startIndex = 0;
                        }
                    } else {
                        startIndex = 0;
                    }
                    endSnippetString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "endBoundary");
                    if (endSnippetString != null) {
                        try {
                            endIndex = codeSnippetIndexGetterService.getEndIndex(code, startSnippetString, endSnippetString);
                        } catch (NumberFormatException e) {
                            endIndex = code.length();
                        }
                    } else {
                        endIndex = code.length();
                    }
                    // Check if startSnippetString is after endSnippetString, and swap if necessary
                    if (startSnippetString != null && endSnippetString != null && startIndex > endIndex) {
                        String temp = startSnippetString;
                        startSnippetString = endSnippetString;
                        endSnippetString = temp;
                        try {
                            startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                        } catch (NumberFormatException e) {
                            startIndex = 0;
                        }
                        try {
                            endIndex = codeSnippetIndexGetterService.getEndIndex(code, startIndex, endSnippetString);
                        } catch (NumberFormatException e) {
                            endIndex = code.length();
                        }
                    }
                }
                if (startIndex < 0) {
                    startIndex = 0;
                }
                String modificationTypeString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "modificationType");
                assert modificationTypeString != null;
                ModificationType modificationType;
                HistoricalContextObjectHolder inquiryContext = new HistoricalContextObjectHolder(new HistoricalContextInquiryHolder(inquiryId));
                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                priorContext.add(inquiryContext);
                String modificationId = "Error: no modification type specified.";
                switch (modificationTypeString) {
                    case "modify":
                        if (startSnippetString == null && endSnippetString == null) {
                            modificationType = ModificationType.MODIFY;
                            modificationId = codeRecorderService.getModifiedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        } else {
                            modificationType = ModificationType.MODIFY_SELECTION;
                            modificationId = codeRecorderService.getModifiedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        }
                        break;
                    case "fix":
                        if (startSnippetString == null && endSnippetString == null) {
                            modificationType = ModificationType.FIX;
                            modificationId = codeRecorderService.getFixedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        } else {
                            modificationType = ModificationType.FIX_SELECTION;
                            modificationId = codeRecorderService.getFixedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        }
                        break;
                    case "create":
                        modificationId = codeRecorderService.getCreatedCode(path, description, new ArrayList<>(), replacementCodeSnippetString);
                        break;
                }
                if (modificationId != null) {
                    if (modificationId.startsWith("Error")) {
                        return "{" +
                                "\"message\": \"" + modificationId + "\"" +
                                "}";
                    } else {
                        return "{" +
                                "\"message\": \"Modification requested. Modification id: " + modificationId + " \"" +
                                "}";
                    }
                } else {
                    return "{" +
                            "\"message\": \"Error: Unspecified.\"" +
                            "}";
                }
        /*} else if (chatGptFunctionCall.getName().equals("request_file_modification_and_wait_for_response")) {
            String path = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
            if (path == null) {
                String packageName = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "package");
                VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                if (virtualFile != null) {
                    path = virtualFile.getPath();
                }
            }
            String description = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "description");
            int startIndex;
            int endIndex;
            String code = codeSnippetExtractorService.getAllText(path);
            String codeSnippetString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "codeSnippet");
            String startSnippetString = null;
            String endSnippetString = null;
            if (codeSnippetString != null) {
                startIndex = codeSnippetIndexGetterService.getStartIndex(code, codeSnippetString);
                endIndex = codeSnippetIndexGetterService.getEndIndex(code, codeSnippetString);
            } else {
                startSnippetString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "startBoundary");
                if (startSnippetString != null) {
                    try {
                        startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                    } catch (NumberFormatException e) {
                        startIndex = 0;
                    }
                } else {
                    startIndex = 0;
                }
                endSnippetString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "endBoundary");
                if (endSnippetString != null) {
                    try {
                        endIndex = codeSnippetIndexGetterService.getEndIndex(code, endSnippetString);
                    } catch (NumberFormatException e) {
                        endIndex = code.length();
                    }
                } else {
                    endIndex = code.length();
                }
                // Check if startSnippetString is after endSnippetString, and swap if necessary
                if (startSnippetString != null && endSnippetString != null && startIndex > endIndex) {
                    String temp = startSnippetString;
                    startSnippetString = endSnippetString;
                    endSnippetString = temp;
                    try {
                        startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                    } catch (NumberFormatException e) {
                        startIndex = 0;
                    }
                    try {
                        endIndex = codeSnippetIndexGetterService.getEndIndex(code, endSnippetString);
                    } catch (NumberFormatException e) {
                        endIndex = code.length();
                    }
                }
            }
            String modificationTypeString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "modificationType");
            assert modificationTypeString != null;
            ModificationType modificationType;
            List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
            HistoricalContextObjectHolder inquiryContext = new HistoricalContextObjectHolder(new HistoricalContextInquiryHolder(inquiryId));
            priorContext.add(inquiryContext);
            switch (modificationTypeString) {
                case "modify":
                    if (startSnippetString == null && endSnippetString == null) {
                        modificationType = ModificationType.MODIFY;
                        codeRecorderService.getModifiedCodeAndWait(path, description, modificationType, new ArrayList<>());
                    } else {
                        modificationType = ModificationType.MODIFY_SELECTION;
                        codeRecorderService.getModifiedCodeAndWait(path, startIndex, endIndex, description, modificationType, new ArrayList<>());
                    }
                    break;
                case "fix":
                    if (startSnippetString == null && endSnippetString == null) {
                        modificationType = ModificationType.FIX;
                        codeRecorderService.getFixedCodeAndWait(path, description, modificationType, new ArrayList<>());
                    } else {
                        modificationType = ModificationType.FIX_SELECTION;
                        codeRecorderService.getFixedCodeAndWait(path, startIndex, endIndex, description, modificationType, new ArrayList<>());
                    }
                    break;
                case "create":
                    codeRecorderService.getCreatedCodeAndWait(path, description, new ArrayList<>());
                    break;
            }
            return "{" +
                    "\"message\": \"Modification requested\"" +
                    "}";
        */
            } else if (chatGptFunctionCall.getName().equals("request_file_creation")) {
                String path = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                String description = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "description");
                String code = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "code");
                codeRecorderService.getCreatedCodeFile(path, description, code);
                return "{" +
                        "\"message\": \"Modification requested\"" +
                        "}";
        /*} else if (chatGptFunctionCall.getName().equals("request_file_creation_and_wait_for_response")) {
            String path = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
            String description = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "description");
            codeRecorderService.getCreatedCodeFileAndWait(path, description);
            return "{" +
                    "\"message\": \"Modification requested\"" +
                    "}";
        */
            } else if (chatGptFunctionCall.getName().equals("request_file_deletion")) {
                String path = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                codeModificationService.getDeletedCodeFile(path);
                return "{" +
                        "\"message\": \"Modification requested\"" +
                        "}";
            } else if (chatGptFunctionCall.getName().equals("run_program")) {
                String path = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                String interpreter = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "interpreter");
                codeRunnerService.runCode(path, interpreter);
                return "{" +
                        "\"message\": \"Program started\"" +
                        "}";
            } else if (chatGptFunctionCall.getName().equals("project_text_search")) {
                String query = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "query");
                String page = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "page");
                int pageInt;
                String pageSize = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "pageSize");
                int pageSizeInt;
                if (page != null) {
                    pageInt = Integer.parseInt(page);
                } else {
                    pageInt = 1;
                }
                if (pageSize != null) {
                    pageSizeInt = Integer.parseInt(pageSize);
                } else {
                    pageSizeInt = 10;
                }
                SearchResponseResource searchResponseResource = projectSearchService.search(query, pageInt, pageSizeInt);
                return gson.toJson(searchResponseResource);
            } else if (chatGptFunctionCall.getName().equals("find_declarations_or_usages_of_code")) {
                String filePath = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                String codeSnippet = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "codeSnippet");
                UsageResultsResource usageResults = findUsagesService.findUsagesWithinRange(filePath, codeSnippet);
                return gson.toJson(usageResults);
            } else if (chatGptFunctionCall.getName().equals("find_implementations_of_code")) {
                String filePath = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                String codeSnippet = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "codeSnippet");
                ImplementationResultsResource implementationResults = findImplementationsService.findImplementationsWithinRange(filePath, codeSnippet);
                return gson.toJson(implementationResults);
            } else if (chatGptFunctionCall.getName().equals("find_compile_time_errors_in_code")) {
                String filePath = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
                String codeSnippet = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "codeSnippet");

                boolean includeWarnings;
                if (JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "includeWarnings") == null) {
                    includeWarnings = false;
                } else {
                    includeWarnings = Boolean.parseBoolean(JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "includeWarnings"));
                }
                List<ErrorResult> errorResults = findErrorService.getErrorsWithinRange(filePath, codeSnippet, includeWarnings);
                return gson.toJson(errorResults);
            }
            return null;
        } catch (Exception e) {
            return "Error: The function call threw the following error and may be non functional: " + Arrays.toString(e.getStackTrace());
        }
    }
}
