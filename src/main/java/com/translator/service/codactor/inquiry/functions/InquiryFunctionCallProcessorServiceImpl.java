package com.translator.service.codactor.inquiry.functions;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.inquiry.function.ChatGptFunctionCall;
import com.translator.model.codactor.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.modification.data.FileModificationDataReferenceHolder;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.editor.diff.GitDiffStingGeneratorService;
import com.translator.service.codactor.json.JsonExtractorService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.transformer.QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InquiryFunctionCallProcessorServiceImpl implements InquiryFunctionCallProcessorService {
    private final Gson gson;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final GitDiffStingGeneratorService gitDiffStingGeneratorService;
    private final QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService queuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService;

    @Inject
    public InquiryFunctionCallProcessorServiceImpl(Gson gson,
                                                   CodeSnippetExtractorService codeSnippetExtractorService,
                                                   FileModificationTrackerService fileModificationTrackerService,
                                                   GitDiffStingGeneratorService gitDiffStingGeneratorService,
                                                   QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService queuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService) {
        this.gson = gson;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.gitDiffStingGeneratorService = gitDiffStingGeneratorService;
        this.queuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService = queuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService;
    }

    @Override
    public String processFunctionCall(ChatGptFunctionCall chatGptFunctionCall) {
        if (chatGptFunctionCall.getName().equals("read_file_contents")) {
            String filePath = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "path");
            String packageName = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "package");
            String content = null;
            Map<String, Object> contentMap = new HashMap<>();
            if (filePath != null) {
                contentMap.put("filePath", filePath);
                content = codeSnippetExtractorService.getAllText(filePath);
            } else if (packageName != null) {
                contentMap.put("filePackage", packageName);
                VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                if (virtualFile != null) {
                    Path path = Paths.get(virtualFile.getPath());
                    try {
                        content = Files.readString(path, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    contentMap.put("filePath", virtualFile.getPath());
                }
            }
            contentMap.put("content", content);
            return gson.toJson(contentMap);
        } else if (chatGptFunctionCall.getName().equals("get_queued_modification_ids")) {
            List<FileModificationDataHolder> fileModificationDataHolderList = fileModificationTrackerService.getQueuedFileModificationObjectHolders();
            List<FileModificationDataReferenceHolder> fileModificationDataReferenceHolderList = queuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService.convert(fileModificationDataHolderList);
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
        } else if (chatGptFunctionCall.getName().equals("read_modification")) {
            String id = JsonExtractorService.extractField(chatGptFunctionCall.getArguments(), "id");
        } else if (chatGptFunctionCall.getName().equals("retry_modification_in_queue")) {
        } else if (chatGptFunctionCall.getName().equals("remove_modification_in_queue")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_modification")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_modification_and_wait_for_response")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_creation")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_creation_and_wait_for_response")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_deletion")) {
        } else if (chatGptFunctionCall.getName().equals("request_file_modification_and_wait_for_response_at_position")) {
        } else if (chatGptFunctionCall.getName().equals("run_program")) {
        }
        return null;
    }
}
