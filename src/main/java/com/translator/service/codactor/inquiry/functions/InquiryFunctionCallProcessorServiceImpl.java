package com.translator.service.codactor.inquiry.functions;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.codactor.ai.FileContentResponse;
import com.translator.model.codactor.api.translator.inquiry.function.ChatGptFunctionCall;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.json.JsonExtractorService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class InquiryFunctionCallProcessorServiceImpl implements InquiryFunctionCallProcessorService {
    private final Gson gson;
    private final CodeSnippetExtractorService codeSnippetExtractorService;

    @Inject
    public InquiryFunctionCallProcessorServiceImpl(Gson gson,
                                                   CodeSnippetExtractorService codeSnippetExtractorService) {
        this.gson = gson;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
    }

    @Override
    public String processFunctionCall(ChatGptFunctionCall chatGptFunctionCall) {
        switch (chatGptFunctionCall.getName()) {
            case "read_file_at_package":
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
        }
        return null;
    }

    @Override
    public void test() {
        System.out.println(codeSnippetExtractorService.getAllTextAtPackage("com.translator.service.codactor.inquiry.functions.InquiryFunctionCallProcessorService"));
    }
}
