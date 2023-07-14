package com.translator.service.codactor.editor;

public class CodeSnippetIndexGetterServiceImpl implements CodeSnippetIndexGetterService {
    public int getStartIndex(String code, String snippet) {
        return code.indexOf(snippet);
    }

    public int getEndIndex(String code, String snippet) {
        int start = getStartIndex(code, snippet);
        return start == -1 ? -1 : start + snippet.length();
    }
}
