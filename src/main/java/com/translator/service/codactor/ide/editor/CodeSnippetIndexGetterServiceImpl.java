package com.translator.service.codactor.ide.editor;

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
        System.out.println("Start snippet index testo: " + startSnippetIndex);
        if (startSnippetIndex == -1) {
            return -1;
        }

        String codeFromStartSnippet = code.substring(startSnippetIndex);
        System.out.println("Snippet: " + snippet);
        System.out.println("Code from start snippet: " + codeFromStartSnippet);
        int snippetIndex = getEndIndex(codeFromStartSnippet, snippet);
        System.out.println("Snippet index: " + snippetIndex);
        System.out.println("Start snippet index: " + startSnippetIndex);
        System.out.println("Snippet index + start snippet index: " + (startSnippetIndex + snippetIndex));
        return snippetIndex == -1 ? -1 : startSnippetIndex + snippetIndex;
    }

    public int getStartIndexBeforeEndIndex(String code, String startSnippet, int endIndex) {
        String codeBeforeEndSnippet = code.substring(0, endIndex);
        return getStartIndex(codeBeforeEndSnippet, startSnippet);
    }

    public int getEndIndexAfterStartIndex(String code, int startIndex, String snippet) {
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

    public int getFirstSnippetStartIndexBeforeOtherSnippet(String code, String firstSnippet, String otherSnippet) {
        int otherSnippetEndIndex = getEndIndex(code, otherSnippet);

        if (otherSnippetEndIndex == -1) {
            return -1;
        }

        String codeBeforeOtherSnippet = code.substring(0, otherSnippetEndIndex);
        return getStartIndexBeforeEndIndex(codeBeforeOtherSnippet, firstSnippet, otherSnippetEndIndex);
    }
}