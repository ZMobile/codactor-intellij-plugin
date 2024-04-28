package com.translator.service.codactor.ai.modification.queued;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.MultiFileModification;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;

import java.util.ArrayList;
import java.util.List;

public class QueuedFileModificationObjectHolderQueryServiceImpl implements QueuedFileModificationObjectHolderQueryService {
    private final FileModificationTrackerService fileModificationTrackerService;
    private final FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService;

    public List<FileModificationDataHolder> getQueuedFileModificationObjectHolders() {
        List<FileModificationDataHolder> fileModificationDataHolders = new ArrayList<>();
        List<FileModification> fileModifications = new ArrayList<>(getAllFileModifications());
        for (FileModification fileModification : fileModifications) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(fileModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        List<FileModificationSuggestionModification> fileModificationSuggestionModifications = new ArrayList<>(getAllFileModificationSuggestionModifications());
        for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModifications) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(fileModificationSuggestionModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        for (MultiFileModification multiFileModification : activeMultiFileModifications) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(multiFileModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        return fileModificationDataHolders;
    }
}
