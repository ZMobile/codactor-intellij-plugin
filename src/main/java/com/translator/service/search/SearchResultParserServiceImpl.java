package com.translator.service.search;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;


import javax.inject.Inject;
import javax.swing.text.BadLocationException;
import java.awt.*;

public class SearchResultParserServiceImpl implements SearchResultParserService {
    private JBTextArea textArea;
    private JBScrollPane scrollPane;
    private String currentSearchString;
    private int found = 0;
    private int parsed = 0;

    @Inject
    public SearchResultParserServiceImpl(JBScrollPane scrollPane) {
        this.scrollPane = scrollPane;
        this.textArea = null;
        this.currentSearchString = "";
    }

    @Override
    public void findNext(String searchString) {
        if (!currentSearchString.equals(searchString)) {
            // new search string
            currentSearchString = searchString;
            found = 0;
            parsed = 0;
        }
        int currentCaretPosition = textArea.getCaretPosition();
        int nextMatch = textArea.getText().indexOf(currentSearchString, currentCaretPosition);
        if (nextMatch == -1) {
            // search string not found in the remaining text, start from the beginning
            parsed = 0;
            currentCaretPosition = 0;
            nextMatch = textArea.getText().indexOf(currentSearchString, currentCaretPosition);
        }
        textArea.setCaretPosition(nextMatch);
        textArea.moveCaretPosition(nextMatch + currentSearchString.length());
        textArea.select(nextMatch, nextMatch + currentSearchString.length());
        parsed = nextMatch + currentSearchString.length();
        found++;

        // Get the scroll pane that contains the JBTextArea

        // Calculate the position of the selected instance in the text area
        Rectangle rect = null;
        try {
            rect = textArea.modelToView(nextMatch);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        // Scroll to the selected instance
        scrollPane.getVerticalScrollBar().setValue((int)rect.getY());
    }

    public void setCurrentSearchString(String searchString) {
        currentSearchString = searchString;
    }

    public int getFound() {
        return found;
    }

    public int getParsed() {
        return parsed;
    }

    public void setDisplay(JBTextArea jBTextArea) {
        textArea = jBTextArea;
    }

    public void setScrollPane(JBScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }
}
