package com.translator.view.codactor.menu;



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

    // other constructors omitted for brevity

    public TextAreaWindow(String initialText) {
        this(initialText, null);
    }


    public TextAreaWindow(String initialText, TextAreaWindowActionListener listener) {
        this.listener = listener;
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog((Frame) null, "Copy/Selection window", true);
            dialog.setLayout(new BorderLayout());

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
            textArea.setCaretPosition(0);

            JBScrollPane scrollPane = new JBScrollPane(textArea);

            JPanel buttonPanel = new JPanel();
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                listener.onOk(textArea.getText());
                text = textArea.getText();
                dialog.dispose();
            });
            buttonPanel.add(okButton);

            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.pack();
            dialog.setResizable(true);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }



    public TextAreaWindow(String header, String initialText, boolean lineWrapping, String cancelButtonText, String okButtonText, TextAreaWindowActionListener textAreaWindowActionListener) {
        this.listener = textAreaWindowActionListener;
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog((Frame) null, header, true);
            dialog.setLayout(new BorderLayout());

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

            JPanel buttonPanel = new JPanel();
            JButton okButton = new JButton(okButtonText);
            okButton.addActionListener(e -> {
                listener.onOk(textArea.getText());
                dialog.dispose();
            });
            JButton cancelButton = new JButton(cancelButtonText);
            cancelButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.pack();
            dialog.setResizable(true);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }

    public String getText() {
        return text;
    }

}
