package com.translator.view.renderer;

import com.translator.model.history.HistoricalContextObjectType;
import com.translator.model.history.data.HistoricalContextModificationDataHolder;
import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.modification.RecordType;
import com.translator.service.line.LineCounterService;
import com.translator.service.line.LineCounterServiceImpl;

import javax.swing.*;
import java.awt.*;

public class HistoricalContextObjectHolderRenderer extends JPanel implements ListCellRenderer<HistoricalContextObjectDataHolder> {
    private LineCounterService lineCounterService;
    private JLabel leftLabelOne;
    private JLabel leftLabelTwo;
    private JLabel labelThree;
    private JLabel statusLabel;

    public HistoricalContextObjectHolderRenderer() {
        lineCounterService = new LineCounterServiceImpl();
        setOpaque(true);
        setPreferredSize(new Dimension(100, 40));

        leftLabelTwo = new JLabel();
        labelThree = new JLabel();
        statusLabel = new JLabel();
        leftLabelOne = new JLabel();

        // Add an EmptyBorder with a preferred padding value
        int padding = 5;
        leftLabelTwo.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
        labelThree.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, padding));
        leftLabelOne.setBorder(BorderFactory.createEmptyBorder(10, padding, 0, 0));


        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(leftLabelOne)
                        .addComponent(leftLabelTwo)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(labelThree)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                )
                                .addComponent(statusLabel)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(leftLabelOne)
                                .addComponent(statusLabel)
                        )
                        .addComponent(leftLabelTwo)
                        .addComponent(labelThree)
        );
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends HistoricalContextObjectDataHolder> list, HistoricalContextObjectDataHolder value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
            HistoricalContextModificationDataHolder historicalContextModificationDataHolder = value.getHistoricalCompletedModificationDataHolder();
            if (historicalContextModificationDataHolder.getRecordType() != null && historicalContextModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                leftLabelOne.setText("File: (SUB) " + historicalContextModificationDataHolder.getFileModificationSuggestionModificationRecord().getFilePath());
                leftLabelTwo.setText("Type: " + historicalContextModificationDataHolder.getFileModificationSuggestionModificationRecord().getModificationType() + " Timestamp: " + historicalContextModificationDataHolder.getFileModificationSuggestionModificationRecord().getCreationTimestamp());
                labelThree.setText("Lines: " + lineCounterService.countLines(historicalContextModificationDataHolder.getFileModificationSuggestionModificationRecord().getEditedCode().trim(), historicalContextModificationDataHolder.getFileModificationSuggestionModificationRecord().getEditedCode().trim().length()));
            } else if (historicalContextModificationDataHolder.getRecordType() != null && historicalContextModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
                leftLabelOne.setText("File: " + historicalContextModificationDataHolder.getFileModificationSuggestionRecord().getFilePath());
                leftLabelTwo.setText("Type: " + historicalContextModificationDataHolder.getFileModificationSuggestionRecord().getModificationType() + " Timestamp: " + historicalContextModificationDataHolder.getFileModificationSuggestionRecord().getCreationTimestamp());
                labelThree.setText("Lines: " + lineCounterService.countLines(historicalContextModificationDataHolder.getFileModificationSuggestionRecord().getSuggestedCode().trim(), historicalContextModificationDataHolder.getFileModificationSuggestionRecord().getSuggestedCode().trim().length()));
            }
            if (isSelected) {
                setBackground(Color.GREEN.darker());
            } else {
                setBackground(Color.GREEN);
            }
        } else {
            Inquiry inquiry = value.getHistoricalContextInquiryDataHolder().getInquiry();
            if (inquiry.getSubjectRecordId() != null) {
                leftLabelOne.setText("Path: " + inquiry.getFilePath());
                leftLabelOne.setText("Modification Inquiry (Creation timestamp UTC): " + inquiry.getCreationTimestamp().toString());
            } else if (inquiry.getSubjectCode() != null) {
                leftLabelOne.setText("Path: " + inquiry.getFilePath());
                leftLabelOne.setText("Code Inquiry (Creation timestamp UTC): " + inquiry.getCreationTimestamp().toString());
            } else {
                leftLabelOne.setText("General Inquiry (Creation timestamp UTC): " + inquiry.getCreationTimestamp().toString());
            }
            leftLabelTwo.setText("");
            labelThree.setText("");
            if (isSelected) {
                setBackground(Color.decode("#CC99FF").darker());
            } else {
                setBackground(Color.decode("#CC99FF"));
            }
        }


        //String statusText = value.isDone() ? "(Done)" : "(Queued)";
        //statusLabel.setText(statusText);

        //setBackground(value.isDone() ? Color.GREEN : Color.decode("#7FFFD4"));

        return this;
    }

}
