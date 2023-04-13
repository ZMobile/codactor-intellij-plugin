package com.translator.view.renderer;

import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestionModification;
import com.translator.service.line.LineCounterService;
import com.translator.service.line.LineCounterServiceImpl;
import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class FileModificationRenderer extends JPanel implements ListCellRenderer<FileModification> {
    private Map<String, JBTextArea> displayMap;
    private LineCounterService lineCounterService;
    private JLabel modificationTypeLabel;
    private JLabel lineRangeLabel;
    private JLabel statusLabel;
    private JLabel filePathLabel;

    public FileModificationRenderer(Map<String, JBTextArea> displayMap) {
        this.displayMap = displayMap;
        this.lineCounterService = new LineCounterServiceImpl();
        setOpaque(true);
        setPreferredSize(new Dimension(100, 40));

        modificationTypeLabel = new JLabel();
        lineRangeLabel = new JLabel();
        statusLabel = new JLabel();
        filePathLabel = new JLabel();

        // Add an EmptyBorder with a preferred padding value
        int padding = 5;
        modificationTypeLabel.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
        lineRangeLabel.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, padding));
        filePathLabel.setBorder(BorderFactory.createEmptyBorder(10, padding, 0, 0));


        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(filePathLabel)
                        .addComponent(modificationTypeLabel)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(lineRangeLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                )
                                .addComponent(statusLabel)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(filePathLabel)
                                .addComponent(statusLabel)
                        )
                        .addComponent(modificationTypeLabel)
                        .addComponent(lineRangeLabel)
        );
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends FileModification> list, FileModification value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof FileModificationSuggestionModification) {
            filePathLabel.setText("File: (SUB) " + value.getFilePath());
        } else {
            filePathLabel.setText("File: " + value.getFilePath());
        }

        modificationTypeLabel.setText("Type: " + value.getModificationType());
        JBTextArea display = displayMap.get(value.getFilePath());
        String lineRangeText;
        if (value instanceof FileModificationSuggestionModification) {
            lineRangeText = "Index: " + value.getStartIndex() + " - " + value.getEndIndex();
        } else {
            int startLine = lineCounterService.countLines(display.getText(), value.getStartIndex());
            int endLine = lineCounterService.countLines(display.getText(), value.getEndIndex());
            lineRangeText = "Lines: " + startLine + " - " + endLine;
        }
        lineRangeLabel.setText(lineRangeText);

        String statusText = value.isDone() ? "(Done)" : "(Queued)";
        statusLabel.setText(statusText);

        setBackground(value.isDone() ? Color.GREEN : Color.decode("#7FFFD4"));

        return this;
    }
}