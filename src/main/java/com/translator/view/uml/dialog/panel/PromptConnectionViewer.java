package com.translator.view.uml.dialog.panel;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.translator.model.uml.draw.figure.LabeledRectangleFigure;
import com.translator.model.uml.node.PromptNode;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PromptConnectionViewer extends JPanel {
    private PromptNode promptNode;

    public PromptConnectionViewer(PromptNode promptNode) {
        this.promptNode = promptNode;
        setLayout(new BorderLayout()); // Set the layout to BorderLayout
        String[] columnNames = {"Connection ID", "Key", "Input/Output"};
        Object[][] data = {
                {"id1", "key1", "Input"},
                {"id2", "key2", "Output"},
                // add more rows as needed
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make the "Connection ID" column non-editable
                if (column == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return JComboBox.class;
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }
        };

        JBTable table = new JBTable(model);

        JComboBox<String> comboBox = new ComboBox<>(new String[]{"Input", "Output"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));

        JScrollPane scrollPane = new JBScrollPane(table);
        add(scrollPane, BorderLayout.CENTER); // Add the scrollPane to the CENTER region

        JToolBar jToolBar = new JToolBar();
        jToolBar.setFloatable(false);
        jToolBar.setBorderPainted(false);

        JLabel label = new JLabel("Connections");
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        jToolBar.add(label);

        add(jToolBar, BorderLayout.NORTH); // Add the toolBar to the SOUTH region
    }
}

