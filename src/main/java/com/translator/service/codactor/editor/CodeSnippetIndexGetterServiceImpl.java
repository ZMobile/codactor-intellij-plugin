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
