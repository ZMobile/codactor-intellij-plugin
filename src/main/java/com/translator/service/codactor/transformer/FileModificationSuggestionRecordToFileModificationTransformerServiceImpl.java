package com.translator.service.codactor.transformer;

import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestion;
import com.translator.model.codactor.modification.FileModificationSuggestionRecord;

public class FileModificationSuggestionRecordToFileModificationTransformerServiceImpl implements FileModificationSuggestionRecordToFileModificationTransformerService {
    @Override
    public FileModification convert(FileModificationSuggestionRecord fileModificationSuggestionRecord) {
        FileModification fileModification = new FileModification(fileModificationSuggestionRecord.getFilePath(), fileModificationSuggestionRecord.getModification(), null, fileModificationSuggestionRecord.getBeforeCode(), fileModificationSuggestionRecord.getModificationType(), null);
        fileModification.setSubjectLine(fileModificationSuggestionRecord.getSubjectLine());
        fileModification.setDone(true);
        fileModification.setError(false);
        FileModificationSuggestion fileModificationSuggestion = new FileModificationSuggestion(fileModificationSuggestionRecord.getId(), fileModificationSuggestionRecord.getFilePath(), fileModificationSuggestionRecord.getModificationId(), fileModificationSuggestionRecord.getSuggestedCode());
        fileModification.getModificationOptions().add(fileModificationSuggestion);
        return fileModification;
    }
}
