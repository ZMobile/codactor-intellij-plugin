package com.translator.service.modification.tracking.highlighter;

import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestionModification;
import com.translator.model.modification.FileModificationSuggestionModificationTracker;
import com.translator.model.modification.FileModificationTracker;
import com.intellij.ui.components.JBTextArea;

import javax.inject.Inject;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.Map;

public class JBTextAreaHighlighterServiceImpl implements JBTextAreaHighlighterService {
    private Map<String, JBTextArea> displayMap;

    @Inject
    public JBTextAreaHighlighterServiceImpl(Map<String, JBTextArea> displayMap) {
        this.displayMap = displayMap;
    }

    @Override
    public void highlightTextArea(FileModificationTracker fileModificationTracker) {
        if (fileModificationTracker == null) {
            return;
        }

        JBTextArea display = displayMap.get(fileModificationTracker.getFilePath());
        display.setEditable(true);

        display.getHighlighter().removeAllHighlights();
        // Add new non-editable filters and highlights for the modifications
        for (FileModification modification : fileModificationTracker.getModifications()) {
            int startIndex = modification.getStartIndex();
            int endIndex = modification.getEndIndex();

            try {
                Color highlightColor;
                if (modification.isDone()) {
                    highlightColor = Color.GREEN;
                } else {
                    highlightColor = Color.decode("#7FFFD4");
                }
                display.getHighlighter().addHighlight(startIndex, endIndex,
                        new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void highlightTextArea(FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker) {
        /*if (fileModificationSuggestionModificationTracker == null) {
            return;
        }

        JBTextArea display = fileModificationSuggestionModificationTracker.getDisplay();
        display.setEditable(true);

        display.getHighlighter().removeAllHighlights();
        // Add new non-editable filters and highlights for the modifications
        for (FileModificationSuggestionModification modification : fileModificationSuggestionModificationTracker.getModifications()) {
            int startIndex = modification.getStartIndex();
            int endIndex = modification.getEndIndex();

            try {
                Color highlightColor;
                highlightColor = Color.decode("#7FFFD4");
                display.getHighlighter().addHighlight(startIndex, endIndex,
                        new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
}
