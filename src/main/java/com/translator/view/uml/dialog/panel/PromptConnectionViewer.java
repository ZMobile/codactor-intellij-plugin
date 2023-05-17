package com.translator.view.uml.dialog.panel;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PromptConnectionViewer extends JPanel {
    public PromptConnectionViewer() {
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

        // Set up the editor for the "Input/Output" column
        JComboBox<String> comboBox = new ComboBox<>(new String[]{"Input", "Output"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));

        // Add the table to a scroll pane, then add the scroll pane to this panel
        JScrollPane scrollPane = new JBScrollPane(table);
        add(scrollPane);
    }
}