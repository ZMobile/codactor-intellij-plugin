package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.vfs.VirtualFile;

public interface CodeSnippetExtractorService {
    String getSnippet(String filePath, int startIndex, int endIndex);

    String getAllText(String filePath);

    String getAllTextAtPackage(String filePackage);

    VirtualFile getVirtualFileFromPackage(String filePackage);

    Document getDocument(String filePath);

    SelectionModel getSelectedText(String filePath);

    String getSnippet(Editor editor, int startIndex, int endIndex);

    String getAllText(Editor editor);

    SelectionModel getSelectedText(Editor editor);

    String getCurrentAndNextLineCodeAfterIndex(String filePath, int startIndex);

    String getCurrentAndOneLinePreviousCodeBeforeIndex(String filePath, int endIndex);

    String getCurrentLineCodeAtIndex(String filePath, int index);
}
