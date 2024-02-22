package com.translator.service.codactor.editor;

import com.google.inject.Inject;

public class CodeSnippetIndexGetterServiceImpl implements CodeSnippetIndexGetterService {
    private CodeSnippetExtractorService codeSnippetExtractorService;

    @Inject
    public CodeSnippetIndexGetterServiceImpl(CodeSnippetExtractorService codeSnippetExtractorService) {
        this.codeSnippetExtractorService = codeSnippetExtractorService;
    }

    public int getStartIndex(String code, String snippet) {
        return code.indexOf(snippet);
    }

    @Override
    public int getStartIndexInFilePath(String filePath, String snippet) {
        String code = codeSnippetExtractorService.getAllText(filePath);
        return getStartIndex(code, snippet);
    }

    public int getEndIndex(String code, String snippet) {
        int start = getStartIndex(code, snippet);
        return start == -1 ? -1 : start + snippet.length();
    }

    @Override
    public int getEndIndexInFilePath(String filePath, String snippet) {
        String code = codeSnippetExtractorService.getAllText(filePath);
        return getEndIndex(code, snippet);
    }

    public int getEndIndex(String code, String startSnippetString, String snippet) {
        int startSnippetIndex = getStartIndex(code, startSnippetString);
        if (startSnippetIndex == -1) {
            return -1;
        }

        String codeFromStartSnippet = code.substring(startSnippetIndex);
        int snippetIndex = getStartIndex(codeFromStartSnippet, snippet);

        return snippetIndex == -1 ? -1 : startSnippetIndex + snippetIndex + snippet.length();
    }

    public int getEndIndex(String code, int startIndex, String snippet) {
        if (startIndex >= code.length() || startIndex < 0) {
            return -1;
        }

        String codeFromStartIndex = code.substring(startIndex);
        int snippetIndex = getStartIndex(codeFromStartIndex, snippet);

        return snippetIndex == -1 ? -1 : startIndex + snippetIndex + snippet.length();
    }

    @Override
    public int getLineAtIndex(String code, int index) {
        int count = 1;
        for (int i = 0; i < index; i++) {
            if (code.charAt(i) == '\n') {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getLineAtIndexInFilePath(String filePath, int index) {
        String code = codeSnippetExtractorService.getAllText(filePath);
        return getLineAtIndex(code, index);
    }
}