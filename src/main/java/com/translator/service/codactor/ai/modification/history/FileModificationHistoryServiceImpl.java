package com.translator.service.codactor.ai.modification.history;

import com.google.inject.Inject;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.model.codactor.api.translator.history.DesktopCodeModificationHistoryResponseResource;
import com.translator.model.codactor.ai.history.data.HistoricalFileModificationDataHolder;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.ai.modification.data.ModificationObjectType;
import com.translator.service.codactor.transformer.modification.HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileModificationHistoryServiceImpl implements FileModificationHistoryService {
    private final CodeModificationHistoryDao codeModificationHistoryDao;
    private final FileModificationManagementService fileModificationManagementService;
    private final HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerService historicalFileModificationDataHolderToFileModificationDataHolderTransformerService;

    @Inject
    public FileModificationHistoryServiceImpl(CodeModificationHistoryDao codeModificationHistoryDao, FileModificationManagementService fileModificationManagementService, HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerService historicalFileModificationDataHolderToFileModificationDataHolderTransformerService) {
        this.codeModificationHistoryDao = codeModificationHistoryDao;
        this.fileModificationManagementService = fileModificationManagementService;
        this.historicalFileModificationDataHolderToFileModificationDataHolderTransformerService = historicalFileModificationDataHolderToFileModificationDataHolderTransformerService;
    }

    @Override
    public List<FileModificationDataHolder> getRecentHistoricalFileModifications() {
        DesktopCodeModificationHistoryResponseResource desktopCompletedCodeModificationHistoryResponseResource = codeModificationHistoryDao.getRecentModifications();
        List<FileModificationDataHolder> completedFileModificationObjects = historicalFileModificationDataHolderToFileModificationDataHolderTransformerService.convert(desktopCompletedCodeModificationHistoryResponseResource.getModificationHistory());
        List<FileModificationDataHolder> queuedModificationObjects = fileModificationManagementService.getQueuedFileModificationObjectHolders();
        Map<String, FileModificationDataHolder> mergedModificationObjects = new HashMap<>();
        for (FileModificationDataHolder fileModificationDataHolder : queuedModificationObjects) {
            if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION) {
                FileModification fileModification = fileModificationDataHolder.getFileModification();
                if (!fileModification.getModificationOptions().isEmpty()) {
                    FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().get(0);
                    //Before code is already present in the file modification object, so it's removed for json object brevity.
                    fileModificationSuggestion.setBeforeCode(null);
                }
                mergedModificationObjects.put(fileModification.getId(), fileModificationDataHolder);
            } else if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationDataHolder.getFileModificationSuggestionModification();
                mergedModificationObjects.put(fileModificationSuggestionModification.getId(), fileModificationDataHolder);
            }
        }
        for (FileModificationDataHolder fileModificationDataHolder : completedFileModificationObjects) {
            if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION) {
                FileModification fileModification = fileModificationDataHolder.getFileModification();
                if (!mergedModificationObjects.containsKey(fileModification.getId())) {
                    mergedModificationObjects.put(fileModification.getId(), fileModificationDataHolder);
                }
            } else if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationDataHolder.getFileModificationSuggestionModification();
                if (!mergedModificationObjects.containsKey(fileModificationSuggestionModification.getId())) {
                    mergedModificationObjects.put(fileModificationSuggestionModification.getId(), fileModificationDataHolder);
                }
            }
        }
        return new ArrayList<>(mergedModificationObjects.values());
    }

    public FileModificationDataHolder getModification(String id) {
        List<FileModificationDataHolder> queuedModificationObjects = fileModificationManagementService.getQueuedFileModificationObjectHolders();
        return queuedModificationObjects.stream()
                .filter(modificationObject ->
                        (modificationObject.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION
                                && modificationObject.getFileModification().getId().equals(id))
                                || (modificationObject.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION
                                && modificationObject.getFileModificationSuggestionModification().getId().equals(id)))
                .findFirst()
                .orElseGet(() -> {
                    HistoricalFileModificationDataHolder historicalFileModificationDataHolder = codeModificationHistoryDao.getModification(id);
                    return historicalFileModificationDataHolderToFileModificationDataHolderTransformerService.convert(historicalFileModificationDataHolder);
                });
    }
}
