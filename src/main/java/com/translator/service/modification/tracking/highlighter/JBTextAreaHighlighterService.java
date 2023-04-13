package com.translator.service.modification.tracking.highlighter;

import com.translator.model.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.modification.FileModificationTracker;

public interface JBTextAreaHighlighterService {
    void highlightTextArea(FileModificationTracker fileModificationTracker);

    void highlightTextArea(FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker);
}
