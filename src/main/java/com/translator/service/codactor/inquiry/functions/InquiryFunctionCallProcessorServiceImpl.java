package com.translator.service.codactor.inquiry.functions;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.history.data.HistoricalInquiryDataHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.data.InquiryDataReferenceHolder;
import com.translator.model.codactor.inquiry.function.ChatGptFunctionCall;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.model.codactor.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.modification.data.FileModificationDataReferenceHolder;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.json.JsonExtractorService;
import com.translator.service.codactor.modification.AutomaticCodeModificationService;
import com.translator.service.codactor.modification.FileModificationRestarterService;
import com.translator.service.codactor.modification.history.FileModificationHistoryService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.transformer.FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InquiryFunctionCallProcessorServiceImpl implements InquiryFunctionCallProcessorService {
    private final Gson gson;
    private final InquiryDao inquiryDao;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final FileModificationHistoryService fileModificationHistoryService;
    private final FileModificationRestarterService fileModificationRestarterService;
    private final AutomaticCodeModificationService automaticCodeModificationService;
    private final FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService;

    @Inject
    public InquiryFunctionCallProcessorServiceImpl(Gson gson,
                                                   InquiryDao inquiryDao,
                                                   CodeSnippetExtractorService codeSnippetExtractorService,
                                                   FileModificationTrackerService fileModificationTrackerService,
                                                   FileModificationHistoryService fileModificationHistoryService,
                                                   FileModificationRestarterService fileModificationRestarterService,
                                                   AutomaticCodeModificationService automaticCodeModificationService,
                                                   FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService) {
        this.gson = gson;
        this.inquiryDao = inquiryDao;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationHistoryService = fileModificationHistoryService;
        this.fileModificationRestarterService = fileModificationRestarterService;
        this.automaticCodeModificationService = automaticCodeModificationService;
        this.fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService = fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService;
    }

    @Override
    public String processFunctionCall(ChatGptFunctionCall chatGptFunctionCall) {
        if (chatGptFunctionCall.getName().equals("read_file_at_package")) {
            String packageName = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "package");
            VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
            if (virtualFile != null) {
                String content;
                Path path = Paths.get(virtualFile.getPath());
                try {
                    content = Files.readString(path, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();

                Map<String, Object> contentMap = new HashMap<>();
                contentMap.put("filePath", virtualFile.getPath());
                contentMap.put("filePackage", packageName);
                contentMap.put("content", content);
                return gson.toJson(contentMap);
            }
        } else if (chatGptFunctionCall.getName().equals("read_at_path")) {
            String filePath = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
            String content = codeSnippetExtractorService.getAllText(filePath);
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("filePath", filePath);
            contentMap.put("content", content);
            return gson.toJson(contentMap);
        } else if (chatGptFunctionCall.getName().equals("get_recent_historical_modifications")) {
            List<FileModificationDataHolder> fileModificationDataHolderList = fileModificationHistoryService.getRecentHistoricalFileModifications();
            List<FileModificationDataReferenceHolder> fileModificationDataReferenceHolderList = fileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService.convert(fileModificationDataHolderList);
            return gson.toJson(fileModificationDataReferenceHolderList);
        } else if (chatGptFunctionCall.getName().equals("read_modification")) {
            String id = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "id");
            FileModificationDataHolder fileModificationDataHolder = fileModificationHistoryService.getModification(id);
            return gson.toJson(fileModificationDataHolder);
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
            return gson.toJson(fileModificationDataHolder);
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
            fileModificationTrackerService.removeModification(fileModification.getId());
            fileModificationRestarterService.restartFileModification(fileModification);
        } else if (chatGptFunctionCall.getName().equals("remove_modification_in_queue")) {
            String id = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "id");
            FileModification fileModification = fileModificationTrackerService.getModification(id);
            fileModificationTrackerService.removeModification(fileModification.getId());
        } else if (chatGptFunctionCall.getName().equals("request_file_modification")) {
            String path = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
            String description = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "description");
            String startIndexString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "startIndex");
            int startIndex;
            if (startIndexString != null) {
                startIndex = Integer.parseInt(startIndexString);
            } else {
                startIndex = 0;
            }
            String endIndexString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "endIndex");
            int endIndex;
            if (endIndexString != null) {
                endIndex = Integer.parseInt(endIndexString);
            } else {
                String code = codeSnippetExtractorService.getAllText(path);
                endIndex = code.length();
            }
            String modificationTypeString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "modificationType");
            assert modificationTypeString != null;
            ModificationType modificationType;
            switch (modificationTypeString) {
                case "modify":
                    if (startIndexString == null && endIndexString == null) {
                        modificationType = ModificationType.MODIFY;
                        automaticCodeModificationService.getModifiedCode(path, description, modificationType, new ArrayList<>());
                    } else {
                        modificationType = ModificationType.MODIFY_SELECTION;
                        automaticCodeModificationService.getModifiedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>());
                    }
                    break;
                case "fix":
                    if (startIndexString == null && endIndexString == null) {
                        modificationType = ModificationType.FIX;
                        automaticCodeModificationService.getFixedCode(path, description, modificationType, new ArrayList<>());
                    } else {
                        modificationType = ModificationType.FIX_SELECTION;
                        automaticCodeModificationService.getFixedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>());
                    }
                    break;
                case "create":
                    automaticCodeModificationService.getCreatedCode(path, description, new ArrayList<>());
                    break;
            }
            return "{" +
                    "\"message\": \"Modification requested successfully!\"" +
                    "}";
        } else if (chatGptFunctionCall.getName().equals("request_file_modification_and_wait_for_response")) {
            String path = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
            String description = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "description");
            String startIndexString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "startIndex");
            int startIndex;
            if (startIndexString != null) {
                startIndex = Integer.parseInt(startIndexString);
            } else {
                startIndex = 0;
            }
            String endIndexString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "endIndex");
            int endIndex;
            if (endIndexString != null) {
                endIndex = Integer.parseInt(endIndexString);
            } else {
                String code = codeSnippetExtractorService.getAllText(path);
                endIndex = code.length();
            }
            String modificationTypeString = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "modificationType");
            assert modificationTypeString != null;
            ModificationType modificationType;
            switch (modificationTypeString) {
                case "modify":
                    if (startIndexString == null && endIndexString == null) {
                        modificationType = ModificationType.MODIFY;
                        automaticCodeModificationService.getModifiedCode(path, description, modificationType, new ArrayList<>());
                    } else {
                        modificationType = ModificationType.MODIFY_SELECTION;
                        automaticCodeModificationService.getModifiedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>());
                    }
                    break;
                case "fix":
                    if (startIndexString == null && endIndexString == null) {
                        modificationType = ModificationType.FIX;
                        automaticCodeModificationService.getFixedCode(path, description, modificationType, new ArrayList<>());
                    } else {
                        modificationType = ModificationType.FIX_SELECTION;
                        automaticCodeModificationService.getFixedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>());
                    }
                    break;
                case "create":
                    automaticCodeModificationService.getCreatedCode(path, description, new ArrayList<>());
                    break;

            }
        } else if (chatGptFunctionCall.getName().equals("request_file_creation")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_creation_and_wait_for_response")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_deletion")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_modification_and_wait_for_response_at_position")) {
        } else if (chatGptFunctionCall.getName().equals("run_program")) {
        }
        return null;
    }
}
