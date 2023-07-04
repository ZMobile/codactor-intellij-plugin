package com.translator.service.codactor.modification;

public interface FileModificationSuggestionDiffViewerService {
    void showDiffViewer(String filePath, String beforeCode, String afterCode);
}
