package com.translator.service.codactor.ai.modification.diff;

public interface AiFileModificationSuggestionDiffViewerService {
    void showDiffViewer(String filePath, String beforeCode, String afterCode);
}
