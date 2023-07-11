package com.translator.service.codactor.transformer;

import com.translator.model.codactor.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.modification.data.FileModificationDataReferenceHolder;

import java.util.List;

public interface QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService {
    FileModificationDataReferenceHolder convert(FileModificationDataHolder fileModificationDataHolder);

    List<FileModificationDataReferenceHolder> convert(List<FileModificationDataHolder> fileModificationDataHolders);
}
