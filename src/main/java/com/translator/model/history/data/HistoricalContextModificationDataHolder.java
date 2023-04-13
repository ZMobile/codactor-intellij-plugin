package com.translator.model.history.data;

import com.translator.model.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.modification.FileModificationSuggestionRecord;
import com.translator.model.modification.RecordType;

public class HistoricalContextModificationDataHolder {
    private final FileModificationSuggestionRecord fileModificationSuggestionRecord;
    private final FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord;
    private final RecordType recordType;
    private boolean includePreviousContext;

    public HistoricalContextModificationDataHolder(FileModificationSuggestionRecord fileModificationSuggestionRecord) {
        this.fileModificationSuggestionRecord = fileModificationSuggestionRecord;
        this.fileModificationSuggestionModificationRecord = null;
        this.recordType = RecordType.FILE_MODIFICATION_SUGGESTION;
    }

    public HistoricalContextModificationDataHolder(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord) {
        this.fileModificationSuggestionModificationRecord = fileModificationSuggestionModificationRecord;
        this.fileModificationSuggestionRecord = null;
        this.recordType = RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION;
    }

    public HistoricalContextModificationDataHolder(FileModificationSuggestionRecord fileModificationSuggestionRecord, boolean includePreviousContext) {
        this.fileModificationSuggestionRecord = fileModificationSuggestionRecord;
        this.fileModificationSuggestionModificationRecord = null;
        this.recordType = RecordType.FILE_MODIFICATION_SUGGESTION;
        this.includePreviousContext = includePreviousContext;
    }

    public HistoricalContextModificationDataHolder(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord, boolean includePreviousContext) {
        this.fileModificationSuggestionModificationRecord = fileModificationSuggestionModificationRecord;
        this.fileModificationSuggestionRecord = null;
        this.recordType = RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION;
        this.includePreviousContext = includePreviousContext;
    }

    public HistoricalContextModificationDataHolder() {
        this.fileModificationSuggestionModificationRecord = null;
        this.fileModificationSuggestionRecord = null;
        this.recordType = null;
    }

    public FileModificationSuggestionRecord getFileModificationSuggestionRecord() {
        return fileModificationSuggestionRecord;
    }

    public FileModificationSuggestionModificationRecord getFileModificationSuggestionModificationRecord() {
        return fileModificationSuggestionModificationRecord;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public boolean includesPreviousContext() {
        return includePreviousContext;
    }
}
