package com.translator.service.codactor.ide.editor;

public interface CodeSnippetIndexGetterService {
    int getStartIndex(String code, String snippet);

    int getStartIndexInFilePath(String filePath, String snippet);

    int getEndIndex(String code, String snippet);

    int getEndIndex(String code, String startSnippetString, String snippet);

    int getStartIndexBeforeEndIndex(String code, String startSnippet, int endIndex);

    int getEndIndexAfterStartIndex(String code, int startIndex, String snippet);

    int getEndIndexInFilePath(String filePath, String snippet);

    int getLineAtIndex(String code, int index);

    int getLineAtIndexInFilePath(String filePath, int index);

    int getFirstSnippetStartIndexBeforeOtherSnippet(String code, String firstSnippet, String otherSnippet);
}
