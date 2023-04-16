package com.translator.service.code;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.translator.model.modification.*;
import com.translator.service.modification.tracking.FileModificationTrackerService;

import javax.inject.Inject;
import java.awt.*;

public class CodeHighlighterServiceImpl implements CodeHighlighterService {
    private Project project;
    private EditorExtractorService editorExtractorService;
    @Inject
    public CodeHighlighterServiceImpl(Project project,
                                      EditorExtractorService editorExtractorService) {
        this.project = project;
        this.editorExtractorService = editorExtractorService;
    }

    @Override
    public void highlightTextArea(FileModificationTracker fileModificationTracker) {
        if (fileModificationTracker == null) {
            return;
        }
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileModificationTracker.getFilePath());
        if (virtualFile == null) {
            return;
        }

        Editor editor = editorExtractorService.getEditorForVirtualFile(project, virtualFile);
        if (editor == null) {
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            removeAllHighlights(editor);

            for (FileModification modification : fileModificationTracker.getModifications()) {
                int startIndex = modification.getRangeMarker().getStartOffset();
                int endIndex = modification.getRangeMarker().getEndOffset();

                try {
                    Color highlightColor;
                    if (modification.isDone()) {
                        highlightColor = JBColor.GREEN;
                    } else {
                        highlightColor = Color.decode("#009688");
                    }
                    addHighlight(editor, startIndex, endIndex, highlightColor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void highlightTextArea(FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker) {
        if (fileModificationSuggestionModificationTracker == null) {
            return;
        }
        FileModificationSuggestion fileModificationSuggestion = fileModificationSuggestionModificationTracker.getFileModificationSuggestion();
        Editor editor = fileModificationSuggestion.getSuggestedCode();

        removeAllHighlights(editor);

        for (FileModificationSuggestionModification modification : fileModificationSuggestionModificationTracker.getModifications()) {
            int startIndex = modification.getRangeMarker().getStartOffset();
            int endIndex = modification.getRangeMarker().getEndOffset();

            try {
                Color highlightColor = Color.decode("#009688");
                addHighlight(editor, startIndex, endIndex, highlightColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void removeAllHighlights(Editor editor) {
        for (RangeHighlighter highlighter : editor.getMarkupModel().getAllHighlighters()) {
            editor.getMarkupModel().removeHighlighter(highlighter);
        }
    }

    private void addHighlight(Editor editor, int startIndex, int endIndex, Color highlightColor) {
        TextAttributes textAttributes = new TextAttributes();
        textAttributes.setBackgroundColor(highlightColor);
        editor.getMarkupModel().addRangeHighlighter(
                startIndex,
                endIndex,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE);
    }
}