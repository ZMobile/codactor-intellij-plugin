package com.translator.service.codactor.ai.modification.queued;

import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;

import java.util.List;

public interface QueuedFileModificationObjectHolderQueryService {
    List<FileModificationDataHolder> getQueuedFileModificationObjectHolders();
}
