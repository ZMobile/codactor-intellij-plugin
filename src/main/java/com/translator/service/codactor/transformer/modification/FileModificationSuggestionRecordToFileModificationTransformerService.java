package com.translator.service.codactor.transformer.modification;

import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationSuggestionRecord;

public interface FileModificationSuggestionRecordToFileModificationTransformerService {
    FileModification convert(FileModificationSuggestionRecord fileModificationSuggestionRecord);
}
