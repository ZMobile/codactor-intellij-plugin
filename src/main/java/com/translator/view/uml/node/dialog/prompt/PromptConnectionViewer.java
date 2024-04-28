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
    private JBTable inputTable;
    private JBTable outputTable;
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
        /*String[] columnNames = {"Connection ID", "Key", "Input/Output"};
        Object[][] data = {

        }*/


        String[] inputColumns = {"Connection ID", "Content-Type", "Key","Required By"};
        String[] outputColumns = {"Connection ID", "Content-Type", "Key", "Sent By"};

        Object[][] inputData = {

        };


        Object[][] outputData = {

        };

        DefaultTableModel inputModel = new DefaultTableModel(inputData, inputColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make the "Connection ID" column non-editable
                return column != 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return String.class;
                } else if (columnIndex == 1) {
                    return JComboBox.class;
                } else if (columnIndex == 2) {
                    return String.class;
                } else if (columnIndex == 3) {
                    return Integer.class;
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }
        };

        DefaultTableModel outputModel = new DefaultTableModel(outputData, outputColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make the "Connection ID" column non-editable
                return column != 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return String.class;
                } else if (columnIndex == 1) {
                    return JComboBox.class;
                } else if (columnIndex == 2) {
                    return String.class;
                } else if (columnIndex == 3) {
                    return Integer.class;
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }
        };

        inputTable =new

        JBTable(inputModel);

        outputTable =new

        JBTable(outputModel);

        inputTable.setFillsViewportHeight(true); // Ensure the table takes up all available space in the view
        outputTable.setFillsViewportHeight(true);

            JComboBox<String> comboBox = new ComboBox<>(new String[]{"Input", "Output"});
        inputTable.getColumnModel().

            getColumn(2).

            setCellEditor(new DefaultCellEditor(comboBox));
        outputTable.getColumnModel().

            getColumn(2).

            setCellEditor(new DefaultCellEditor(comboBox));

        inputTable.setMinimumSize(new

            Dimension(0,50));
        outputTable.setMinimumSize(new

            Dimension(0,50));

            //updateConnections();


        JLabel label1 = new JLabel("Inputs");
        label1.setHorizontalTextPosition(SwingConstants.CENTER);
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JToolBar jToolBar1 = new JToolBar();
        jToolBar1.setFloatable(false);
        jToolBar1.setBorderPainted(false);

        jToolBar1.add(label1);


        JToolBar jToolBar2 = new JToolBar();
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);

        jToolBar2.add(label1);

        JLabel label2 = new JLabel("Outputs");
        label2.setHorizontalTextPosition(SwingConstants.CENTER);
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);

        jToolBar2.add(label2);

        JPanel northPanel = new JPanel(new BorderLayout());

        northPanel.add(jToolBar1, BorderLayout.NORTH);
        northPanel.add(inputTable, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        JPanel southPanel = new JPanel(new BorderLayout());

        southPanel.add(jToolBar2, BorderLayout.NORTH);
        southPanel.add(outputTable, BorderLayout.CENTER);

        add(southPanel, BorderLayout.SOUTH);

        // You need to add your own functionality to handle the movement of rows between tables when the "Input/Output" column is changed

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

        DefaultTableModel inputModel = (DefaultTableModel) this.inputTable.getModel();
        DefaultTableModel outputModel = (DefaultTableModel) this.outputTable.getModel();
        inputModel.setRowCount(0);
        outputModel.setRowCount(0);

        for (MetadataLabeledLineConnectionFigure input : inputFigures) {
            Connection connection = gson.fromJson(input.getMetadata(), Connection.class);
            Object[] row = {connection.getId(), connection.getInputKey(), "Input", 0};
            inputModel.addRow(row);
        }

        for (MetadataLabeledLineConnectionFigure output : outputFigures) {
            Connection connection = gson.fromJson(output.getMetadata(), Connection.class);
            Object[] row = {connection.getId(), connection.getOutputKey(), "Output", 0};
            outputModel.addRow(row);
        }

        inputModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    Object data = inputModel.getValueAt(row, column);
                    String connectionId = inputModel.getValueAt(row, 0).toString();

                    if (column == 1) { // If the "Key" column is changed
                        setConnectionKey(connectionId, data.toString());
                    }
                    promptHighlighterService.highlightPrompts(promptNodeDialog);
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());

// Manually create and set the table header
        /*JTableHeader header = table.getTableHeader();
        panel.add(header, BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);
        panel.setMinimumSize(new Dimension(0, 50));

        this.add(panel, BorderLayout.CENTER);*/


        /* Add listener on inputModel to handle row moving when Input is changed to Output */


        /* Add listener on outputModel to handle row moving when Output is changed to Input */


        // Refresh the panel
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