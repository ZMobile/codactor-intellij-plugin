package com.translator.view.renderer;

import com.translator.model.history.data.HistoricalContextModificationDataHolder;
import com.translator.model.modification.RecordType;
import com.translator.service.line.LineCounterService;
import com.translator.service.line.LineCounterServiceImpl;

import javax.swing.*;
import java.awt.*;

public class HistoricalCompletedFileModificationRenderer extends JPanel implements ListCellRenderer<HistoricalContextModificationDataHolder> {
    private LineCounterService lineCounterService;
    private JLabel modificationTypeLabel;
    private JLabel lineRangeLabel;
    private JLabel statusLabel;
    private JLabel filePathLabel;

    public HistoricalCompletedFileModificationRenderer() {
        lineCounterService = new LineCounterServiceImpl();
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
    public Component getListCellRendererComponent(JList<? extends HistoricalContextModificationDataHolder> list, HistoricalContextModificationDataHolder value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.getRecordType() != null && value.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
            filePathLabel.setText("File: (SUB) " + value.getFileModificationSuggestionModificationRecord().getFilePath());
            modificationTypeLabel.setText("Type: " + value.getFileModificationSuggestionModificationRecord().getModificationType() + " Timestamp: " + value.getFileModificationSuggestionModificationRecord().getCreationTimestamp());
            lineRangeLabel.setText("Lines: " + lineCounterService.countLines(value.getFileModificationSuggestionModificationRecord().getEditedCode().trim(), value.getFileModificationSuggestionModificationRecord().getEditedCode().trim().length()));
            setBackground(Color.decode("#228B22"));
        } else if (value.getRecordType() != null && value.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
            filePathLabel.setText("File: " + value.getFileModificationSuggestionRecord().getFilePath());
            modificationTypeLabel.setText("Type: " + value.getFileModificationSuggestionRecord().getModificationType() + " Timestamp: " + value.getFileModificationSuggestionRecord().getCreationTimestamp());
            lineRangeLabel.setText("Lines: " + lineCounterService.countLines(value.getFileModificationSuggestionRecord().getSuggestedCode().trim(), value.getFileModificationSuggestionRecord().getSuggestedCode().trim().length()));
            setBackground(Color.decode("#228B22"));
        } else if (value.getRecordType() == null) {
            filePathLabel.setText("New General Inquiry");
            modificationTypeLabel.setText("");
            lineRangeLabel.setText("");
        }

        return this;
    }
}