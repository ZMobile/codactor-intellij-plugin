package com.translator.model.codactor.history.data;

import com.translator.model.codactor.modification.FileModificationSuggestionModificationRecord;
import com.translator.model.codactor.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.modification.RecordType;

public class HistoricalFileModificationDataHolder {
    private final FileModificationSuggestionRecord fileModificationSuggestionRecord;
    private final FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord;
    private final RecordType recordType;
    private boolean includePreviousContext;

    public HistoricalFileModificationDataHolder(FileModificationSuggestionRecord fileModificationSuggestionRecord) {
        this.fileModificationSuggestionRecord = fileModificationSuggestionRecord;
        this.fileModificationSuggestionModificationRecord = null;
        this.recordType = RecordType.FILE_MODIFICATION_SUGGESTION;
    }

    public HistoricalFileModificationDataHolder(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord) {
        this.fileModificationSuggestionModificationRecord = fileModificationSuggestionModificationRecord;
        this.fileModificationSuggestionRecord = null;
        this.recordType = RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION;
    }

    public HistoricalFileModificationDataHolder(FileModificationSuggestionRecord fileModificationSuggestionRecord, boolean includePreviousContext) {
        this.fileModificationSuggestionRecord = fileModificationSuggestionRecord;
        this.fileModificationSuggestionModificationRecord = null;
        this.recordType = RecordType.FILE_MODIFICATION_SUGGESTION;
        this.includePreviousContext = includePreviousContext;
    }

    public HistoricalFileModificationDataHolder(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord, boolean includePreviousContext) {
        this.fileModificationSuggestionModificationRecord = fileModificationSuggestionModificationRecord;
        this.fileModificationSuggestionRecord = null;
        this.recordType = RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION;
        this.includePreviousContext = includePreviousContext;
    }

    public HistoricalFileModificationDataHolder() {
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
