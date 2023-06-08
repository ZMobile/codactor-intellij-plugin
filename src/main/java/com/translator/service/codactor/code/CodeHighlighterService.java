package com.translator.service.codactor.code;

import com.intellij.openapi.editor.Editor;
import com.translator.model.codactor.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.codactor.modification.FileModificationTracker;

public interface CodeHighlighterService {
    void highlightTextArea(FileModificationTracker fileModificationTracker);

    void highlightTextArea(FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker);

    void highlightTextArea(FileModificationTracker fileModificationTracker, Editor editor);
}
