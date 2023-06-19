package com.translator.view.codactor.viewer.inquiry;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class InquiryChatBoxViewer extends JPanel {
    private JBTextArea promptInput;
    private JButton jButton1;
    private JButton jButton2;

    public InquiryChatBoxViewer() {
        initComponents();
        addComponents();
    }

    private void initComponents() {
        promptInput = new JBTextArea();
        promptInput.setLineWrap(true);
        promptInput.setWrapStyleWord(true);

        jButton1 = new JButton();
        jButton1.setText("Ask");
        jButton1.setToolTipText("");

        jButton2 = new JButton();
        jButton2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));
        jButton2.setMaximumSize(new Dimension(80, 23));
        jButton2.setMinimumSize(new Dimension(80, 23));
        jButton2.setPreferredSize(new Dimension(80, 23));
        jButton2.setSize(new Dimension(80, 23));

        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Handle "Ask" button action logic
                // ...
            }
        });

        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Handle microphone button action logic
                // ...
            }
        });
    }

    private void addComponents() {
        setLayout(new BorderLayout());

        JBScrollPane jBScrollPane2 = new JBScrollPane(promptInput);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        buttonPanel.add(jButton2);
        buttonPanel.add(jButton1);

        add(jBScrollPane2, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }
}

    /*With the logic split up between the two classes, you can now use them in your main class like this:

private void initComponents() {
        InquiryChatListViewer inquiryChatListViewer = new InquiryChatListViewer();
        InquiryChatBoxViewer inquiryChatBoxViewer = new InquiryChatBoxViewer();

        // Add inquiryChatListViewer and inquiryChatBoxViewer to the main panel
        setLayout(new BorderLayout());
        add(inquiryChatListViewer, BorderLayout.CENTER);
        add(inquiryChatBoxViewer, BorderLayout.SOUTH);
        }
        */
