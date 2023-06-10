package com.translator.service.codactor.ui.measure;

import com.intellij.ui.components.JBTextArea;

import javax.swing.*;

public interface TextAreaHeightCalculatorService {
    int calculateDesiredHeight(JBTextArea textArea, int width, boolean folding);

    int calculateDesiredHeight(JTextPane textPane, int width, boolean folding);
}
