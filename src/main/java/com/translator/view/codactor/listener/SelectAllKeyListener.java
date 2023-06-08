package com.translator.view.codactor.listener;

import com.intellij.ui.components.JBTextArea;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SelectAllKeyListener implements KeyListener {
    private JBTextArea display;

    public SelectAllKeyListener(JBTextArea display) {
        this.display = display;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0 || (e.getModifiers() & KeyEvent.META_MASK) != 0)) {
            display.setSelectionStart(0);
            display.setSelectionEnd(display.getText().length());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
