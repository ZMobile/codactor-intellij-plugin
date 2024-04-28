package com.translator.service.codactor.transformer;

import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;
import com.translator.model.codactor.ai.modification.data.FileModificationDataReferenceHolder;

import java.util.List;

public interface FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService {
    FileModificationDataReferenceHolder convert(FileModificationDataHolder fileModificationDataHolder);

    List<FileModificationDataReferenceHolder> convert(List<FileModificationDataHolder> fileModificationDataHolders);
}
