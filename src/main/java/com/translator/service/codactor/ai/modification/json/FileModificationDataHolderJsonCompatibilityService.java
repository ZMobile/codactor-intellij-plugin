package com.translator.service.codactor.ai.modification.json;

import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;

import java.util.List;

public interface FileModificationDataHolderJsonCompatibilityService {
    FileModificationDataHolder makeFileModificationDataHolderCompatibleWithJson(FileModificationDataHolder fileModificationDataHolder);

    List<FileModificationDataHolder> makeFileModificationDataHoldersCompatibleWithJson(List<FileModificationDataHolder> fileModificationDataHolders);
}
