package com.translator.view.codactor.panel;


import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import java.awt.*;

public class FixedHeightPanel extends JPanel {
    private Editor editor;
    private int fixedHeight;

    public FixedHeightPanel(Editor editor) {
        this.editor = editor;
        this.fixedHeight = editor.getLineHeight() * getLineCount(editor.getDocument().getText());
    }

    private int getLineCount(String code) {
        String[] lines = code.split("\\r?\\n");
        return lines.length;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        preferredSize.height = fixedHeight;
        return preferredSize;
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension minimumSize = super.getMinimumSize();
        minimumSize.height = fixedHeight;
        return minimumSize;
    }

    public Editor getEditor() {
        return editor;
    }
}