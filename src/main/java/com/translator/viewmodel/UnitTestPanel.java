package com.translator.viewmodel;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UnitTestPanel extends JPanel {
    private JPanel editorPanel;
    private Editor editor;
    private JButton regenerateButton;
    private JButton removeButton;
    private JTextField testNameField;
    private JTextArea testDescriptionArea;
    private JPanel leftContentPane;


    public UnitTestPanel(JPanel leftContentPane, ActionListener removeActionListener) {
        this.leftContentPane = leftContentPane;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Initialize components
        JPanel headerPanel = createHeaderPanel(removeActionListener);

        // Add components to the main panel
        add(headerPanel, BorderLayout.NORTH);
        //add(editorPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel(ActionListener removeActionListener) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Test Details"));

        JPanel fieldsPanel = new JPanel(new BorderLayout(5, 5));

        // Unit Test Name
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Unit Test Name:"), BorderLayout.NORTH);
        testNameField = new JTextField();
        namePanel.add(testNameField, BorderLayout.CENTER);
        fieldsPanel.add(namePanel, BorderLayout.NORTH);

        // Unit Test Description
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("Unit Test Description:"), BorderLayout.NORTH);
        testDescriptionArea = new JTextArea(4, 20);
        testDescriptionArea.setLineWrap(true);
        testDescriptionArea.setWrapStyleWord(true);
        descriptionPanel.add(makeResizableTextArea(testDescriptionArea), BorderLayout.CENTER);
        fieldsPanel.add(descriptionPanel, BorderLayout.CENTER);

        headerPanel.add(fieldsPanel, BorderLayout.CENTER);

        // Regenerate and Remove buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // Regenerate button (left)
        regenerateButton = new JButton("Regenerate Test");
        buttonPanel.add(regenerateButton, BorderLayout.WEST);

        // Remove button (right)
        removeButton = new JButton("-");
        removeButton.setPreferredSize(new Dimension(50, removeButton.getPreferredSize().height));
        removeButton.addActionListener(removeActionListener);
        buttonPanel.add(removeButton, BorderLayout.EAST);

        headerPanel.add(buttonPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createEditorPanel() {
        editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder("Unit Test Editor"));

        // Create IntelliJ editor
        editor = createEditor("Unit test content goes here...");
        editorPanel.add(makeResizable(editor), BorderLayout.CENTER);

        return editorPanel;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
        //editorPanel.removeAll();
        editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(makeResizable(editor), BorderLayout.CENTER);
        editorPanel.revalidate();
        editorPanel.repaint();
        add(editorPanel, BorderLayout.CENTER);
    }

    private JPanel makeResizable(Editor editor) {
        JPanel resizablePanel = new JPanel(new BorderLayout());
        JScrollPane editorScrollPane = new JScrollPane(editor.getComponent());
        resizablePanel.add(editorScrollPane, BorderLayout.CENTER);

        JPanel resizeHandle = createResizeHandle(resizablePanel);
        resizablePanel.add(resizeHandle, BorderLayout.SOUTH);
        return resizablePanel;
    }

    private JPanel makeResizableTextArea(JTextArea textArea) {
        JPanel resizablePanel = new JPanel(new BorderLayout());
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        resizablePanel.add(textAreaScrollPane, BorderLayout.CENTER);

        JPanel resizeHandle = createResizeHandle(resizablePanel);
        resizablePanel.add(resizeHandle, BorderLayout.SOUTH);
        return resizablePanel;
    }

    private JPanel createResizeHandle(JPanel resizablePanel) {
        JPanel resizeHandle = new JPanel();
        resizeHandle.setPreferredSize(new Dimension(0, 5));
        resizeHandle.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        resizeHandle.setBackground(Color.GRAY);

        resizeHandle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                resizeHandle.putClientProperty("mousePressPoint", e.getPoint());
            }
        });

        resizeHandle.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point mousePressPoint = (Point) resizeHandle.getClientProperty("mousePressPoint");
                if (mousePressPoint != null) {
                    int deltaY = e.getY() - mousePressPoint.y;
                    Dimension size = resizablePanel.getPreferredSize();
                    size.height = Math.max(size.height + deltaY, 100); // Minimum height
                    resizablePanel.setPreferredSize(size);
                    resizablePanel.revalidate();
                    resizablePanel.repaint();
                    leftContentPane.revalidate(); // Push down everything below
                    leftContentPane.repaint();
                }
            }
        });

        return resizeHandle;
    }

    private Editor createEditor(String placeholderText) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument(placeholderText);
        Editor editor = editorFactory.createEditor(document);

        EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setFoldingOutlineShown(true);

        return editor;
    }

    public String getTestName() {
        return testNameField.getText();
    }

    public void setTestName(String testName) {
        testNameField.setText(testName);
    }

    public String getTestDescription() {
        return testDescriptionArea.getText();
    }

    public void setTestDescription(String testDescription) {
        testDescriptionArea.setText(testDescription);
    }

    public JButton getRegenerateButton() {
        return regenerateButton;
    }

    public void cleanUp() {
        if (editor != null) {
            EditorFactory.getInstance().releaseEditor(editor);
        }
    }

    public Editor getEditor() {
        return editor;
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }
}
