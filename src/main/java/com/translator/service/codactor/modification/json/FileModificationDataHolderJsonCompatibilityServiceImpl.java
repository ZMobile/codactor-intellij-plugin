package com.translator.service.codactor.modification.json;

import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestion;
import com.translator.model.codactor.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.modification.data.FileModificationDataHolder;

import java.util.ArrayList;
import java.util.List;

public class FileModificationDataHolderJsonCompatibilityServiceImpl implements FileModificationDataHolderJsonCompatibilityService {
    @Override
    public FileModificationDataHolder makeFileModificationDataHolderCompatibleWithJson(FileModificationDataHolder fileModificationDataHolder) {
        FileModificationDataHolder newFileModificationDataHolder = null;
        if (fileModificationDataHolder.getFileModification() != null) {
            FileModification fileModification = new FileModification(fileModificationDataHolder.getFileModification());
            fileModification.setRangeMarker(null);
            fileModification.setPriorContext(null);
            if (!fileModification.getModificationOptions().isEmpty()) {
                for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
                    fileModificationSuggestion.setDiffEditor(null);
                    fileModificationSuggestion.setSuggestedCodeEditor(null);
                }
            }
            newFileModificationDataHolder = new FileModificationDataHolder(fileModification);
        }
        if (fileModificationDataHolder.getFileModificationSuggestionModification() != null) {
            FileModificationSuggestionModification fileModificationSuggestionModification = new FileModificationSuggestionModification(fileModificationDataHolder.getFileModificationSuggestionModification());
            fileModificationSuggestionModification.setRangeMarker(null);
            newFileModificationDataHolder = new FileModificationDataHolder(fileModificationSuggestionModification);
        }
        if (fileModificationDataHolder.getMultiFileModification() != null) {
            newFileModificationDataHolder = new FileModificationDataHolder(fileModificationDataHolder.getMultiFileModification());
        }
        return newFileModificationDataHolder;
    }

    @Override
    public List<FileModificationDataHolder> makeFileModificationDataHoldersCompatibleWithJson(List<FileModificationDataHolder> fileModificationDataHolders) {
        List<FileModificationDataHolder> newFileModificationDataHolders = new ArrayList<>();
        for (FileModificationDataHolder fileModificationDataHolder : fileModificationDataHolders) {
            FileModificationDataHolder newFileModificationDataHolder = makeFileModificationDataHolderCompatibleWithJson(fileModificationDataHolder);
            newFileModificationDataHolders.add(newFileModificationDataHolder);
        }
        return newFileModificationDataHolders;
    }
}
