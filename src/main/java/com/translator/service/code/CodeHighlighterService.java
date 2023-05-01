package com.translator.service.code;

import com.intellij.openapi.editor.Editor;
import com.translator.model.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.modification.FileModificationTracker;

public interface CodeHighlighterService {
    void highlightTextArea(FileModificationTracker fileModificationTracker);

    void highlightTextArea(FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker);

    void highlightTextArea(FileModificationTracker fileModificationTracker, Editor editor);
}
