package com.translator.service.codactor.transformer;

import com.translator.model.codactor.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.modification.data.FileModificationDataReferenceHolder;
import com.translator.model.codactor.modification.data.ModificationObjectType;

import java.util.ArrayList;
import java.util.List;

public class FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerServiceImpl implements FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService {
    @Override
    public FileModificationDataReferenceHolder convert(FileModificationDataHolder fileModificationDataHolder) {
        if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION) {
            return new FileModificationDataReferenceHolder(fileModificationDataHolder.getFileModification());
        } else if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
            return new FileModificationDataReferenceHolder(fileModificationDataHolder.getFileModificationSuggestionModification());
        } else if (fileModificationDataHolder.getQueuedModificationObjectType() == ModificationObjectType.MULTI_FILE_MODIFICATION) {
            return new FileModificationDataReferenceHolder(fileModificationDataHolder.getMultiFileModification());
        } else {
            throw new RuntimeException("Unknown queued modification object type: " + fileModificationDataHolder.getQueuedModificationObjectType());
        }
    }

    @Override
    public List<FileModificationDataReferenceHolder> convert(List<FileModificationDataHolder> fileModificationDataHolders) {
        List<FileModificationDataReferenceHolder> fileModificationDataReferenceHolders = new ArrayList<>();
        for (FileModificationDataHolder fileModificationDataHolder : fileModificationDataHolders) {
            fileModificationDataReferenceHolders.add(convert(fileModificationDataHolder));
        }
        return fileModificationDataReferenceHolders;
    }
}
