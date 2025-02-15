package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.codactor.ai.modification.FileModificationTracker;

import java.awt.*;

public interface CodeHighlighterService {
    void highlightTextArea(FileModification fileModification);

    void highlightTextArea(FileModification fileModification, Editor editor);

    void highlightTextArea(FileModificationSuggestionModification fileModificationSuggestionModification);

    void highlightTextArea(FileModificationTracker fileModificationTracker, Editor editor);

    void updateHighlights(FileModificationTracker fileModificationTracker);

    void addHighlight(String filePath, int startIndex, int endIndex, Color highlightColor);

    void removeHighlight(FileModification fileModification);

    void removeAllHighlights(String filePath);

    void removeAllHighlights(Editor editor);
}
