package com.translator.service.codactor.ui.measure;


import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class TextAreaHeightCalculatorServiceImpl implements TextAreaHeightCalculatorService {
    @Override
    public int calculateDesiredHeight(JBTextArea textArea, int width, boolean folding) {
        Font font = textArea.getFont();

        FontMetrics fontMetrics = textArea.getFontMetrics(font);
        if (folding) {
            int totalLines = 0;
            String[] lines = textArea.getText().split("\n");
            //List<String> lineRecords = new ArrayList<>();
            for (String line : lines) {
                String[] words = line.split("\\s+");
                StringBuilder lineBuilder = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    if (word.contains("\n")) {
                        //lineRecords.add(lineBuilder.toString().trim());
                        totalLines++;
                        lineBuilder.setLength(0);
                        continue;
                    }
                    int lineWidth = fontMetrics.stringWidth(lineBuilder + word);
                    if (lineWidth > width) {
                        //lineRecords.add(lineBuilder.toString().trim());
                        totalLines++;
                        lineBuilder.setLength(0);
                    }
                    lineBuilder.append(word).append(" ");
                }
                //lineRecords.add(lineBuilder.toString().trim());
                totalLines++;
            }
            //for (String line : lineRecords) {
            //System.out.println(line);
            //}
            return ((totalLines + 1) * textArea.getFontMetrics(textArea.getFont()).getHeight());
        } else {
            return ((textArea.getLineCount() + 1) * textArea.getFontMetrics(textArea.getFont()).getHeight());
        }
    }

    @Override
    public int calculateDesiredHeight(JTextPane textPane, int width, boolean folding) {
        StyledDocument doc = textPane.getStyledDocument();
        FontMetrics fontMetrics = textPane.getFontMetrics(textPane.getFont());

        int totalLines = 0;
        try {
            // Get the text content from the styled document
            String text = doc.getText(0, doc.getLength());

            // Split the text into lines
            String[] lines = text.split("\n");

            for (String line : lines) {
                if (folding) {
                    // Split the line into words
                    String[] words = line.split("\\s+");
                    StringBuilder lineBuilder = new StringBuilder();

                    for (int i = 0; i < words.length; i++) {
                        String word = words[i];

                        if (word.contains("\n")) {
                            totalLines++;
                            lineBuilder.setLength(0);
                            continue;
                        }

                        // Calculate the width of the line
                        int lineWidth = fontMetrics.stringWidth(lineBuilder + word);

                        if (lineWidth > width) {
                            totalLines++;
                            lineBuilder.setLength(0);
                        }

                        lineBuilder.append(word).append(" ");
                    }

                    totalLines++;
                } else {
                    totalLines++;
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return ((totalLines + 1) * fontMetrics.getHeight());
    }
}
