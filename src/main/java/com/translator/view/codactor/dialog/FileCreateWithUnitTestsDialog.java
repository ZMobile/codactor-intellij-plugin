package com.translator.view.codactor.dialog;

import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.psi.PsiDirectory;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileCreateWithUnitTestsDialog extends JDialog {
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
    private JPanel contentPane;

    @Inject
    public FileCreateWithUnitTestsDialog(@Assisted PsiDirectory directory) {
        setModal(true);
        initUIComponents();
        JScrollPane scrollableContent = new JScrollPane(contentPane);
        scrollableContent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setContentPane(scrollableContent);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.directory = directory;
    }

    private void initUIComponents() {
        contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Class name label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPane.add(new JLabel("Class Name:"), gbc);

        gbc.gridy++;
        classNameTextField = new JTextField(50);
        contentPane.add(classNameTextField, gbc);

        // Code description label and resizable text area
        gbc.gridy++;
        contentPane.add(new JLabel("Code Description:"), gbc);

        gbc.gridy++;
        codeDescription = new JTextArea(4, 50);
        codeDescription.setLineWrap(true);
        codeDescription.setWrapStyleWord(true);
        contentPane.add(makeResizableTextArea(codeDescription), gbc);

        // Regenerate all button
        gbc.gridy++;
        gbc.gridwidth = 1;
        regenerateAllButton = new JButton("Regenerate All");
        contentPane.add(regenerateAllButton, gbc);

        // Regenerate interface button
        gbc.gridy++;
        regenerateInterfaceButton = new JButton("Regenerate Interface");
        contentPane.add(regenerateInterfaceButton, gbc);

        // Documented interface label and resizable editor
        gbc.gridy++;
        gbc.gridwidth = 2;
        contentPane.add(new JLabel("Documented Interface:"), gbc);

        gbc.gridy++;
        documentedInterfaceEditor = createEditor("Has not been generated yet");
        contentPane.add(makeResizable(documentedInterfaceEditor), gbc);

        // Regenerate tests button and checkbox
        gbc.gridy++;
        gbc.gridwidth = 1;
        regenerateTestsButton = new JButton("Regenerate Tests");
        contentPane.add(regenerateTestsButton, gbc);

        gbc.gridx = 1;
        regenerateDescriptionsCheckBox = new JCheckBox("Regenerate Test Descriptions");
        contentPane.add(regenerateDescriptionsCheckBox, gbc);

        // Unit tests label
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        contentPane.add(new JLabel("Unit Tests:"), gbc);

        // Unit test panel
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        unitTestsPanel = new JPanel(new GridBagLayout());
        contentPane.add(unitTestsPanel, gbc);

        populateUnitTestsPanel();
    }

    private void populateUnitTestsPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (int i = 0; i < 10; i++) { // Example: 10 unit tests
            // Add a divider above the button
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            unitTestsPanel.add(separator, gbc);
            gbc.gridy++;

            // Add the regenerate button
            JButton regenerateButton = new JButton("Regenerate Test");
            unitTestsPanel.add(regenerateButton, gbc);
            gbc.gridy++;

            // Add the resizable editor
            Editor unitTestEditor = createEditor("Has not been generated yet");
            JPanel resizableEditor = makeResizable(unitTestEditor);
            unitTestsPanel.add(resizableEditor, gbc);
            gbc.gridy++;
        }

        // Ensure components align to the top
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        unitTestsPanel.add(new JPanel(), gbc);
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
                    contentPane.revalidate(); // Push down everything below
                    contentPane.repaint();
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
