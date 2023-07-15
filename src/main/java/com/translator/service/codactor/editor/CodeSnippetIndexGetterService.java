package com.translator.service.codactor.editor;

public interface CodeSnippetIndexGetterService {
    int getStartIndex(String code, String snippet);

    int getEndIndex(String code, String snippet);

    int getLineAtIndex(String code, int index);

    int getLineAtIndexInFilePath(String filePath, int index);
}
