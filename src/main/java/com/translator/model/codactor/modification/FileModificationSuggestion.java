package com.translator.model.codactor.modification;

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
import com.translator.service.codactor.editor.GptToLanguageTransformerService;
import com.translator.service.codactor.editor.GptToLanguageTransformerServiceImpl;
import com.translator.service.codactor.editor.diff.DiffEditorGeneratorService;

import java.util.Objects;

public class FileModificationSuggestion implements Disposable {
    private final String filePath;
    private final String modificationId;
    private final String myId;
    private String beforeCode;
    private String suggestedCode;
    private Editor diffEditor;
    private Editor suggestedCodeEditor;

    public FileModificationSuggestion(DiffEditorGeneratorService diffEditorGeneratorService, Project project, String id, String filePath, String modificationId, String beforeCode, String suggestedCode) {
        this.myId = id;
        this.filePath = filePath;
        this.modificationId = modificationId;
        this.beforeCode = beforeCode;
        this.suggestedCode = suggestedCode;
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                GptToLanguageTransformerService gptToLanguageTransformerService = new GptToLanguageTransformerServiceImpl();
                String language = gptToLanguageTransformerService.getFromFilePath(filePath);
                String extension = gptToLanguageTransformerService.getExtensionFromLanguage(language);
                if (extension == null) {
                    extension = "txt";
                }
                EditorFactory editorFactory = EditorFactory.getInstance();
                FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension(extension);
                Document document = editorFactory.createDocument(suggestedCode);
                this.suggestedCodeEditor = editorFactory.createEditor(document, project, fileType, true);
                this.diffEditor = diffEditorGeneratorService.createDiffEditor(beforeCode, suggestedCode);
                EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
                ((EditorEx) this.diffEditor).setHighlighter(editorHighlighter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public FileModificationSuggestion(DiffEditorGeneratorService diffEditorGeneratorService, Project project, String id, String filePath, String modificationId, String beforeCode, String suggestedCode, String extension) {
        this.myId = id;
        this.filePath = filePath;
        this.modificationId = modificationId;
        this.beforeCode = beforeCode;
        this.suggestedCode = suggestedCode;
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                String newExtension = Objects.requireNonNullElse(extension, "txt");
                if (newExtension.startsWith(".")) {
                    newExtension = newExtension.substring(1);
                }
                EditorFactory editorFactory = EditorFactory.getInstance();
                FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension(newExtension);
                Document document = editorFactory.createDocument(suggestedCode);
                this.suggestedCodeEditor = editorFactory.createEditor(document, project, fileType, true);
                this.diffEditor = diffEditorGeneratorService.createDiffEditor(beforeCode, suggestedCode);
                EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
                ((EditorEx) this.diffEditor).setHighlighter(editorHighlighter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public FileModificationSuggestion(String id, String filePath, String modificationId, String suggestedCode) {
        this.myId = id;
        this.filePath = filePath;
        this.modificationId = modificationId;
        this.suggestedCode = suggestedCode;
    }

    public FileModificationSuggestion(FileModificationSuggestion fileModificationSuggestion) {
        this.myId = fileModificationSuggestion.getId();
        this.filePath = fileModificationSuggestion.getFilePath();
        this.modificationId = fileModificationSuggestion.getModificationId();
        this.beforeCode = fileModificationSuggestion.getBeforeCode();
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

    public String getSuggestedCode() {
        return suggestedCode;
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
