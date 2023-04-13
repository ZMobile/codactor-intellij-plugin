package com.translator.service.search;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;


import javax.swing.*;

public interface SearchResultParserService {
    void findNext(String searchString);

    void setDisplay(JBTextArea display);

    void setCurrentSearchString(String searchString);

    void setScrollPane(JBScrollPane scrollPane);
}
