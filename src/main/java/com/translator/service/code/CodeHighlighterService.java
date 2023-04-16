package com.translator.service.code;

import com.translator.model.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.modification.FileModificationTracker;

public interface CodeHighlighterService {
    void highlightTextArea(FileModificationTracker fileModificationTracker);

    void highlightTextArea(FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker);
}
