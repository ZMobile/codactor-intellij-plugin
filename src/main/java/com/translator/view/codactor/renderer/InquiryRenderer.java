package com.translator.view.codactor.renderer;

import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.service.codactor.line.LineCounterService;
import com.translator.service.codactor.line.LineCounterServiceImpl;

import javax.swing.*;
import java.awt.*;

public class InquiryRenderer extends JPanel implements ListCellRenderer<Inquiry> {
    private LineCounterService lineCounterService;
    private JLabel inquiryLabel;
    private JLabel subjectLineLabel;
    private JLabel filePathLabel;
    private JLabel statusLabel;

    public InquiryRenderer() {
        lineCounterService = new LineCounterServiceImpl();
        setOpaque(true);
        setPreferredSize(new Dimension(100, 40));

        subjectLineLabel = new JLabel();
        filePathLabel = new JLabel();
        statusLabel = new JLabel();
        inquiryLabel = new JLabel();

        // Add an EmptyBorder with a preferred padding value
        int padding = 5;
        subjectLineLabel.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
        filePathLabel.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, 0));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, padding));
        inquiryLabel.setBorder(BorderFactory.createEmptyBorder(10, padding, 0, 0));


        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(inquiryLabel)
                        .addComponent(subjectLineLabel)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(filePathLabel)
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
                        .addComponent(subjectLineLabel)
                        .addComponent(filePathLabel)
        );
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Inquiry> list, Inquiry value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.getSubjectRecordId() != null) {
            subjectLineLabel.setText(value.getSubjectLine());
            inquiryLabel.setText("Modification Inquiry");
            filePathLabel.setText("Path: " + value.getFilePath());
        } else if (value.getSubjectCode() != null) {
            subjectLineLabel.setText(value.getSubjectLine());
            inquiryLabel.setText("Code Inquiry");
            filePathLabel.setText("Path: " + value.getFilePath());
        } else {
            subjectLineLabel.setText(value.getSubjectLine());
            inquiryLabel.setText("General Inquiry");
            filePathLabel.setText("");
        }
        setBackground(Color.decode("#AA00FF"));

        //String statusText = value.isDone() ? "(Done)" : "(Queued)";
        //statusLabel.setText(statusText);

        //setBackground(value.isDone() ? Color.decode("#228B22") : Color.decode("#009688"));

        return this;
    }
}
