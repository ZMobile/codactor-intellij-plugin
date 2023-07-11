package com.translator.service.codactor.transformer.modification;

import com.translator.model.codactor.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.modification.FileModificationSuggestionModificationRecord;

public class FileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerServiceImpl implements FileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService {
    @Override
    public FileModificationSuggestionModification convert(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord) {
        FileModificationSuggestionModification fileModificationSuggestionModification = new FileModificationSuggestionModification(fileModificationSuggestionModificationRecord.getFilePath(), fileModificationSuggestionModificationRecord.getModificationId(), fileModificationSuggestionModificationRecord.getSuggestionId(), null, fileModificationSuggestionModificationRecord.getBeforeText(), fileModificationSuggestionModificationRecord.getModificationType());
        fileModificationSuggestionModification.setError(fileModificationSuggestionModification.isError());
        fileModificationSuggestionModification.setDone(fileModificationSuggestionModification.isDone());
        fileModificationSuggestionModification.setSubjectLine(fileModificationSuggestionModificationRecord.getSubjectLine());
        return fileModificationSuggestionModification;
    }
}
