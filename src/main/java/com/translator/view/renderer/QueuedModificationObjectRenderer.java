package com.translator.view.renderer;

import com.intellij.openapi.project.Project;
import com.translator.model.modification.*;
import com.translator.service.file.FileReaderService;
import com.translator.service.line.LineCounterService;
import com.translator.service.line.LineCounterServiceImpl;
import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class QueuedModificationObjectRenderer extends JPanel implements ListCellRenderer<QueuedFileModificationObjectHolder> {
    private Project project;
    private FileReaderService fileReaderService;
    private LineCounterService lineCounterService;
    private JLabel modificationTypeLabel;
    private JLabel lineRangeLabel;
    private JLabel statusLabel;
    private JLabel filePathLabel;

    public QueuedModificationObjectRenderer(Project project,
                                            FileReaderService fileReaderService) {
        this.project = project;
        this.fileReaderService = fileReaderService;
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
    public Component getListCellRendererComponent(JList<? extends QueuedFileModificationObjectHolder> list, QueuedFileModificationObjectHolder value, int index, boolean isSelected, boolean cellHasFocus) {
        String lineRangeText = "";
        String statusText = "";
        if (value.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
            FileModificationSuggestionModification fileModificationSuggestionModification = value.getFileModificationSuggestionModification();
            filePathLabel.setText("File: (SUB) " + fileModificationSuggestionModification.getFilePath());
            lineRangeText = "Index: " + fileModificationSuggestionModification.getRangeMarker().getStartOffset() + " - " + fileModificationSuggestionModification.getRangeMarker().getEndOffset();
            statusText = "(Queued)";
            setBackground(Color.decode("#009688"));
        } else if (value.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION) {
            FileModification fileModification = value.getFileModification();
            filePathLabel.setText("File: " + fileModification.getFilePath());
            String fileContent = fileReaderService.readFileContent(project, fileModification.getFilePath());
            int startLine = lineCounterService.countLines(fileContent, fileModification.getRangeMarker().getStartOffset());
            int endLine = lineCounterService.countLines(fileContent, fileModification.getRangeMarker().getEndOffset());
            lineRangeText = "Lines: " + startLine + " - " + endLine;
            statusText = fileModification.isDone() ? "(Done)" : "(Queued)";
            setBackground(fileModification.isDone() ? Color.GREEN : Color.decode("#009688"));
        } else if (value.getQueuedModificationObjectType() == QueuedModificationObjectType.MULTI_FILE_MODIFICATION) {
            MultiFileModification multiFileModification = value.getMultiFileModification();
            lineRangeText = "Multi-File Mod Stage: " + multiFileModification.getStage();
            statusText = "(Queued)";
            filePathLabel.setText("File: " + multiFileModification.getFilePath());
            setBackground(Color.ORANGE);
        }
        lineRangeLabel.setText(lineRangeText);
        statusLabel.setText(statusText);
        return this;
    }
}