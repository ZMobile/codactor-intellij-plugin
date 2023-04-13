package com.translator.view.panel;


import javax.swing.*;
import java.awt.*;

public class FixedHeightPanel extends JPanel {
    private int fixedHeight;

    public FixedHeightPanel(int fixedHeight) {
        this.fixedHeight = fixedHeight;
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
}