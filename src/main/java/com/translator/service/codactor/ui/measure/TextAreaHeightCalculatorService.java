package com.translator.service.codactor.ui.measure;

import com.intellij.ui.components.JBTextArea;

public interface TextAreaHeightCalculatorService {
    int calculateDesiredHeight(JBTextArea textArea, int width, boolean folding);
}
