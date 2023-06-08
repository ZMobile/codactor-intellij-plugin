package com.translator.view.uml.dialog.prompt;

import com.google.gson.Gson;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.draw.figure.*;
import com.translator.model.uml.node.Node;
import com.translator.model.uml.node.PromptNode;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PromptConnectionViewer extends JPanel {
    private PromptNode promptNode;
    private Drawing drawing;
    private Gson gson;
    private JBTable table;

    public PromptConnectionViewer(PromptNode promptNode, Drawing drawing, Gson gson) {
        this.promptNode = promptNode;
        this.drawing = drawing;
        this.gson = gson;
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

        table = new JBTable(model);

        JComboBox<String> comboBox = new ComboBox<>(new String[]{"Input", "Output"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));
        System.out.println("This gets called 1");
        updateConnections();
        System.out.println("This gets called 2");
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

    public void updateConnections() {
        for (Figure figure : drawing.getChildren()) {
            if (figure instanceof LabeledRectangleFigure) {
                LabeledRectangleFigure labeledRectangleFigure = (LabeledRectangleFigure) figure;
                Node node = gson.fromJson(labeledRectangleFigure.getMetadata(), Node.class);
                System.out.println("Node: " + node.getId());
            }
        }
        List<MetadataLabeledLineConnectionFigure> metadataLabeledLineConnectionFigureList = drawing.getChildren().stream()
                .filter(child -> child instanceof MetadataLabeledLineConnectionFigure)
                .map(child -> (MetadataLabeledLineConnectionFigure) child)
                .collect(Collectors.toList());
        for (MetadataLabeledLineConnectionFigure metadataLabeledLineConnectionFigure : metadataLabeledLineConnectionFigureList) {
            System.out.println("Metadata: " + metadataLabeledLineConnectionFigure.getMetadata());
            System.out.println("Start: " + getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getStartFigure()));
            System.out.println("End: " + getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getEndFigure()));
        }
        List<MetadataLabeledLineConnectionFigure> inputs = metadataLabeledLineConnectionFigureList.stream()
                .peek(metadataLabeledLineConnectionFigure -> System.out.println("Checking input: " + getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getEndFigure())))
                .filter(metadataLabeledLineConnectionFigure -> promptNode.getId().equals(getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getEndFigure())))
                .collect(Collectors.toList());

        List<MetadataLabeledLineConnectionFigure> outputs = metadataLabeledLineConnectionFigureList.stream()
                .peek(metadataLabeledLineConnectionFigure -> System.out.println("Checking output: " + getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getStartFigure())))
                .filter(metadataLabeledLineConnectionFigure -> promptNode.getId().equals(getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getStartFigure())))
                .collect(Collectors.toList());


        List<Object[]> rowData = new ArrayList<>();

        for (MetadataLabeledLineConnectionFigure input : inputs) {
            Connection connection = gson.fromJson(input.getMetadata(), Connection.class);
            Object[] row = {connection.getId(), connection.getInputKey(), "Input"};
            rowData.add(row);
        }

        for (MetadataLabeledLineConnectionFigure output : outputs) {
            Connection connection = gson.fromJson(output.getMetadata(), Connection.class);
            Object[] row = {connection.getId(), connection.getOutputKey(), "Output"};
            rowData.add(row);
        }

        DefaultTableModel model = new DefaultTableModel(rowData.toArray(new Object[0][]), new String[]{"Connection ID", "Key", "Input/Output"}) {
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

        // assuming that 'table' is a member variable
        table.setModel(model);
    }

    public String getNodeIdFromFigure(Figure figure) {
        String metadata = getLabeledNodeMetadata(figure);
        System.out.println("Testo metadata: " + metadata);
        Node node = gson.fromJson(metadata, Node.class);
        return node.getId();
    }

    public String getLabeledNodeMetadata(Figure figure) {
        if (figure instanceof LabeledRectangleFigure) {
            return ((LabeledRectangleFigure) figure).getMetadata();
        } else if (figure instanceof LabeledDiamondFigure) {
            return  ((LabeledDiamondFigure) figure).getMetadata();
        } else if (figure instanceof LabeledEllipseFigure) {
            return ((LabeledEllipseFigure) figure).getMetadata();
        } else if (figure instanceof LabeledRoundRectangleFigure) {
            return ((LabeledRoundRectangleFigure) figure).getMetadata();
        } else if (figure instanceof LabeledTriangleFigure) {
            return ((LabeledTriangleFigure) figure).getMetadata();
        } else {
            return null;
        }
    }
}

