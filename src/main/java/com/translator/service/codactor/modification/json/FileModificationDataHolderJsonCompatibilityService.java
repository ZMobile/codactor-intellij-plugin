package com.translator.service.codactor.modification.json;

import com.translator.model.codactor.modification.data.FileModificationDataHolder;

import java.util.List;

public interface FileModificationDataHolderJsonCompatibilityService {
    FileModificationDataHolder makeFileModificationDataHolderCompatibleWithJson(FileModificationDataHolder fileModificationDataHolder);

    List<FileModificationDataHolder> makeFileModificationDataHoldersCompatibleWithJson(List<FileModificationDataHolder> fileModificationDataHolders);
}
