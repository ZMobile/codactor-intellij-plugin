package com.translator.view.uml.node.dialog.prompt;

import com.google.gson.Gson;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.table.JBTable;
import com.translator.model.uml.draw.connection.Connection;
import com.translator.model.uml.draw.figure.*;
import com.translator.model.uml.draw.node.Node;
import com.translator.model.uml.draw.node.PromptNode;
import com.translator.service.uml.node.NodeDialogWindowMapperService;
import com.translator.service.uml.node.PromptHighlighterService;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PromptConnectionViewer extends JPanel {
    private PromptNodeDialog promptNodeDialog;
    private PromptNode promptNode;
    private Drawing drawing;
    private Gson gson;
    private JBTable table;
    private NodeDialogWindowMapperService nodeDialogWindowMapperService;
    private PromptHighlighterService promptHighlighterService;
    private List<MetadataLabeledLineConnectionFigure> inputFigures;
    private List<MetadataLabeledLineConnectionFigure> outputFigures;

    public PromptConnectionViewer(PromptNodeDialog promptNodeDialog,
                                  PromptNode promptNode,
                                  Drawing drawing,
                                  Gson gson,
                                  NodeDialogWindowMapperService nodeDialogWindowMapperService,
                                  PromptHighlighterService promptHighlighterService) {
        this.promptNodeDialog = promptNodeDialog;
        this.promptNode = promptNode;
        this.drawing = drawing;
        this.gson = gson;
        this.nodeDialogWindowMapperService = nodeDialogWindowMapperService;
        this.promptHighlighterService = promptHighlighterService;
        this.inputFigures = new ArrayList<>();
        this.outputFigures = new ArrayList<>();
        setLayout(new BorderLayout()); // Set the layout to BorderLayout
        String[] columnNames = {"Connection ID", "Key", "Input/Output"};
        Object[][] data = {

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
        table.setFillsViewportHeight(true); // Ensure the table takes up all available space in the view
        add(table, BorderLayout.CENTER);
        JComboBox<String> comboBox = new ComboBox<>(new String[]{"Input", "Output"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));
        table.setMinimumSize(new Dimension(0, 50));
        updateConnections();
        add(table, BorderLayout.CENTER);

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
        List<MetadataLabeledLineConnectionFigure> metadataLabeledLineConnectionFigureList = drawing.getChildren().stream()
                .filter(child -> child instanceof MetadataLabeledLineConnectionFigure)
                .map(child -> (MetadataLabeledLineConnectionFigure) child)
                .collect(Collectors.toList());

        inputFigures.clear();
        inputFigures = metadataLabeledLineConnectionFigureList.stream()
                .peek(metadataLabeledLineConnectionFigure -> System.out.println("Checking input: " + getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getEndFigure())))
                .filter(metadataLabeledLineConnectionFigure -> promptNode.getId().equals(getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getEndFigure())))
                .collect(Collectors.toList());

        outputFigures.clear();
        outputFigures = metadataLabeledLineConnectionFigureList.stream()
                .peek(metadataLabeledLineConnectionFigure -> System.out.println("Checking output: " + getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getStartFigure())))
                .filter(metadataLabeledLineConnectionFigure -> promptNode.getId().equals(getNodeIdFromFigure(metadataLabeledLineConnectionFigure.getStartFigure())))
                .collect(Collectors.toList());

        List<Object[]> rowData = new ArrayList<>();

        for (MetadataLabeledLineConnectionFigure input : inputFigures) {
            Connection connection = gson.fromJson(input.getMetadata(), Connection.class);
            Object[] row = {connection.getId(), connection.getInputKey(), "Input"};
            rowData.add(row);
        }

        for (MetadataLabeledLineConnectionFigure output : outputFigures) {
            Connection connection = gson.fromJson(output.getMetadata(), Connection.class);
            Object[] row = {connection.getId(), connection.getOutputKey(), "Output"};
            rowData.add(row);
        }

        DefaultTableModel model = new DefaultTableModel(rowData.toArray(new Object[0][]), new String[]{"Connection ID", "Key (Editable)", "Input/Output"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Only second column is editable now
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return super.getColumnClass(columnIndex);
            }
        };

        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    Object data = model.getValueAt(row, column);
                    String connectionId = model.getValueAt(row, 0).toString();

                    if (column == 1) { // If the "Key" column is changed
                        setConnectionKey(connectionId, data.toString());
                    }
                    promptHighlighterService.highlightPrompts(promptNodeDialog);
                }
            }
        });

        JBTable table = new JBTable(model);
        table.setFillsViewportHeight(true);

// Create a JPanel and add the table to it
        JPanel panel = new JPanel(new BorderLayout());

// Manually create and set the table header
        JTableHeader header = table.getTableHeader();
        panel.add(header, BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);
        panel.setMinimumSize(new Dimension(0, 50));

        this.add(panel, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    public String getNodeIdFromFigure(Figure figure) {
        String metadata = getLabeledNodeMetadata(figure);
        Node node = gson.fromJson(metadata, Node.class);
        return node.getId();
    }

    public String getLabeledNodeMetadata(Figure figure) {
        if (figure instanceof LabeledRectangleFigure) {
            return ((LabeledRectangleFigure) figure).getMetadata();
        } else if (figure instanceof LabeledDiamondFigure) {
            return ((LabeledDiamondFigure) figure).getMetadata();
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

    public void setConnectionKey(String connectionId, String key) {
        drawing.getChildren().stream()
                .filter(child -> child instanceof MetadataLabeledLineConnectionFigure)
                .map(child -> (MetadataLabeledLineConnectionFigure) child)
                .filter(metadataLabeledLineConnectionFigure -> connectionId.equals(gson.fromJson(metadataLabeledLineConnectionFigure.getMetadata(), Connection.class).getId()))
                .forEach(metadataLabeledLineConnectionFigure -> {
                    Connection connection = gson.fromJson(metadataLabeledLineConnectionFigure.getMetadata(), Connection.class);
                    if (connection.getInputNodeId().equals(promptNode.getId())) {
                        connection.setInputKey(key);
                    } else if (connection.getOutputNodeId().equals(promptNode.getId())) {
                        connection.setOutputKey(key);
                    }
                    metadataLabeledLineConnectionFigure.setMetadata(gson.toJson(connection));
                });
    }

    public List<Connection> getInputs() {
        List<Connection> inputs = new ArrayList<>();
        for (MetadataLabeledLineConnectionFigure input : inputFigures) {
            inputs.add(gson.fromJson(input.getMetadata(), Connection.class));
        }
        return inputs;
    }

    public List<Connection> getOutputs() {
        List<Connection> outputs = new ArrayList<>();
        for (MetadataLabeledLineConnectionFigure output : outputFigures) {
            outputs.add(gson.fromJson(output.getMetadata(), Connection.class));
        }
        return outputs;
    }
}