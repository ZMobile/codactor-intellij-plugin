package com.translator.view.renderer;

import com.intellij.ui.JBColor;
import com.translator.model.inquiry.Inquiry;
import com.translator.service.line.LineCounterService;
import com.translator.service.line.LineCounterServiceImpl;

import javax.swing.*;
import java.awt.*;

public class InquiryRenderer extends JPanel implements ListCellRenderer<Inquiry> {
    private LineCounterService lineCounterService;
    private JLabel inquiryLabel;
    private JLabel filePathLabel;
    private JLabel lineRangeLabel;
    private JLabel statusLabel;

    public InquiryRenderer() {
        lineCounterService = new LineCounterServiceImpl();
        setOpaque(true);
        setPreferredSize(new Dimension(100, 40));

        filePathLabel = new JLabel();
        lineRangeLabel = new JLabel();
        statusLabel = new JLabel();
        inquiryLabel = new JLabel();

        // Add an EmptyBorder with a preferred padding value
        int padding = 5;
        filePathLabel.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
        lineRangeLabel.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, padding));
        inquiryLabel.setBorder(BorderFactory.createEmptyBorder(10, padding, 0, 0));


        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(inquiryLabel)
                        .addComponent(filePathLabel)
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
                                .addComponent(inquiryLabel)
                                .addComponent(statusLabel)
                        )
                        .addComponent(filePathLabel)
                        .addComponent(lineRangeLabel)
        );
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Inquiry> list, Inquiry value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.getSubjectRecordId() != null) {
            filePathLabel.setText("Path: " + value.getFilePath());
            inquiryLabel.setText("Modification Inquiry (Creation timestamp UTC): " + value.getCreationTimestamp().toString());
        } else if (value.getSubjectCode() != null) {
            filePathLabel.setText("Path: " + value.getFilePath());
            inquiryLabel.setText("Code Inquiry (Creation timestamp UTC): " + value.getCreationTimestamp().toString());
        } else {
            filePathLabel.setText("Path: null");
            inquiryLabel.setText("General Inquiry (Creation timestamp UTC): " + value.getCreationTimestamp().toString());
        }
        lineRangeLabel.setText("");
        setBackground(Color.decode("#AA00FF"));

        //String statusText = value.isDone() ? "(Done)" : "(Queued)";
        //statusLabel.setText(statusText);

        //setBackground(value.isDone() ? Color.decode("#228B22") : Color.decode("#009688"));

        return this;
    }
}
