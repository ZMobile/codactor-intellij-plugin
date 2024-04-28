package com.translator.service.codactor.transformer.modification;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;

public interface FileModificationSuggestionRecordToFileModificationTransformerService {
    FileModification convert(FileModificationSuggestionRecord fileModificationSuggestionRecord);
}
