package com.translator.service.codactor.editor.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiffEditorGeneratorServiceImpl implements DiffEditorGeneratorService {
    private final EditorFactory editorFactory;

    public DiffEditorGeneratorServiceImpl() {
        this.editorFactory = EditorFactory.getInstance();
    }

    public Editor createDiffEditor(String beforeCode, String afterCode) {
        List<String> beforeLines = Arrays.asList(beforeCode.split("\n"));
        List<String> afterLines = Arrays.asList(afterCode.split("\n"));

        Patch<String> patch = DiffUtils.diff(beforeLines, afterLines);

        List<AbstractDelta<String>> deltas = patch.getDeltas();

        StringBuilder merged = new StringBuilder();
        List<Pair<Integer, Color>> changes = new ArrayList<>();
        int beforePosition = 0;
        int afterPosition = 0;
        for (AbstractDelta<String> delta : deltas) {
            // Copy unchanged lines before this delta
            while (beforePosition < delta.getSource().getPosition()) {
                merged.append(beforeLines.get(beforePosition)).append("\n");
                beforePosition++;
                afterPosition++;
            }

            switch(delta.getType()) {
                case DELETE:
                    for (String line : delta.getSource().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.RED));
                        afterPosition++;
                    }
                    beforePosition += delta.getSource().getLines().size();
                    break;
                case INSERT:
                    for (String line : delta.getTarget().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.GREEN));
                        afterPosition++;
                    }
                    break;
                case CHANGE:
                    for (String line : delta.getSource().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.RED));
                        afterPosition++;
                    }
                    beforePosition += delta.getSource().getLines().size();
                    for (String line : delta.getTarget().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.GREEN));
                        afterPosition++;
                    }
                    break;
            }
        }

        // Copy remaining unchanged lines
        while (beforePosition < beforeLines.size()) {
            merged.append(beforeLines.get(beforePosition)).append("\n");
            beforePosition++;
            afterPosition++;
        }

        Document document = editorFactory.createDocument(merged.toString());
        Editor editor = editorFactory.createEditor(document);

        for (Pair<Integer, Color> change : changes) {
            editor.getMarkupModel().addLineHighlighter(change.getFirst(), HighlighterLayer.LAST, new TextAttributes(change.getSecond(), null, null, null, Font.PLAIN));
        }

        return editor;
    }

    public void updateDiffEditor(Editor editor, String beforeCode, String afterCode) {
        List<String> beforeLines = Arrays.asList(beforeCode.split("\n"));
        List<String> afterLines = Arrays.asList(afterCode.split("\n"));

        Patch<String> patch = DiffUtils.diff(beforeLines, afterLines);

        List<AbstractDelta<String>> deltas = patch.getDeltas();

        StringBuilder merged = new StringBuilder();
        List<Pair<Integer, Color>> changes = new ArrayList<>();
        int beforePosition = 0;
        int afterPosition = 0;
        for (AbstractDelta<String> delta : deltas) {
            // Copy unchanged lines before this delta
            while (beforePosition < delta.getSource().getPosition()) {
                merged.append(beforeLines.get(beforePosition)).append("\n");
                beforePosition++;
                afterPosition++;
            }

            switch(delta.getType()) {
                case DELETE:
                    for (String line : delta.getSource().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.RED));
                        afterPosition++;
                    }
                    beforePosition += delta.getSource().getLines().size();
                    break;
                case INSERT:
                    for (String line : delta.getTarget().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.GREEN));
                        afterPosition++;
                    }
                    break;
                case CHANGE:
                    for (String line : delta.getSource().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.RED));
                        afterPosition++;
                    }
                    beforePosition += delta.getSource().getLines().size();
                    for (String line : delta.getTarget().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.GREEN));
                        afterPosition++;
                    }
                    break;
            }
        }

        // Copy remaining unchanged lines
        while (beforePosition < beforeLines.size()) {
            merged.append(beforeLines.get(beforePosition)).append("\n");
            beforePosition++;
            afterPosition++;
        }

        // Set document text
        ApplicationManager.getApplication().runWriteAction(() -> {
            editor.getDocument().setText(merged.toString());
        });

        // Clear existing highlights
        for (RangeHighlighter highlighter : editor.getMarkupModel().getAllHighlighters()) {
            editor.getMarkupModel().removeHighlighter(highlighter);
        }

        // Apply new highlights
        for (Pair<Integer, Color> change : changes) {
            editor.getMarkupModel().addLineHighlighter(change.getFirst(), HighlighterLayer.LAST, new TextAttributes(change.getSecond(), null, null, null, Font.PLAIN));
        }
    }

    public Editor createDiffEditorWithMimickedIndentation(String beforeCode, String afterCode) {
        List<String> beforeLines = Arrays.asList(beforeCode.split("\n"));
        List<String> afterLines = Arrays.asList(afterCode.split("\n"));

        Patch<String> patch = DiffUtils.diff(beforeLines, afterLines);

        List<AbstractDelta<String>> deltas = patch.getDeltas();

        StringBuilder merged = new StringBuilder();
        List<Pair<Integer, Color>> changes = new ArrayList<>();
        int beforePosition = 0;
        int afterPosition = 0;
        for (AbstractDelta<String> delta : deltas) {
            // Copy unchanged lines before this delta
            while (beforePosition < delta.getSource().getPosition()) {
                merged.append(beforeLines.get(beforePosition)).append("\n");
                beforePosition++;
                afterPosition++;
            }

            switch(delta.getType()) {
                case DELETE:
                    for (String line : delta.getSource().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.RED));
                        afterPosition++;
                    }
                    beforePosition += delta.getSource().getLines().size();
                    break;
                case INSERT:
                    String leadingWhitespace = getLeadingWhitespace(beforeLines.get(beforePosition));
                    for (String line : delta.getTarget().getLines()) {
                        String adjustedLine = setLeadingWhitespace(line, leadingWhitespace);
                        merged.append(adjustedLine).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.GREEN));
                        afterPosition++;
                    }
                    break;
                case CHANGE:
                    for (String line : delta.getSource().getLines()) {
                        merged.append(line).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.RED));
                        afterPosition++;
                    }
                    beforePosition += delta.getSource().getLines().size();
                    leadingWhitespace = getLeadingWhitespace(beforeLines.get(beforePosition));
                    for (String line : delta.getTarget().getLines()) {
                        String adjustedLine = setLeadingWhitespace(line, leadingWhitespace);
                        merged.append(adjustedLine).append("\n");
                        changes.add(new Pair<>(afterPosition, Color.GREEN));
                        afterPosition++;
                    }
                    break;
            }
        }

        // Copy remaining unchanged lines
        while (beforePosition < beforeLines.size()) {
            merged.append(beforeLines.get(beforePosition)).append("\n");
            beforePosition++;
            afterPosition++;
        }

        Document document = editorFactory.createDocument(merged.toString());
        Editor editor = editorFactory.createEditor(document);

        for (Pair<Integer, Color> change : changes) {
            editor.getMarkupModel().addLineHighlighter(change.getFirst(), HighlighterLayer.LAST, new TextAttributes(change.getSecond(), null, null, null, Font.PLAIN));
        }

        return editor;
    }

    private String getLeadingWhitespace(String s) {
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return s.substring(0, i);
    }

    private String setLeadingWhitespace(String s, String leadingWhitespace) {
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return leadingWhitespace + s.substring(i);
    }
}