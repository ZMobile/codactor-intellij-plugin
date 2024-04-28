package com.translator.service.codactor.ai.modification.tracking.suggestion;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;

import java.io.File;
import java.util.List;

public interface FileModificationSuggestionService {
    void createFileModificationSuggestions(FileModification fileModification, List<FileModificationSuggestionRecord> modificationOptions);

    FileModificationSuggestion getFileModificationSuggestion(FileModification fileModification, String suggestionId);
}
