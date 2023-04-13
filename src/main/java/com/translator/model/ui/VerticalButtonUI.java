package com.translator.model.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class VerticalButtonUI extends BasicButtonUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(Math.PI / 2, c.getWidth() / 2, c.getHeight() / 2.0);
        g2.translate(-50, 0);
        super.paint(g, c);
    }
}
