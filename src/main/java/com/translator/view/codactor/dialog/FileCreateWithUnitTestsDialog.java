package com.translator.view.codactor.dialog;

import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.psi.PsiDirectory;
import com.intellij.ui.JBSplitter;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileCreateWithUnitTestsDialog extends JDialog {
    private JBSplitter mainSplitPane;
    private String fileName;
    private PsiDirectory directory;
    private JTextField classNameTextField;
    private JTextArea codeDescription;
    private JButton regenerateAllButton;
    private JButton regenerateInterfaceButton;
    private Editor documentedInterfaceEditor;
    private JButton regenerateTestsButton;
    private JCheckBox regenerateDescriptionsCheckBox;
    private JPanel unitTestsPanel;
    private JPanel leftContentPane;
    private JPanel rightContentPane;

    @Inject
    public FileCreateWithUnitTestsDialog(@Assisted PsiDirectory directory) {
        // Set up the main split pane
        mainSplitPane = new JBSplitter(false);
        /*mainSplitPane.setResizeWeight(0.5); // Distribute space evenly initially
        mainSplitPane.setDividerSize(8); // Make the divider more visible
        mainSplitPane.setOneTouchExpandable(true);*/

        // Set a dark background for the split pane divider
        mainSplitPane.setBackground(Color.DARK_GRAY);
        mainSplitPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border

        // Initialize left and right UI components
        initLeftUIComponents();
        initRightUIComponents();

        JScrollPane leftScrollableContent = new JScrollPane(leftContentPane);
        leftScrollableContent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        mainSplitPane.setFirstComponent(leftScrollableContent);
        mainSplitPane.setSecondComponent(rightContentPane);

        setContentPane(mainSplitPane);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.directory = directory;
    }

    private void initRightUIComponents() {
        // Create the right content pane
        rightContentPane = new JPanel(new BorderLayout());

        // Create the IntelliJ code editor (EditorFactory)
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document editorDocument = editorFactory.createDocument("Editor content goes here...");
        Editor editor = editorFactory.createEditor(editorDocument);

        // Wrap the editor in a panel
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder("Editor"));
        editorPanel.add(editor.getComponent(), BorderLayout.CENTER);

        // Create the bottom panel (unit test runner)
        JPanel unitTestPanel = new JPanel(new BorderLayout());
        unitTestPanel.setBorder(BorderFactory.createTitledBorder("Unit Test Runner"));
        JTextArea unitTestOutputArea = new JTextArea("Unit test output will appear here...");
        unitTestOutputArea.setEditable(false);
        unitTestOutputArea.setBackground(new Color(60, 63, 65)); // Match IntelliJ theme
        unitTestOutputArea.setForeground(Color.WHITE);
        unitTestPanel.add(new JScrollPane(unitTestOutputArea), BorderLayout.CENTER);

        // Create a vertical split pane
        JBSplitter splitPane = new JBSplitter(true);
        /*splitPane.setResizeWeight(0.7); // Allocate more space to the editor
        splitPane.setDividerLocation(300); // Initial divider location
        splitPane.setOneTouchExpandable(true); // Adds small arrows for quick resizing
        splitPane.setDividerSize(8);*/
        splitPane.setBackground(new Color(60, 63, 65)); // Match IntelliJ theme
        splitPane.setFirstComponent(editorPanel);
        splitPane.setSecondComponent(unitTestPanel);

        // Add the vertical split pane to the right content pane
        rightContentPane.add(splitPane, BorderLayout.CENTER);
    }



    private void initLeftUIComponents() {
        leftContentPane = new JPanel(new GridBagLayout());
        leftContentPane.setMinimumSize(new Dimension(100, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Class name label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        leftContentPane.add(new JLabel("Class Name:"), gbc);

        gbc.gridy++;
        classNameTextField = new JTextField(25);
        leftContentPane.add(classNameTextField, gbc);

        // Code description label and resizable text area
        gbc.gridy++;
        leftContentPane.add(new JLabel("Code Description:"), gbc);

        gbc.gridy++;
        codeDescription = new JTextArea(4, 25);
        codeDescription.setLineWrap(true);
        codeDescription.setWrapStyleWord(true);
        leftContentPane.add(makeResizableTextArea(codeDescription), gbc);

        // Regenerate all button
        gbc.gridy++;
        gbc.gridwidth = 1;
        regenerateAllButton = new JButton("Regenerate All");
        leftContentPane.add(regenerateAllButton, gbc);

        // Regenerate interface button
        gbc.gridy++;
        regenerateInterfaceButton = new JButton("Regenerate Interface");
        leftContentPane.add(regenerateInterfaceButton, gbc);

        // Documented interface label and resizable editor
        gbc.gridy++;
        gbc.gridwidth = 2;
        leftContentPane.add(new JLabel("Documented Interface:"), gbc);

        gbc.gridy++;
        documentedInterfaceEditor = createEditor("Has not been generated yet");
        leftContentPane.add(makeResizable(documentedInterfaceEditor), gbc);

        // Regenerate tests button, checkbox, and "+" button
        gbc.gridy++;
        JPanel regenerateTestsPanel = new JPanel(new GridBagLayout());

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.weightx = 1;

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.anchor = GridBagConstraints.EAST;

// Left panel for buttons
        JPanel leftSidePanel = new JPanel();
        leftSidePanel.setLayout(new BoxLayout(leftSidePanel, BoxLayout.X_AXIS));
        regenerateTestsButton = new JButton("Regenerate Tests");
        regenerateDescriptionsCheckBox = new JCheckBox("Regenerate Test Descriptions");
        leftSidePanel.add(regenerateTestsButton);
        leftSidePanel.add(Box.createHorizontalStrut(10));
        leftSidePanel.add(regenerateDescriptionsCheckBox);

// Right panel for "+" button
        JPanel rightSidePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton addUnitTestButton = new JButton("+");
        addUnitTestButton.addActionListener(e -> addUnitTest());
        rightSidePanel.add(addUnitTestButton);

// Add panels to regenerateTestsPanel
        regenerateTestsPanel.add(leftSidePanel, leftGbc);
        regenerateTestsPanel.add(rightSidePanel, rightGbc);

        leftContentPane.add(regenerateTestsPanel, gbc);

        // Unit tests label
        gbc.gridy++;
        gbc.gridwidth = 2;
        leftContentPane.add(new JLabel("Unit Tests:"), gbc);

        // Unit test panel
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        unitTestsPanel = new JPanel(new GridBagLayout());
        leftContentPane.add(unitTestsPanel, gbc);

        populateUnitTestsPanel();
    }

    private void addUnitTest() {
        // Create a new unit test component
        JPanel unitTestComponent = createUnitTestComponent();

        // Add the unit test component to the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridy = unitTestsPanel.getComponentCount();
        unitTestsPanel.add(unitTestComponent, gbc);

        // Rebuild layout and refresh scroll pane
        rebuildUnitTestsPanel();
        refreshScrollPane();
    }


    private JPanel createUnitTestComponent() {
        JPanel unitTestComponent = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Buttons panel with GridBagLayout for precise alignment
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonsGbc = new GridBagConstraints();

        // Regenerate Test button
        JButton regenerateButton = new JButton("Regenerate Test");
        buttonsGbc.gridx = 0;
        buttonsGbc.weightx = 0; // No horizontal expansion
        buttonsGbc.anchor = GridBagConstraints.WEST; // Align to the left
        buttonsGbc.insets = new Insets(0, 0, 0, 0); // No padding
        buttonsPanel.add(regenerateButton, buttonsGbc);

        // Remove button
        JButton removeButton = new JButton("-");
        removeButton.setPreferredSize(new Dimension(50, removeButton.getPreferredSize().height));
        removeButton.addActionListener(e -> {
            unitTestsPanel.remove(unitTestComponent);
            rebuildUnitTestsPanel(); // Rebuild layout to remove gaps
            refreshScrollPane();
        });
        buttonsGbc.gridx = 1;
        buttonsGbc.weightx = 1; // Push the button to the far right
        buttonsGbc.anchor = GridBagConstraints.EAST; // Align to the right
        buttonsPanel.add(removeButton, buttonsGbc);

        // Add buttons panel to the component
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        unitTestComponent.add(buttonsPanel, gbc);

        // Create resizable editor panel
        Editor editor = createEditor("Has not been generated yet");
        JPanel editorPanel = makeResizable(editor);

        // Add editor panel to the component
        gbc.gridy++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        unitTestComponent.add(editorPanel, gbc);

        // Add a divider line below the editor
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        gbc.gridy++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        unitTestComponent.add(separator, gbc);

        return unitTestComponent;
    }



    private void removeUnitTest(JPanel unitTestRow) {
        // Find the index of the row
        int rowIndex = unitTestsPanel.getComponentZOrder(unitTestRow);

        // Remove the row and the editor below it
        unitTestsPanel.remove(unitTestRow);
        if (rowIndex + 1 < unitTestsPanel.getComponentCount()) {
            unitTestsPanel.remove(rowIndex + 1);
        }

        // Rebuild the layout
        rebuildUnitTestsPanel();
        refreshScrollPane();
    }

    private void rebuildUnitTestsPanel() {
        // Save existing components except the filler panel
        Component[] components = unitTestsPanel.getComponents();
        java.util.List<Component> validComponents = new java.util.ArrayList<>();
        for (Component component : components) {
            if (!(component instanceof JPanel && ((JPanel) component).getComponentCount() == 0)) {
                validComponents.add(component);
            }
        }

        // Clear the panel
        unitTestsPanel.removeAll();

        // Re-add each component
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        for (int i = 0; i < validComponents.size(); i++) {
            gbc.gridy = i;
            unitTestsPanel.add(validComponents.get(i), gbc);
        }

        // Add the filler panel at the bottom
        gbc.gridy = validComponents.size();
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        unitTestsPanel.add(new JPanel(), gbc);

        // Refresh the layout
        unitTestsPanel.revalidate();
        unitTestsPanel.repaint();
        refreshScrollPane();
    }



    private void refreshScrollPane() {
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, unitTestsPanel);
        if (scrollPane != null) {
            scrollPane.revalidate();
            scrollPane.repaint();
        }
    }



    private void populateUnitTestsPanel() {
        for (int i = 0; i < 10; i++) {
            addUnitTest();
        }
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
        Editor editor = editorFactory.createEditor(editorFactory.createDocument(placeholderText));
        EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setFoldingOutlineShown(true);
        return editor;
    }
}
