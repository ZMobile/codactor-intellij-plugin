package com.translator.service.codactor.ai.modification.queued;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.MultiFileModification;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.multi.MultiFileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class QueuedFileModificationObjectHolderQueryServiceImpl implements QueuedFileModificationObjectHolderQueryService {
    private final FileModificationTrackerService fileModificationTrackerService;
    private final FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService;
    private final MultiFileModificationTrackerService multiFileModificationTrackerService;

    @Inject
    public QueuedFileModificationObjectHolderQueryServiceImpl(FileModificationTrackerService fileModificationTrackerService,
                                                              FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService,
                                                              MultiFileModificationTrackerService multiFileModificationTrackerService) {
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationSuggestionModificationTrackerService = fileModificationSuggestionModificationTrackerService;
        this.multiFileModificationTrackerService = multiFileModificationTrackerService;
    }

    @Override
    public List<FileModificationDataHolder> getQueuedFileModificationObjectHolders() {
        List<FileModificationDataHolder> fileModificationDataHolders = new ArrayList<>();
        List<FileModification> fileModifications = new ArrayList<>(fileModificationTrackerService.getAllFileModifications());
        for (FileModification fileModification : fileModifications) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(fileModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        List<FileModificationSuggestionModification> fileModificationSuggestionModifications = new ArrayList<>(fileModificationSuggestionModificationTrackerService.getAllFileModificationSuggestionModifications());
        for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModifications) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(fileModificationSuggestionModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        for (MultiFileModification multiFileModification : multiFileModificationTrackerService.getActiveMultiFileModifications()) {
            FileModificationDataHolder fileModificationDataHolder = new FileModificationDataHolder(multiFileModification);
            fileModificationDataHolders.add(fileModificationDataHolder);
        }
        return fileModificationDataHolders;
    }
}
