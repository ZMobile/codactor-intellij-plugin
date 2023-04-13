package com.translator.model.modification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileModification {
    private final String myId;
    private String modificationRecordId;
    private String filePath;
    private int startIndex;
    private int endIndex;
    private String beforeText;
    private ModificationType modificationType;
    private List<FileModificationSuggestion> modificationOptions;
    private boolean done;

    public FileModification(String filePath, int startIndex, int endIndex, String beforeText, ModificationType modificationType) {
        this.filePath = filePath;
        this.myId = UUID.randomUUID().toString();
        this.startIndex = startIndex;
        this.endIndex = endIndex;
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

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
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

    public void shiftUp(int shift) {
        startIndex -= shift;
        endIndex -= shift;
    }

    public void shiftDown(int shift) {
        startIndex += shift;
        endIndex += shift;
    }
}
