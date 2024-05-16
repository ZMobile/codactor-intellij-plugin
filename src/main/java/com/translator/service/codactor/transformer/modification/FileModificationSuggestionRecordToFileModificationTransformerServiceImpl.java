package com.translator.service.codactor.transformer.modification;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;

public class FileModificationSuggestionRecordToFileModificationTransformerServiceImpl implements FileModificationSuggestionRecordToFileModificationTransformerService {
    @Override
    public FileModification convert(FileModificationSuggestionRecord fileModificationSuggestionRecord) {
        FileModification fileModification = new FileModification(fileModificationSuggestionRecord.getFilePath(), fileModificationSuggestionRecord.getModification(), null, fileModificationSuggestionRecord.getBeforeCode(), fileModificationSuggestionRecord.getModificationType(), null);
        fileModification.setSubjectLine(fileModificationSuggestionRecord.getSubjectLine());
        fileModification.setModificationRecordId(fileModificationSuggestionRecord.getModificationId());
        fileModification.setDone(true);
        fileModification.setError(false);
        FileModificationSuggestion fileModificationSuggestion = new FileModificationSuggestion(fileModificationSuggestionRecord.getId(), fileModificationSuggestionRecord.getFilePath(), fileModificationSuggestionRecord.getModificationId(), fileModificationSuggestionRecord.getSuggestedCodeBeforeRestoration(), fileModificationSuggestionRecord.getSuggestedCode());
        fileModification.getModificationOptions().add(fileModificationSuggestion);
        return fileModification;
    }
}
