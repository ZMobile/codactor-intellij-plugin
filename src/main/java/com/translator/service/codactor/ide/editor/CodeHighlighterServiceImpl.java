package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.ai.modification.*;

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

    /*@Override
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

        highlightTextArea(fileModificationTracker, editor);
    }*/

    @Override
    public void highlightTextArea(FileModification fileModification) {
        if (fileModification == null) {
            return;
        }
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileModification.getFilePath());
        if (virtualFile == null) {
            return;
        }

        Editor editor = editorExtractorService.getEditorForVirtualFile(project, virtualFile);
        if (editor == null) {
            return;
        }

        highlightTextArea(fileModification, editor);
    }

    @Override
    public void highlightTextArea(FileModification fileModification, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> {
            removeAllHighlights(editor);
            int startIndex = fileModification.getRangeMarker().getStartOffset();
            int endIndex = fileModification.getRangeMarker().getEndOffset();

            try {
                Color highlightColor;
                if (fileModification.isError()) {
                    highlightColor = Color.decode("#FF0000");
                } else if (fileModification.isDone()) {
                    highlightColor = Color.decode("#228B22");
                } else {
                    highlightColor = Color.decode("#009688");
                }
                addHighlight(editor, startIndex, endIndex, highlightColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void highlightTextArea(FileModificationTracker fileModificationTracker, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> {
            removeAllHighlights(editor);

            for (FileModification modification : fileModificationTracker.getModifications()) {
                if (modification.getRangeMarker() != null) {
                    int startIndex = modification.getRangeMarker().getStartOffset();
                    int endIndex = modification.getRangeMarker().getEndOffset();

                    try {
                        Color highlightColor;
                        if (modification.isError()) {
                            highlightColor = Color.decode("#FF0000");
                        } else if (modification.isDone()) {
                            highlightColor = Color.decode("#228B22");
                        } else {
                            highlightColor = Color.decode("#009688");
                        }
                        addHighlight(editor, startIndex, endIndex, highlightColor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void highlightTextArea(FileModificationSuggestionModification fileModificationSuggestionModification) {
        Editor editor = fileModificationSuggestionModification.getEditor();

        ApplicationManager.getApplication().invokeLater(() -> {
            removeAllHighlights(editor);
            int startIndex = fileModificationSuggestionModification.getRangeMarker().getStartOffset();
            int endIndex = fileModificationSuggestionModification.getRangeMarker().getEndOffset();

            try {
                Color highlightColor;
                if (fileModificationSuggestionModification.isError()) {
                    highlightColor = Color.decode("#FF0000");
                } else {
                    highlightColor = Color.decode("#009688");
                }
                addHighlight(editor, startIndex, endIndex, highlightColor);
            } catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public void removeAllHighlights(Editor editor) {
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

    @Override
    public void addHighlight(String filePath, int startIndex, int endIndex, Color highlightColor) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (virtualFile == null) {
            return;
        }

        Editor editor = editorExtractorService.getEditorForVirtualFile(project, virtualFile);
        if (editor == null) {
            return;
        }

        addHighlight(editor, startIndex, endIndex, highlightColor);
    }

    @Override
    public void removeAllHighlights(String filePath) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (virtualFile == null) {
            return;
        }

        Editor editor = editorExtractorService.getEditorForVirtualFile(project, virtualFile);
        if (editor == null) {
            return;
        }

        removeAllHighlights(editor);
    }
}