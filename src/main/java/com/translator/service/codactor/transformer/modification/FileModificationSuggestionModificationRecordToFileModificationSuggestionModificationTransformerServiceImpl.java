package com.translator.service.codactor.transformer.modification;

import com.translator.model.codactor.ai.modification.FileModificationSuggestionModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModificationRecord;

public class FileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerServiceImpl implements FileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService {
    @Override
    public FileModificationSuggestionModification convert(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord) {
        FileModificationSuggestionModification fileModificationSuggestionModification = new FileModificationSuggestionModification(null, fileModificationSuggestionModificationRecord.getFilePath(), fileModificationSuggestionModificationRecord.getModificationId(), fileModificationSuggestionModificationRecord.getSuggestionId(), null, fileModificationSuggestionModificationRecord.getBeforeText(), fileModificationSuggestionModificationRecord.getModificationType());
        fileModificationSuggestionModification.setModificationSuggestionModificationRecordId(fileModificationSuggestionModificationRecord.getId());
        fileModificationSuggestionModification.setError(fileModificationSuggestionModification.isError());
        fileModificationSuggestionModification.setDone(fileModificationSuggestionModification.isDone());
        fileModificationSuggestionModification.setSubjectLine(fileModificationSuggestionModificationRecord.getSubjectLine());
        return fileModificationSuggestionModification;
    }
}
