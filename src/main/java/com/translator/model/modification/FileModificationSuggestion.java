package com.translator.model.modification;

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
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTextArea;
import com.translator.service.code.CodeToFileTypeTransformerService;
import com.translator.service.code.CodeToFileTypeTransformerServiceImpl;

import java.util.Objects;

public class FileModificationSuggestion {
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
        ApplicationManager.getApplication().invokeAndWait(() -> {
            try {
                CodeToFileTypeTransformerService codeToFileTypeTransformerService = new CodeToFileTypeTransformerServiceImpl();
                EditorFactory editorFactory = EditorFactory.getInstance();
                FileType fileType = codeToFileTypeTransformerService.convert(suggestedCode);
                Document document = editorFactory.createDocument(suggestedCode);
                this.suggestedCode = editorFactory.createEditor(document, null);
                EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
                ((EditorEx) this.suggestedCode).setHighlighter(editorHighlighter);
                ((EditorEx) this.suggestedCode).setViewer(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assert this.suggestedCode != null;
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
}
