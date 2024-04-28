package com.translator.model.codactor.ai.modification;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;

import java.util.UUID;

public class FileModificationSuggestionModification {
    private String myId;
    private String modificationRecordId;
    private Editor editor;
    private final String filePath;
    private RangeMarker rangeMarker;
    private final String modificationId;
    private final String suggestionId;
    private final String beforeText;
    private final ModificationType modificationType;
    private String subjectLine;
    private String editedCode;
    private boolean error;
    private boolean done;

    public FileModificationSuggestionModification(Editor editor,
                                                  String filePath,
                                                  String modificationId,
                                                  String suggestionId,
                                                  RangeMarker rangeMarker,
                                                  String beforeText,
                                                  ModificationType modificationType) {
        this.editor = editor;
        this.myId = UUID.randomUUID().toString();
        this.filePath = filePath;
        this.modificationId = modificationId;
        this.suggestionId = suggestionId;
        this.rangeMarker = rangeMarker;
        this.beforeText = beforeText;
        this.modificationType = modificationType;
        this.error = false;
        this.done = false;
    }

    public FileModificationSuggestionModification(FileModificationSuggestionModification fileModificationSuggestionModification) {
        this.editor = fileModificationSuggestionModification.getEditor();
        this.myId = fileModificationSuggestionModification.getId();
        this.modificationRecordId = fileModificationSuggestionModification.getModificationRecordId();
        this.filePath = fileModificationSuggestionModification.getFilePath();
        this.rangeMarker = fileModificationSuggestionModification.getRangeMarker();
        this.modificationId = fileModificationSuggestionModification.getModificationId();
        this.suggestionId = fileModificationSuggestionModification.getSuggestionId();
        this.beforeText = fileModificationSuggestionModification.getBeforeText();
        this.modificationType = fileModificationSuggestionModification.getModificationType();
        this.subjectLine = fileModificationSuggestionModification.getSubjectLine();
        this.editedCode = fileModificationSuggestionModification.getEditedCode();
        this.error = fileModificationSuggestionModification.isError();
        this.done = fileModificationSuggestionModification.isDone();
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

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
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

    public String getSubjectLine() {
        return subjectLine;
    }

    public void setSubjectLine(String subjectLine) {
        this.subjectLine = subjectLine;
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

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
