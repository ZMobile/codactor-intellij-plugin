package com.translator.service.codactor.editor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.codactor.modification.FileModificationTracker;

import java.awt.*;

public interface CodeHighlighterService {
    void highlightTextArea(FileModificationTracker fileModificationTracker);

    void highlightTextArea(FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker);

    void highlightTextArea(FileModificationTracker fileModificationTracker, Editor editor);

    void addHighlight(String filePath, int startIndex, int endIndex, Color highlightColor);

    void removeAllHighlights(String filePath);
}
