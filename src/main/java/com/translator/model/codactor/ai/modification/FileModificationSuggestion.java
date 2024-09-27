package com.translator.model.codactor.ai.modification;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerService;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerServiceImpl;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;

import java.util.Objects;

public class FileModificationSuggestion implements Disposable {
    private final String filePath;
    private final String modificationId;
    private final String myId;
    private String beforeCode;
    private String suggestedCodeBeforeRestoration;
    private String suggestedCode;
    private Editor diffEditor;
    private Editor suggestedCodeEditor;

    public FileModificationSuggestion(String filePath, String modificationId, String myId, String beforeCode, String suggestedCodeBeforeRestoration, String suggestedCode, Editor diffEditor, Editor suggestedCodeEditor) {
        this.filePath = filePath;
        this.modificationId = modificationId;
        this.myId = myId;
        this.beforeCode = beforeCode;
        this.suggestedCodeBeforeRestoration = suggestedCodeBeforeRestoration;
        this.suggestedCode = suggestedCode;
        this.diffEditor = diffEditor;
        this.suggestedCodeEditor = suggestedCodeEditor;
    }

    public FileModificationSuggestion(String id, String filePath, String modificationId, String suggestedCodeBeforeRestoration, String suggestedCode) {
        this.myId = id;
        this.filePath = filePath;
        this.modificationId = modificationId;
this.suggestedCodeBeforeRestoration = suggestedCodeBeforeRestoration;
        this.suggestedCode = suggestedCode;
    }

    public FileModificationSuggestion(FileModificationSuggestion fileModificationSuggestion) {
        this.myId = fileModificationSuggestion.getId();
        this.filePath = fileModificationSuggestion.getFilePath();
        this.modificationId = fileModificationSuggestion.getModificationId();
        this.beforeCode = fileModificationSuggestion.getBeforeCode();
        this.suggestedCodeBeforeRestoration = fileModificationSuggestion.suggestedCodeBeforeRestoration;
        this.suggestedCode = fileModificationSuggestion.getSuggestedCode();
        this.diffEditor = fileModificationSuggestion.getDiffEditor();
        this.suggestedCodeEditor = fileModificationSuggestion.getSuggestedCodeEditor();
    }

    public String getFilePath() {
        return filePath;
    }

    public String getId() {
        return myId;
    }

    public String getModificationId() {
        return modificationId;
    }

    public void setBeforeCode(String beforeCode) {
        this.beforeCode = beforeCode;
    }

    public String getBeforeCode() {
        return beforeCode;
    }

    public String getSuggestedCodeBeforeRestoration() {
        return suggestedCodeBeforeRestoration;
    }

    public String getSuggestedCode() {
        return suggestedCode;
    }

    public void setSuggestedCode(String suggestedCode) {
        this.suggestedCode = suggestedCode;
    }

    public Editor getDiffEditor() {
        return diffEditor;
    }

    public void setDiffEditor(Editor diffEditor) {
        this.diffEditor = diffEditor;
    }

    public Editor getSuggestedCodeEditor() {
        return suggestedCodeEditor;
    }

    public void setSuggestedCodeEditor(Editor suggestedCodeEditor) {
        this.suggestedCodeEditor = suggestedCodeEditor;
    }

    @Override
    public void dispose() {
        if (diffEditor != null) {
            EditorFactory.getInstance().releaseEditor(diffEditor);
        }
        if (suggestedCodeEditor != null) {
            EditorFactory.getInstance().releaseEditor(suggestedCodeEditor);
        }
    }
}
