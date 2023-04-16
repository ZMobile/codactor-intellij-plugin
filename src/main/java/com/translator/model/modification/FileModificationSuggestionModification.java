package com.translator.model.modification;

import com.intellij.openapi.editor.RangeMarker;
import com.translator.service.modification.tracking.CodeRangeTrackerService;

import java.util.List;
import java.util.UUID;

public class FileModificationSuggestionModification {
    private final String myId;
    private String modificationRecordId;
    private final String filePath;
    private RangeMarker rangeMarker;
    private final String modificationId;
    private final String suggestionId;
    private final String beforeText;
    private final ModificationType modificationType;
    private String editedCode;

    public FileModificationSuggestionModification(String filePath,
                                                  String modificationId,
                                                  String suggestionId,
                                                  RangeMarker rangeMarker,
                                                  String beforeText,
                                                  ModificationType modificationType) {
        this.myId = UUID.randomUUID().toString();
        this.filePath = filePath;
        this.modificationId = modificationId;
        this.suggestionId = suggestionId;
        this.rangeMarker = rangeMarker;
        this.beforeText = beforeText;
        this.modificationType = modificationType;
    }

    public String getId() {
        return myId;
    }

    public String getModificationId() {
        return modificationId;
    }

    public String getSuggestionId() {
        return suggestionId;
    }

    public String getModificationRecordId() {
        return modificationRecordId;
    }

    public void setModificationRecordId(String modificationRecordId) {
        this.modificationRecordId = modificationRecordId;
    }

    public String getFilePath() {
        return filePath;
    }

    public RangeMarker getRangeMarker() {
        return rangeMarker;
    }

    public void setRangeMarker(RangeMarker rangeMarker) {
        this.rangeMarker = rangeMarker;
    }

    public String getBeforeText() {
        return beforeText;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }
    

    public void setEditedCode(String editedCode) {
        this.editedCode = editedCode;
    }

    public String getEditedCode() {
        return editedCode;
    }

    public String getModificationSuggestionModificationRecordId() {
        return getModificationRecordId();
    }

    public void setModificationSuggestionModificationRecordId(String modificationSuggestionModificationRecordId) {
        setModificationRecordId(modificationSuggestionModificationRecordId);
    }
}
