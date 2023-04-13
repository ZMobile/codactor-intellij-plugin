package com.translator.view.menu;



import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TextAreaWindow {
    public interface TextAreaWindowActionListener {
        void onOk(String text);
    }

    private TextAreaWindowActionListener listener;
    private String text;

    public TextAreaWindow(String initialText) {
        SwingUtilities.invokeLater(() -> {
            JBTextArea textArea = new JBTextArea(20, 60);
            textArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0 || (e.getModifiers() & KeyEvent.META_MASK) != 0)) {
                        textArea.setSelectionStart(0);
                        textArea.setSelectionEnd(textArea.getText().length());
                    }
                }
            });
            textArea.setText(initialText);

            JBScrollPane scrollPane = new JBScrollPane(textArea);

            int option = JOptionPane.showOptionDialog(null, scrollPane,
                    "Copy/Selection window", JOptionPane.OK_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK"}, "OK");
            if (option == JOptionPane.OK_OPTION) {
                text = textArea.getText();
            }
        });
        this.listener = null;
    }

    public TextAreaWindow(String header, String initialText, boolean lineWrapping) {
        SwingUtilities.invokeLater(() -> {
            JBTextArea textArea = new JBTextArea(20, 40);
            textArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0 || (e.getModifiers() & KeyEvent.META_MASK) != 0)) {
                        textArea.setSelectionStart(0);
                        textArea.setSelectionEnd(textArea.getText().length());
                    }
                }
            });
            textArea.setText(initialText);
            JBScrollPane scrollPane = new JBScrollPane(textArea);

            if (lineWrapping) {
                textArea.setLineWrap(true);
                scrollPane.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }

            int option = JOptionPane.showOptionDialog(null, scrollPane,
                    "Copy/Selection window", JOptionPane.OK_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK"}, "OK");
            if (option == JOptionPane.OK_OPTION) {
                text = textArea.getText();
            }
        });
        this.listener = null;
    }

    public TextAreaWindow(String header, String initialText, boolean lineWrapping, String cancelButtonText, String okButtonText, TextAreaWindowActionListener textAreaWindowActionListener) {
        this.listener = textAreaWindowActionListener;
        SwingUtilities.invokeLater(() -> {
            JBTextArea textArea = new JBTextArea(20, 40);
            textArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0 || (e.getModifiers() & KeyEvent.META_MASK) != 0)) {
                        textArea.setSelectionStart(0);
                        textArea.setSelectionEnd(textArea.getText().length());
                    }
                }
            });
            textArea.setText(initialText);
            JBScrollPane scrollPane = new JBScrollPane(textArea);

            if (lineWrapping) {
                textArea.setLineWrap(true);
                scrollPane.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }

            int option = JOptionPane.showOptionDialog(null, scrollPane,
                    header, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, new Object[]{okButtonText, cancelButtonText}, okButtonText);
            if (option == JOptionPane.OK_OPTION) {
                listener.onOk(textArea.getText());
            }
        });
    }

    public String getText() {
        return text;
    }

}
