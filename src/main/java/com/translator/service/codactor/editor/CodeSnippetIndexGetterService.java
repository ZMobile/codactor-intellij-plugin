package com.translator.service.codactor.editor;

public interface CodeSnippetIndexGetterService {
    int getStartIndex(String code, String snippet);

    int getStartIndexInFilePath(String filePath, String snippet);

    int getEndIndex(String code, String snippet);

    int getEndIndex(String code, String startSnippetString, String snippet);

    int getEndIndex(String code, int startIndex, String snippet);

    int getEndIndexInFilePath(String filePath, String snippet);

    int getLineAtIndex(String code, int index);

    int getLineAtIndexInFilePath(String filePath, int index);
}
