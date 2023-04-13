package com.translator.service.ui;

import com.google.inject.name.Named;
import com.intellij.ui.components.JBScrollPane;
import com.translator.service.search.SearchResultParserService;
import com.translator.service.search.SearchResultParserServiceImpl;
import com.intellij.ui.components.JBTextArea;


import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class DisplayProjectorServiceImpl implements DisplayProjectorService {
    public interface DisplayInitializer {
        JBTextArea initializeDisplay(String filePath, JBTextArea display);

        void projectDisplay(String filePath, JBTextArea display);
    }

    private final SearchResultParserService searchResultParserService;
    private JBScrollPane rTextScrollPane;
    private JBTextArea display;
    private Map<String, JBTextArea> displayMap = new HashMap<>();
    private DisplayInitializer displayInitializer;

    @Inject
    public DisplayProjectorServiceImpl(@Named("displayMap") Map<String, JBTextArea> displayMap,
                                       SearchResultParserService searchResultParserService) {
        this.displayMap = displayMap;
        this.searchResultParserService = searchResultParserService;
    }

    public void projectDisplay(String filePath, JBTextArea display) {
        JBTextArea newDisplay = this.displayInitializer.initializeDisplay(filePath, display);
        this.rTextScrollPane.setViewportView(newDisplay);
        this.display = newDisplay;
        this.searchResultParserService.setDisplay(newDisplay);
        this.displayInitializer.projectDisplay(filePath, newDisplay);
    }

    public void setCaratPosition(int position) {
        this.display.setCaretPosition(position);
    }

    public void setTextScrollPane(JBScrollPane rTextScrollPane) {
        this.rTextScrollPane = rTextScrollPane;
    }

    public void setDisplayInitializer(DisplayInitializer displayInitializer) {
        this.displayInitializer = displayInitializer;
    }

    public Map<String, JBTextArea> getDisplayMap() {
        return displayMap;
    }
}
