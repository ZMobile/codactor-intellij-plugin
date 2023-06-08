package com.translator.service.codactor.ui.measure;



import com.intellij.ui.components.JBTextArea;

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
}
