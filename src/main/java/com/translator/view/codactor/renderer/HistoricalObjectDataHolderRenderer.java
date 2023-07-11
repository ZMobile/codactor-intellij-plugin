package com.translator.view.codactor.renderer;

import com.translator.model.codactor.history.HistoricalContextObjectType;
import com.translator.model.codactor.history.data.HistoricalFileModificationDataHolder;
import com.translator.model.codactor.history.data.HistoricalObjectDataHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.modification.RecordType;
import com.translator.service.codactor.line.LineCounterService;
import com.translator.service.codactor.line.LineCounterServiceImpl;

import javax.swing.*;
import java.awt.*;

public class HistoricalObjectDataHolderRenderer extends JPanel implements ListCellRenderer<HistoricalObjectDataHolder> {
    private LineCounterService lineCounterService;
    private JLabel leftLabelOne;
    private JLabel leftLabelTwo;
    private JLabel labelThree;
    private JLabel statusLabel;

    public HistoricalObjectDataHolderRenderer() {
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
    public Component getListCellRendererComponent(JList<? extends HistoricalObjectDataHolder> list, HistoricalObjectDataHolder value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.getHistoricalContextObjectType() == HistoricalContextObjectType.FILE_MODIFICATION) {
            HistoricalFileModificationDataHolder historicalFileModificationDataHolder = value.getHistoricalModificationDataHolder();
            if (historicalFileModificationDataHolder.getRecordType() != null && historicalFileModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
                leftLabelOne.setText("File: (SUB) " + historicalFileModificationDataHolder.getFileModificationSuggestionModificationRecord().getFilePath());
                leftLabelTwo.setText("Type: " + historicalFileModificationDataHolder.getFileModificationSuggestionModificationRecord().getModificationType() + " Subject: " + historicalFileModificationDataHolder.getFileModificationSuggestionModificationRecord().getSubjectLine());
                labelThree.setText("Lines: " + lineCounterService.countLines(historicalFileModificationDataHolder.getFileModificationSuggestionModificationRecord().getEditedCode().trim(), historicalFileModificationDataHolder.getFileModificationSuggestionModificationRecord().getEditedCode().trim().length()));
            } else if (historicalFileModificationDataHolder.getRecordType() != null && historicalFileModificationDataHolder.getRecordType() == RecordType.FILE_MODIFICATION_SUGGESTION) {
                leftLabelOne.setText("File: " + historicalFileModificationDataHolder.getFileModificationSuggestionRecord().getFilePath());
                leftLabelTwo.setText("Type: " + historicalFileModificationDataHolder.getFileModificationSuggestionRecord().getModificationType() + " Subject: " + historicalFileModificationDataHolder.getFileModificationSuggestionRecord().getSubjectLine());
                labelThree.setText("Lines: " + lineCounterService.countLines(historicalFileModificationDataHolder.getFileModificationSuggestionRecord().getSuggestedCode().trim(), historicalFileModificationDataHolder.getFileModificationSuggestionRecord().getSuggestedCode().trim().length()));
            }
            if (isSelected) {
                setBackground(Color.decode("#228B22").darker());
            } else {
                setBackground(Color.decode("#228B22"));
            }
        } else {
            Inquiry inquiry = value.getHistoricalContextInquiryDataHolder().getInquiry();
            if (inquiry.getSubjectRecordId() != null) {
                leftLabelOne.setText("Modification Inquiry");
                leftLabelTwo.setText(value.getHistoricalContextInquiryDataHolder().getInquiry().getSubjectLine());
                labelThree.setText("Path: " + inquiry.getFilePath());
            } else if (inquiry.getSubjectCode() != null) {
                leftLabelTwo.setText("Code Inquiry");
                leftLabelOne.setText(value.getHistoricalContextInquiryDataHolder().getInquiry().getSubjectLine());
                labelThree.setText("Path: " + inquiry.getFilePath());
            } else {
                leftLabelOne.setText("General Inquiry");
                leftLabelTwo.setText(value.getHistoricalContextInquiryDataHolder().getInquiry().getSubjectLine());
                labelThree.setText("");
            }
            if (isSelected) {
                setBackground(Color.decode("#AA00FF").darker());
            } else {
                setBackground(Color.decode("#AA00FF"));
            }
        }


        //String statusText = value.isDone() ? "(Done)" : "(Queued)";
        //statusLabel.setText(statusText);

        //setBackground(value.isDone() ? Color.decode("#228B22") : Color.decode("#009688"));

        return this;
    }

}
