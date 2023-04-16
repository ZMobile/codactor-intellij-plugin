package com.translator.model.modification;

import com.intellij.openapi.editor.RangeMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileModification {
    private final String myId;
    private String modificationRecordId;
    private String filePath;
    private RangeMarker rangeMarker;
    private String beforeText;
    private ModificationType modificationType;
    private List<FileModificationSuggestion> modificationOptions;
    private boolean done;

    public FileModification(String filePath, RangeMarker rangeMarker, String beforeText, ModificationType modificationType) {
        this.filePath = filePath;
        this.myId = UUID.randomUUID().toString();
        this.rangeMarker = rangeMarker;
        this.beforeText = beforeText;
        this.modificationType = modificationType;
        this.modificationOptions = new ArrayList<>();
        this.done = false;
    }

    public String getId() {
        return myId;
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

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public List<FileModificationSuggestion> getModificationOptions() {
        return modificationOptions;
    }

    public FileModificationSuggestion getModificationSuggestion(String suggestionId) {
        for (FileModificationSuggestion suggestion : modificationOptions) {
            if (suggestion.getId().equals(suggestionId)) {
                return suggestion;
            }
        }
        return null;
    }

    public void setModificationOptions(List<FileModificationSuggestion> modificationOptions) {
        this.modificationOptions = modificationOptions;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
