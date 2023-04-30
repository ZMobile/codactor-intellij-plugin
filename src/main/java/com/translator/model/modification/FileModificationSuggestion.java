package com.translator.model.modification;

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
import com.intellij.openapi.util.Disposer;
import com.translator.service.code.GptToLanguageTransformerService;
import com.translator.service.code.GptToLanguageTransformerServiceImpl;

import java.util.Objects;

public class FileModificationSuggestion implements Disposable {
    private final Project project;
    private final String filePath;
    private final String modificationId;
    private final String myId;
    private Editor suggestedCode;

    public FileModificationSuggestion(Project project, String id, String filePath, String modificationId, String suggestedCode) {
        this.project = project;
        this.myId = id;
        this.filePath = filePath;
        this.modificationId = modificationId;
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
                this.suggestedCode = editorFactory.createEditor(document, null);
                EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
                ((EditorEx) this.suggestedCode).setHighlighter(editorHighlighter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public FileModificationSuggestion(Project project, String id, String filePath, String modificationId, String suggestedCode, String extension) {
        this.project = project;
        this.myId = id;
        this.filePath = filePath;
        this.modificationId = modificationId;
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                String newExtension = Objects.requireNonNullElse(extension, "txt");
                if (newExtension.startsWith(".")) {
                    newExtension = newExtension.substring(1);
                }
                EditorFactory editorFactory = EditorFactory.getInstance();
                FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension(newExtension);
                Document document = editorFactory.createDocument(suggestedCode);
                this.suggestedCode = editorFactory.createEditor(document, null);
                EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
                ((EditorEx) this.suggestedCode).setHighlighter(editorHighlighter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    public Editor getSuggestedCode() {
        return suggestedCode;
    }

    @Override
    public void dispose() {
        EditorFactory.getInstance().releaseEditor(suggestedCode);
    }
}
