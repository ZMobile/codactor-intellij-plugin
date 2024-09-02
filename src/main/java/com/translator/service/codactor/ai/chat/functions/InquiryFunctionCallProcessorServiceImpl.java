package com.translator.service.codactor.ai.chat.functions;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirective;
import com.translator.model.codactor.ide.psi.error.ErrorResult;
import com.translator.model.codactor.ide.psi.implementation.ImplementationResultsResource;
import com.translator.model.codactor.ide.psi.usage.UsageResultsResource;
import com.translator.model.codactor.ide.file.search.SearchResponseResource;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.data.InquiryDataReferenceHolder;
import com.translator.model.codactor.ai.chat.function.GptFunctionCall;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.ai.modification.data.FileModificationDataReferenceHolder;
import com.translator.model.codactor.ai.modification.data.FileModificationRangeData;
import com.translator.service.codactor.ai.modification.AiCodeModificationService;
import com.translator.service.codactor.ai.modification.queued.QueuedFileModificationObjectHolderQueryService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ide.directory.FileDirectoryStructureQueryService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.CodeSnippetIndexGetterService;
import com.translator.service.codactor.ide.editor.psi.FindErrorService;
import com.translator.service.codactor.ide.editor.psi.FindImplementationsService;
import com.translator.service.codactor.ide.editor.psi.FindUsagesService;
import com.translator.service.codactor.ide.file.FileOpenerService;
import com.translator.service.codactor.ide.file.SelectedFileFetcherService;
import com.translator.service.codactor.ai.chat.functions.search.ProjectSearchService;
import com.translator.service.codactor.json.JsonExtractorService;
import com.translator.service.codactor.ai.modification.AiCodeModificationRecorderService;
import com.translator.service.codactor.ai.modification.AiFileModificationRestarterService;
import com.translator.service.codactor.ai.modification.history.FileModificationHistoryService;
import com.translator.service.codactor.ai.modification.json.FileModificationDataHolderJsonCompatibilityService;
import com.translator.service.codactor.ai.runner.CodeRunnerService;
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
    private final QueuedFileModificationObjectHolderQueryService queuedFileModificationObjectHolderQueryService;
    private final FileModificationHistoryService fileModificationHistoryService;
    private final AiFileModificationRestarterService aiFileModificationRestarterService;
    private final AiCodeModificationRecorderService aiCodeModificationRecorderService;
    private final AiCodeModificationService aiCodeModificationService;
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
                                                   QueuedFileModificationObjectHolderQueryService queuedFileModificationObjectHolderQueryService,
                                                   FileModificationHistoryService fileModificationHistoryService,
                                                   AiFileModificationRestarterService aiFileModificationRestarterService,
                                                   AiCodeModificationRecorderService aiCodeModificationRecorderService,
                                                   AiCodeModificationService aiCodeModificationService,
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
        this.queuedFileModificationObjectHolderQueryService = queuedFileModificationObjectHolderQueryService;
        this.fileModificationHistoryService = fileModificationHistoryService;
        this.aiFileModificationRestarterService = aiFileModificationRestarterService;
        this.aiCodeModificationRecorderService = aiCodeModificationRecorderService;
        this.aiCodeModificationService = aiCodeModificationService;
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
    public String processFunctionCall(Inquiry inquiry, GptFunctionCall gptFunctionCall) {
        try {
            if (gptFunctionCall.getName().equals("get_project_base_path")) {
                return project.getBasePath();
            } else if (gptFunctionCall.getName().equals("read_current_selected_file_in_editor")) {
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
            } else if (gptFunctionCall.getName().equals("read_current_selected_files_in_tree_view")) {
                VirtualFile[] virtualFiles = selectedFileFetcherService.getSelectedFilesInTreeView();
                List<String> filePaths = new ArrayList<>();
                for (VirtualFile virtualFile : virtualFiles) {
                    filePaths.add(virtualFile.getPath());
                }
                Map<String, Object> contentMap = new HashMap<>();
                contentMap.put("filePaths", filePaths);
                return gson.toJson(contentMap);
            } else if (gptFunctionCall.getName().equals("read_file_at_package")) {
                String packageName = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "package");
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
            } else if (gptFunctionCall.getName().equals("read_file_at_path")) {
                String filePath = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
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
            } else if (gptFunctionCall.getName().equals("open_file_at_path_for_user")) {
                String filePath = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                fileOpenerService.openFileInEditor(filePath);
                return "File opened in editor.";
            } else if (gptFunctionCall.getName().equals("read_directory_structure_at_path")) {
                String filePath = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                String depth = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "depth");
                assert depth != null;
                int depthInt = Integer.parseInt(depth);
                return fileDirectoryStructureQueryService.getDirectoryStructureAsJson(filePath, depthInt);
            } else if (gptFunctionCall.getName().equals("get_recent_historical_modifications")) {
                List<FileModificationDataHolder> fileModificationDataHolderList = fileModificationHistoryService.getRecentHistoricalFileModifications();
                List<FileModificationDataReferenceHolder> fileModificationDataReferenceHolderList = fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService.convert(fileModificationDataHolderList);
                return gson.toJson(fileModificationDataReferenceHolderList);
            } else if (gptFunctionCall.getName().equals("read_modification")) {
                String id = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "id");
                FileModificationDataHolder fileModificationDataHolder = fileModificationHistoryService.getModification(id);
                FileModificationDataHolder newFileModificationDataHolder = fileModificationDataHolderJsonCompatibilityService.makeFileModificationDataHolderCompatibleWithJson(fileModificationDataHolder);
                return gson.toJson(newFileModificationDataHolder);
            } else if (gptFunctionCall.getName().equals("get_queued_modifications")) {
                List<FileModificationDataHolder> fileModificationDataHolderList = queuedFileModificationObjectHolderQueryService.getQueuedFileModificationObjectHolders();
                List<FileModificationDataReferenceHolder> fileModificationDataReferenceHolderList = fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService.convert(fileModificationDataHolderList);
                return gson.toJson(fileModificationDataReferenceHolderList);
            } else if (gptFunctionCall.getName().equals("read_modification_in_queue_at_position")) {
                String position = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "position");
                List<FileModificationDataHolder> fileModificationDataHolderList = queuedFileModificationObjectHolderQueryService.getQueuedFileModificationObjectHolders();
                assert position != null;
                if (fileModificationDataHolderList.size() <= Integer.parseInt(position)) {
                    return "Error: Invalid position";
                }
                FileModificationDataHolder fileModificationDataHolder = fileModificationDataHolderList.get(Integer.parseInt(position));
                FileModificationDataHolder newFileModificationDataHolder = fileModificationDataHolderJsonCompatibilityService.makeFileModificationDataHolderCompatibleWithJson(fileModificationDataHolder);
                return gson.toJson(newFileModificationDataHolder);
            } else if (gptFunctionCall.getName().equals("get_recent_historical_inquiries")) {
                List<Inquiry> historicalInquiryList = inquiryDao.getRecentInquiries();
                List<InquiryDataReferenceHolder> inquiryDataReferenceHolders = new ArrayList<>();
                for (Inquiry recentInquiry : historicalInquiryList) {
                    InquiryDataReferenceHolder inquiryDataReferenceHolder = new InquiryDataReferenceHolder(recentInquiry);
                    inquiryDataReferenceHolders.add(inquiryDataReferenceHolder);
                }
                return gson.toJson(inquiryDataReferenceHolders);
            } else if (gptFunctionCall.getName().equals("read_inquiry")) {
                String id = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "id");
                Inquiry queriedInquiry = inquiryDao.getInquiry(id);
                return gson.toJson(queriedInquiry);
            } else if (gptFunctionCall.getName().equals("retry_modification_in_queue")) {
                String id = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "id");
                FileModification fileModification = fileModificationTrackerService.getModification(id);
                aiFileModificationRestarterService.restartFileModification(fileModification);
                return "Modification restarted.";
            } else if (gptFunctionCall.getName().equals("remove_modification_in_queue")) {
                String id = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "id");
                FileModification fileModification = fileModificationTrackerService.getModification(id);
                fileModificationTrackerService.removeModification(fileModification.getId());
                return "Modification removed.";
            } else if (gptFunctionCall.getName().equals("request_file_modification")) {
                String path = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                if (path == null) {
                    String packageName = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "package");
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
                String description = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "description");
                String replacementCodeSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "replacementCodeSnippet");
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
                String codeSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "codeSnippet");
                String startSnippetString = null;
                String endSnippetString = null;
                if (codeSnippetString != null) {
                    startIndex = codeSnippetIndexGetterService.getStartIndex(code, codeSnippetString);
                    endIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(code, startIndex, codeSnippetString);
                } else {
                    startSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "startBoundary");

                    if (startSnippetString != null) {
                        try {
                            startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                        } catch (NumberFormatException e) {
                            startIndex = 0;
                        }
                    } else {
                        startIndex = 0;
                    }
                    endSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "endBoundary");
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
                            if (startIndex == -1) {
                                return "Error: Start boundary not found in code snippet.\n"
                                        + " Code: " + code
                                        + " Start boundary searched: " + startSnippetString;
                            }
                        } catch (NumberFormatException e) {
                            startIndex = 0;
                        }
                        try {
                            endIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(code, startIndex, endSnippetString);
                            if (endIndex == -1) {
                                return "Error: End boundary not found in targeted code snippet.\n"
                                        + " Code: " + code
                                        + " End boundary searched: " + endSnippetString;
                            }
                        } catch (NumberFormatException e) {
                            endIndex = code.length();
                        }
                    }
                }
                if (startIndex < 0) {
                    startIndex = 0;
                }
                String modificationTypeString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "modificationType");
                assert modificationTypeString != null;
                ModificationType modificationType;
                String modificationId = "Error: no modification type specified.";
                switch (modificationTypeString) {
                    case "modify":
                        if (startSnippetString == null && endSnippetString == null) {
                            modificationType = ModificationType.MODIFY;
                            modificationId = aiCodeModificationRecorderService.getModifiedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        } else {
                            System.out.println("Start snippet string: " + startSnippetString + " +\n End snippet string: " + endSnippetString);
                            modificationType = ModificationType.MODIFY_SELECTION;
                            modificationId = aiCodeModificationRecorderService.getModifiedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        }
                        break;
                    case "fix":
                        if (startSnippetString == null && endSnippetString == null) {
                            modificationType = ModificationType.FIX;
                            modificationId = aiCodeModificationRecorderService.getFixedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        } else {
                            modificationType = ModificationType.FIX_SELECTION;
                            modificationId = aiCodeModificationRecorderService.getFixedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        }
                        break;
                    case "create":
                        modificationId = aiCodeModificationRecorderService.getCreatedCode(path, description, new ArrayList<>(), replacementCodeSnippetString);
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
            } else if (gptFunctionCall.getName().equals("request_file_creation")) {
                String path = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                String description = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "description");
                String code = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "code");
                aiCodeModificationRecorderService.getCreatedCodeFile(path, description, code);
                return "{" +
                        "\"message\": \"Modification requested\"" +
                        "}";
            } else if (gptFunctionCall.getName().equals("request_file_deletion")) {
                String path = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                aiCodeModificationService.getDeletedCodeFile(path);
                return "{" +
                        "\"message\": \"Modification requested\"" +
                        "}";
            } else if (gptFunctionCall.getName().equals("run_program")) {
                String path = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                String interpreter = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "interpreter");
                codeRunnerService.runCode(path, interpreter);
                return "{" +
                        "\"message\": \"Program started\"" +
                        "}";
            } else if (gptFunctionCall.getName().equals("project_text_search")) {
                String query = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "query");
                String page = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "page");
                int pageInt;
                String pageSize = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "pageSize");
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
            } else if (gptFunctionCall.getName().equals("find_declarations_or_usages_of_code")) {
                String filePath = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                String codeSnippet = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "codeSnippet");
                UsageResultsResource usageResults = findUsagesService.findUsagesWithinRange(filePath, codeSnippet);
                return gson.toJson(usageResults);
            } else if (gptFunctionCall.getName().equals("find_implementations_of_code")) {
                String filePath = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                String codeSnippet = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "codeSnippet");
                ImplementationResultsResource implementationResults = findImplementationsService.findImplementationsWithinRange(filePath, codeSnippet);
                return gson.toJson(implementationResults);
            } else if (gptFunctionCall.getName().equals("find_compile_time_errors_in_code")) {
                String filePath = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                String codeSnippet = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "codeSnippet");

                boolean includeWarnings;
                if (JsonExtractorService.extractField(gptFunctionCall.getArguments(), "includeWarnings") == null) {
                    includeWarnings = false;
                } else {
                    includeWarnings = Boolean.parseBoolean(JsonExtractorService.extractField(gptFunctionCall.getArguments(), "includeWarnings"));
                }
                List<ErrorResult> errorResults = findErrorService.getErrorsWithinRange(filePath, codeSnippet, includeWarnings);
                return gson.toJson(errorResults);
            } else if (gptFunctionCall.getName().equalsIgnoreCase("create_and_run_unit_test")) {
                String filePath = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                String testDescription = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "description");
                String content = codeSnippetExtractorService.getAllText(filePath);
                //Collects test dependency info from maven and gradle files
                StringBuilder response = new StringBuilder();
                response.append("You have chosen to create and run a unit test for the following Java code file: \n");
                response.append("path: " + filePath + "\n");
                response.append("content: {" + content + "}\n");
                response.append("The following is the description of the test being conducted: " + testDescription + "\n");
                //response.append("The following are the unit test dependencies for this Java project: ");
                //response.append(mavenAndGradleDependencyCollectorService.collectProjectTestDependencies());
                response.append("\n");
                response.append("In order to run this test, you will need to use the provided functions to create a unit test to run, but you may also temporarily place logs in the subject code file which will be triggered by the unit tests.");
                CreateAndRunUnitTestDirective createAndRunUnitTestDirective = new CreateAndRunUnitTestDirective();
                createAndRunUnitTestDirective.getSession().setFilePath(filePath);
                createAndRunUnitTestDirective.getSession().setTestDescription(testDescription);
                inquiry.setActiveDirective(createAndRunUnitTestDirective);
                return response.toString();
            }
            return null;
        } catch (Exception e) {
            return "Error: The function call threw the following error and may be non functional: " + Arrays.toString(e.getStackTrace());
        }
    }
































    public String testMethod() {
        TestObject testObject = new TestObject("/Users/zantehays/IdeaProjects/code-translator-dev/code-translator-service/src/main/java/com/translator/service/user/UserServiceImpl.java",
                "@Override\n    public void createAccount(UserRecord.CreateRequest firebaseUserRequest, User user) throws FirebaseAuthException {",
                "logger.info(\"User (id\\u003d\" + user.getId() + \") account created.\");\n    }\n",
                "@Override\n    public void createAccount(UserRecord.CreateRequest firebaseUserRequest, User user) throws FirebaseAuthException {\n        firebaseAuthUserDao.create(firebaseUserRequest);\n        Map<String, Object> claims = new HashMap<>();\n        claims.put(\"USER\", true);\n        claims.put(\"ADMIN\", false);\n        UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(user.getId())\n                .setCustomClaims(claims);\n        userDao.put(user);\n        firebaseAuthUserDao.update(updateRequest);\n        UserRecordDetails userRecordDetails = new UserRecordDetails(user.getId());\n        userRecordDetailsService.createUserRecordDetails(userRecordDetails);\n        logger.info(\"User (id=\" + user.getId() + \") account created.\");\n        System.out.println(\"Wohoo\");\n    }",
                "modify",
                "Added a print statement at the end of createAccount method");
        String json = gson.toJson(testObject);
        String path = JsonExtractorService.extractField(json, "path");
        if (path == null) {
            String packageName = JsonExtractorService.extractField(json, "package");
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
        String description = JsonExtractorService.extractField(json, "description");
        String replacementCodeSnippetString = JsonExtractorService.extractField(json, "replacementCodeSnippet");
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
        String codeSnippetString = JsonExtractorService.extractField(json, "codeSnippet");
        String startSnippetString = null;
        String endSnippetString = null;
        if (codeSnippetString != null) {
            startIndex = codeSnippetIndexGetterService.getStartIndex(code, codeSnippetString);
            endIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(code, startIndex, codeSnippetString);
        } else {
            startSnippetString = JsonExtractorService.extractField(json, "startBoundary");

            if (startSnippetString != null) {
                try {
                    startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                    if (startIndex == -1) {
                        return "Error: Start boundary not found in code snippet.\n"
                                + " Code: " + code
                                + " Start boundary searched: " + startSnippetString;
                    }
                } catch (NumberFormatException e) {
                    startIndex = 0;
                }
            } else {
                startIndex = 0;
            }
            endSnippetString = JsonExtractorService.extractField(json, "endBoundary");
            if (endSnippetString != null) {
                System.out.println("This gets called 1");
                try {
                    System.out.println("This gets called 2: " + endSnippetString);
                    endIndex = codeSnippetIndexGetterService.getEndIndex(code, startSnippetString, endSnippetString);
                    if (endIndex == -1) {
                        return "Error: End boundary not found in code snippet.\n"
                                + " Code: " + code
                                + " End boundary searched: " + endSnippetString;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("This gets called 3");
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
                    endIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(code, startIndex, endSnippetString);
                } catch (NumberFormatException e) {
                    endIndex = code.length();
                }
            }
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        String modificationTypeString = JsonExtractorService.extractField(json, "modificationType");
        assert modificationTypeString != null;
        ModificationType modificationType;
        String modificationId = "Error: no modification type specified.";
        switch (modificationTypeString) {
            case "modify":
                if (startSnippetString == null && endSnippetString == null) {
                    modificationType = ModificationType.MODIFY;
                    modificationId = aiCodeModificationRecorderService.getModifiedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                } else {
                    System.out.println("Start snippet string: " + startSnippetString + " +\n End snippet string: " + endSnippetString);
                    modificationType = ModificationType.MODIFY_SELECTION;
                    modificationId = aiCodeModificationRecorderService.getModifiedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                }
                break;
            case "fix":
                if (startSnippetString == null && endSnippetString == null) {
                    modificationType = ModificationType.FIX;
                    modificationId = aiCodeModificationRecorderService.getFixedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                } else {
                    modificationType = ModificationType.FIX_SELECTION;
                    modificationId = aiCodeModificationRecorderService.getFixedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                }
                break;
            case "create":
                modificationId = aiCodeModificationRecorderService.getCreatedCode(path, description, new ArrayList<>(), replacementCodeSnippetString);
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
    }
}
