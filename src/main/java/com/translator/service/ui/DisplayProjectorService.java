package com.translator.service.ui;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;


import java.util.Map;

public interface DisplayProjectorService {
    void projectDisplay(String filePath, JBTextArea display);

    void setCaratPosition(int position);

    void setTextScrollPane(JBScrollPane rTextScrollPane);

    void setDisplayInitializer(DisplayProjectorServiceImpl.DisplayInitializer displayInitializer);

    Map<String, JBTextArea> getDisplayMap();
}
