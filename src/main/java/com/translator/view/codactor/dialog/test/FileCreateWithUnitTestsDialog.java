package com.translator.view.codactor.dialog.test;

import com.google.inject.assistedinject.Assisted;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.psi.PsiDirectory;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.chat.function.directive.test.ReplacedClassInfoResource;
import com.translator.model.codactor.ai.chat.function.directive.test.ResultsResource;
import com.translator.model.codactor.ai.modification.test.UnitTestData;
import com.translator.service.codactor.ai.chat.functions.directives.test.CompileAndRunTestsService;
import com.translator.service.codactor.ai.chat.functions.directives.test.ImplementationFixerService;
import com.translator.service.codactor.ai.modification.test.junit.CodeImplementationGeneratorService;
import com.translator.service.codactor.ide.editor.EditorService;
import com.translator.service.codactor.ide.editor.RangeReplaceService;
import com.translator.service.codactor.ide.file.FileCreatorService;
import com.translator.service.codactor.ide.file.FileRemoverService;
import com.translator.service.codactor.ai.modification.test.junit.InterfaceTemplateGeneratorService;
import com.translator.service.codactor.ai.modification.test.junit.UnitTestGeneratorService;
import com.translator.service.codactor.ai.modification.test.junit.UnitTestListGeneratorService;
import com.translator.viewmodel.UnitTestPanel;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FileCreateWithUnitTestsDialog extends JDialog {
    private Inquiry inquiry;
    private InquiryChat interfaceInquiryChat;
    //private VirtualFile interfaceFile;
    private JBSplitter mainSplitPane;
    private String interfaceFileName;
    private String implementationFileName;
    private PsiDirectory directory;
    private JTextField classNameTextField;
    private JTextArea codeDescription;
    private JButton regenerateAllButton;
    private JButton regenerateInterfaceButton;
    private JPanel documentedInterfacePanel;
    private Editor documentedInterfaceEditor;
    private JPanel implementationPanel;
    private Editor implementationEditor;
    private JButton regenerateTestsButton;
    private JCheckBox regenerateDescriptionsCheckBox;
    private JPanel unitTestsPanel;
    private JPanel leftContentPane;
    private JPanel rightContentPane;
    private JLabel unitTestStatusLabel;
    private JTextArea failedUnitTestsTextArea;
    //private JTextArea passedUnitTestsTextArea;

    private InterfaceTemplateGeneratorService interfaceTemplateGeneratorService;
    private UnitTestListGeneratorService unitTestListGeneratorService;
    private UnitTestGeneratorService unitTestGeneratorService;
    private CodeImplementationGeneratorService codeImplementationGeneratorService;
    private FileCreatorService fileCreatorService;
    private FileRemoverService fileRemoverService;
    private EditorService editorService;
    private CompileAndRunTestsService compileAndRunTestsService;
    private ImplementationFixerService implementationFixerService;
    private RangeReplaceService rangeReplaceService;

    @Inject
    public FileCreateWithUnitTestsDialog(@Assisted PsiDirectory directory,
                                         InterfaceTemplateGeneratorService interfaceTemplateGeneratorService,
                                         UnitTestListGeneratorService unitTestListGeneratorService,
                                         UnitTestGeneratorService unitTestGeneratorService,
                                         CodeImplementationGeneratorService codeImplementationGeneratorService,
                                         FileCreatorService fileCreatorService,
                                         EditorService editorService,
                                         CompileAndRunTestsService compileAndRunTestsService,
                                         ImplementationFixerService implementationFixerService,
                                         RangeReplaceService rangeReplaceService) {
        this.interfaceTemplateGeneratorService = interfaceTemplateGeneratorService;
        this.unitTestListGeneratorService = unitTestListGeneratorService;
        this.unitTestGeneratorService = unitTestGeneratorService;
        this.codeImplementationGeneratorService = codeImplementationGeneratorService;
        this.fileCreatorService = fileCreatorService;
        this.editorService = editorService;
        this.compileAndRunTestsService = compileAndRunTestsService;
        this.implementationFixerService = implementationFixerService;
        this.rangeReplaceService = rangeReplaceService;

        setTitle("Create File with Unit Tests");
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
        implementationEditor = editorFactory.createEditor(editorDocument);

        // Wrap the editor in a panel
        implementationPanel = new JPanel(new BorderLayout());
        implementationEditor.setBorder(BorderFactory.createTitledBorder("Editor"));
        implementationPanel.add(implementationEditor.getComponent(), BorderLayout.CENTER);

        // Create a panel for the button and editor
        JPanel topPanel = new JPanel(new BorderLayout());

        // Create the "Regenerate Implementation" button
        JButton regenerateButton = new JButton("Regenerate Implementation");
        regenerateButton.addActionListener(e -> {
            regenerateImplementation();
        });

        // Add the button to the top of the top panel
        topPanel.add(regenerateButton, BorderLayout.NORTH);
        topPanel.add(implementationPanel, BorderLayout.CENTER);

        // Create the bottom panel (unit test runner)
        JPanel unitTestPanel = new JPanel(new BorderLayout());
        unitTestPanel.setBorder(BorderFactory.createTitledBorder("Unit Test Runner"));
        /*JTextArea unitTestOutputArea = new JTextArea("Unit test output will appear here...");
        unitTestOutputArea.setEditable(false);
        unitTestOutputArea.setBackground(new Color(60, 63, 65)); // Match IntelliJ theme
        unitTestOutputArea.setForeground(Color.WHITE);*/
        //unitTestPanel.add(new JScrollPane(unitTestOutputArea), BorderLayout.CENTER);
        populateUnitTestsPanel(unitTestPanel);

        // Create a vertical split pane
        JBSplitter splitPane = new JBSplitter(true);
        splitPane.setBackground(new Color(60, 63, 65)); // Match IntelliJ theme
        splitPane.setFirstComponent(topPanel);
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
        regenerateAllButton.addActionListener(e -> {
            /*CountDownLatch latch = new CountDownLatch(1);
            ProgressManager.getInstance().run(new Task.Backgroundable(null, "Generating Interface") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {*/
                    regenerateInterface();
                    regenerateUnitTestListAndImplementation(interfaceInquiryChat);
            /*        latch.countDown();
                }
            });
            try {
                latch.await(); // Block until all tasks are complete
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interface test generation interrupted", ex);
            }

            new Thread(() -> {
                try {
                    latch.await(); // Block until all tasks are complete
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interface test generation interrupted", e2);
                }
*/
                //regenerateUnitTestList(interfaceInquiryChat);
                //runUnitTestsAndGetFeedback();
            //}).start();
            //More will be added here
        });
        leftContentPane.add(regenerateAllButton, gbc);

        // Regenerate interface button
        gbc.gridy++;
        regenerateInterfaceButton = new JButton("Regenerate Interface");
        regenerateInterfaceButton.addActionListener(e -> {
            regenerateInterface();
        });
        leftContentPane.add(regenerateInterfaceButton, gbc);

        // Documented interface label and resizable editor
        gbc.gridy++;
        gbc.gridwidth = 2;
        leftContentPane.add(new JLabel("Documented Interface:"), gbc);

        gbc.gridy++;
        documentedInterfacePanel = new JPanel(new BorderLayout());
        documentedInterfaceEditor = createEditor("Has not been generated yet");
        documentedInterfacePanel.add(makeResizable(documentedInterfaceEditor), BorderLayout.CENTER);
        leftContentPane.add(documentedInterfacePanel, gbc);

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
        regenerateTestsButton.addActionListener(e -> {
            regenerateUnitTestList(interfaceInquiryChat);
        });
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

        //populateUnitTestsPanel();
    }

    private void regenerateInterface() {
        String directoryPath = directory.getVirtualFile().getPath();
        if (interfaceFileName != null) {
            String oldFilePath = directoryPath + "/" + interfaceFileName;
            if (!interfaceFileName.endsWith(".java")) {
                oldFilePath += ".java";
            }
            fileRemoverService.deleteCodeFile(oldFilePath);
        } else {
            EditorFactory editorFactory = EditorFactory.getInstance();
            editorFactory.releaseEditor(documentedInterfaceEditor);
        }
        documentedInterfacePanel.removeAll();
        documentedInterfaceEditor = null;
        inquiry = interfaceTemplateGeneratorService.generateInterfaceTemplate(
                classNameTextField.getText(), directoryPath, codeDescription.getText());
        interfaceInquiryChat = inquiry.getChats().get(inquiry.getChats().size() - 1);
        //Code is surrounded by "```" to indicate a code block. Isolate this code:
        String startOfCode = interfaceInquiryChat.getMessage().substring(interfaceInquiryChat.getMessage().indexOf("```") + 3);
        String code = startOfCode.substring(0, startOfCode.indexOf("```"));
        if (code.startsWith("java")) {
            code = code.substring(4);
        }
        if (code.startsWith("\n")) {
            code = code.substring(1);
        }
        interfaceFileName = classNameTextField.getText();
        if (!interfaceFileName.endsWith(".java")) {
            interfaceFileName += ".java";
        }
        try {
            fileCreatorService.createFile(directoryPath, interfaceFileName, code);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        documentedInterfaceEditor = editorService.getEditorHeadless(directoryPath + "/" + interfaceFileName);
        // Refresh the UI
        documentedInterfacePanel.add(makeResizable(documentedInterfaceEditor), BorderLayout.CENTER);

        leftContentPane.revalidate();
        leftContentPane.repaint();
        System.out.println("Editor: " + documentedInterfaceEditor);
    }

    private void regenerateUnitTestList(InquiryChat interfaceInquiryChat) {
        List<UnitTestData> unitTestDataList = new ArrayList<>();
        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Generating Unit Tests") {
              @Override
              public void run(@NotNull ProgressIndicator indicator) {
                  List<UnitTestData> addList = unitTestListGeneratorService.generateUnitTestList(inquiry, interfaceInquiryChat);
                  for (UnitTestData unitTestData : addList) {
                      System.out.println("Unit Test: " + unitTestData.getName());
                  }
                  unitTestDataList.addAll(addList);
                  ApplicationManager.getApplication().invokeLater(() -> {
                      leftContentPane.revalidate();
                      leftContentPane.repaint();
                  });
                  regenerateUnitTests(unitTestDataList);
              }
          });
    }

    private void regenerateUnitTestListAndImplementation(InquiryChat interfaceInquiryChat) {
        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Generating Unit Tests") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                List<UnitTestData> unitTestDataList = unitTestListGeneratorService.generateUnitTestList(inquiry, interfaceInquiryChat);
                ApplicationManager.getApplication().invokeLater(() -> {
                    leftContentPane.revalidate();
                    leftContentPane.repaint();
                });
                regenerateUnitTestsThenImplementation(unitTestDataList);
            }
        });
    }

    private void regenerateUnitTests(List<UnitTestData> unitTestDataList) {
        for (UnitTestData unitTestData : unitTestDataList) {
            ProgressManager.getInstance().run(new Task.Backgroundable(null, "Generating Unit Tests") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    addUnitTest(unitTestData.getName(), unitTestData.getDescription());
                }
            });
        }
    }

    private void regenerateUnitTestsThenImplementation(List<UnitTestData> unitTestDataList) {
        CountDownLatch addUnitTestLatch = new CountDownLatch(unitTestDataList.size());
        for (UnitTestData unitTestData : unitTestDataList) {
            ProgressManager.getInstance().run(new Task.Backgroundable(null, "Generating Unit Tests") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        System.out.println("Generating unit test for: " + unitTestData.getName());
                        addUnitTest(unitTestData.getName(), unitTestData.getDescription());
                        System.out.println("Finished generating unit test for: " + unitTestData.getName());
                    } finally {
                        addUnitTestLatch.countDown();
                    }
                }
            });
        }
        new Thread(() -> {
            try {
                addUnitTestLatch.await(); // Wait until all unit tests are added
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Unit test generation interrupted", e);
            }

            // Update the UI on the Event Dispatch Thread
            ApplicationManager.getApplication().invokeLater(() -> {
                leftContentPane.revalidate();
                leftContentPane.repaint();
            });

            // Regenerate the implementation
            regenerateImplementation();
        }).start();
    }

    private void regenerateUnitTest(InquiryChat inquiryChat, UnitTestPanel unitTestPanel) {
        String directoryPath = directory.getVirtualFile().getPath();
        if (unitTestPanel.getEditor() != null) {
            String oldFilePath = directoryPath + "/" + unitTestPanel.getTestName();
            if (!oldFilePath.endsWith(".java")) {
                oldFilePath += ".java";
            }
            fileRemoverService.deleteCodeFile(oldFilePath);
        }
        String interfaceCode = documentedInterfaceEditor.getDocument().getText();
        //Get the package: (package com.translator.view.codactor.dialog.test;)
        String packageName = interfaceCode.trim().substring(interfaceCode.indexOf("package") + 8, interfaceCode.indexOf(";"));
        String code = unitTestGeneratorService.generateUnitTestCode(inquiry, inquiryChat, interfaceFileName, packageName, unitTestPanel.getTestName(), unitTestPanel.getTestDescription());
        if (!code.trim().startsWith("package")) {
            code = "package " + packageName + ";\n\n" + code;
        }
        //Get the code from the interface:
        // Extract the class name from the generated code
        String className = extractClassNameFromCode(code);

        // Use class name as the file name
        String unitTestinterfaceFileName = className + ".java";

        if (!unitTestinterfaceFileName.endsWith(".java")) {
            unitTestinterfaceFileName += ".java";
        }
        try {
            fileCreatorService.createFile(directoryPath, unitTestinterfaceFileName, code);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        String finalUnitTestinterfaceFileName = unitTestinterfaceFileName;
        ApplicationManager.getApplication().invokeLater(() -> {
            Editor unitTestEditor = editorService.getEditorHeadless(directoryPath + "/" + finalUnitTestinterfaceFileName);
            // Refresh the UI
            unitTestPanel.setEditor(unitTestEditor);

            leftContentPane.revalidate();
            leftContentPane.repaint();
            System.out.println("Editor: " + documentedInterfaceEditor);
        });
    }

    private void regenerateImplementation() {
        String directoryPath = directory.getVirtualFile().getPath();
        if (implementationFileName != null) {
            String oldFilePath = directoryPath + "/" + implementationFileName;
            if (!implementationFileName.endsWith(".java")) {
                oldFilePath += ".java";
            }
            fileRemoverService.deleteCodeFile(oldFilePath);
        } else {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                EditorFactory editorFactory = EditorFactory.getInstance();
                editorFactory.releaseEditor(implementationEditor);
            });
        }
        String code = codeImplementationGeneratorService.generateImplementationCode(inquiry, interfaceInquiryChat);
        String interfaceCode = documentedInterfaceEditor.getDocument().getText();
        //Get the package: (package com.translator.view.codactor.dialog.test;)
        String packageName = interfaceCode.trim().substring(interfaceCode.indexOf("package") + 8, interfaceCode.indexOf(";"));
        if (!code.trim().startsWith("package")) {
            code = "package " + packageName + ";\n\n" + code;
        }
        //Get the code from the interface:
        // Extract the class name from the generated code
        String className = extractClassNameFromCode(code);

        // Use class name as the file name
        implementationFileName = className + ".java";

        if (!implementationFileName.endsWith(".java")) {
            implementationFileName += ".java";
        }
        try {
            fileCreatorService.createFile(directoryPath, implementationFileName, code);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        runUnitTestsAndGetFeedback(null);
        implementationEditor = editorService.getEditorHeadless(directoryPath + "/" + implementationFileName);
        // Refresh the UI
        ApplicationManager.getApplication().invokeLater(() -> {
                    implementationPanel.add(makeResizable(implementationEditor), BorderLayout.CENTER);
            implementationPanel.revalidate();
            implementationPanel.repaint();
                });
    }

    // Helper method to extract class name
    private String extractClassNameFromCode(String code) {
        String[] lines = code.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("class ") || line.startsWith("public class ")) {
                line = line.substring(line.indexOf("class ") + 6);
                // Extract the word after "class"
                return line.split("\\s")[0];
            }
        }
        System.out.println("Code: " + code);
        throw new IllegalStateException("No class name found in code");
    }

    private void addUnitTest() {
        // Create a new unit test component
        //Putting this in:
        ActionListener removeButtonListener = e -> {
            JButton removeButton = (JButton) e.getSource();
            JPanel unitTestComponent = (JPanel) removeButton.getParent();
            removeUnitTest(unitTestComponent);
        };
        UnitTestPanel unitTestPanel = new UnitTestPanel(leftContentPane, removeButtonListener);

        //Instead of this:

        // Add the unit test component to the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridy = unitTestsPanel.getComponentCount();
        unitTestsPanel.add(unitTestPanel, gbc);

        // Rebuild layout and refresh scroll pane
        rebuildUnitTestsPanel();
        refreshScrollPane();
    }

    private void addUnitTest(String testName, String testDescription) {
        // Create a new unit test panel component
        ActionListener removeButtonListener = e -> {
            JButton removeButton = (JButton) e.getSource();
            JPanel unitTestComponent = (JPanel) removeButton.getParent();
            removeUnitTest(unitTestComponent);
        };

        // Pass the testName and testDescription to the UnitTestPanel
        UnitTestPanel unitTestPanel = new UnitTestPanel(leftContentPane, removeButtonListener);
        unitTestPanel.setTestName(testName);
        unitTestPanel.setTestDescription(testDescription);

        unitTestPanel.getRegenerateButton().addActionListener(
            e -> {
                regenerateUnitTest(interfaceInquiryChat, unitTestPanel);
            }
        );

        // Add the unit test panel to the unitTestsPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridy = unitTestsPanel.getComponentCount();
        unitTestsPanel.add(unitTestPanel, gbc);

        // Rebuild layout and refresh scroll pane
        rebuildUnitTestsPanel();
        refreshScrollPane();
        regenerateUnitTest(interfaceInquiryChat, unitTestPanel);
    }

    /*private JPanel createUnitTestComponent() {
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
    }*/

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

    private JPanel makeResizable(Editor editor) {
        JPanel resizablePanel = new JPanel(new BorderLayout());
        ApplicationManager.getApplication().invokeAndWait(() -> {
            JScrollPane editorScrollPane = new JScrollPane(editor.getComponent());
            resizablePanel.add(editorScrollPane, BorderLayout.CENTER);
        });

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
        //Editor editor = editorService.getEditorHeadless("/Users/zantehays/IdeaProjects/codactor-intellij-plugin/src/main/java/com/translator/view/codactor/dialog/LoginDialog.java");
        System.out.println("Editor: " + editor);
        return editor;
    }

    public void runUnitTestsAndGetFeedback(ReplacedClassInfoResource replacedClassInfoResource) {
        String directoryPath = directory.getVirtualFile().getPath();
        String implementationFilePath = directoryPath + "/" + implementationFileName;
        String interfaceFilePath = directoryPath + "/" + interfaceFileName;
        if (!implementationFileName.endsWith(".java")) {
            implementationFilePath += ".java";
        }
        if (!interfaceFileName.endsWith(".java")) {
            interfaceFilePath += ".java";
        }
        this.unitTestStatusLabel.setText("Compiling and running...");
        String finalImplementationFilePath = implementationFilePath;
        String finalInterfaceFilePath = interfaceFilePath;
        String finalInterfaceFilePath1 = interfaceFilePath;
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<ResultsResource> results = compileAndRunTestsService.compileAndRunUnitTests(finalInterfaceFilePath, finalImplementationFilePath, directoryPath);
            List<ResultsResource> failedResults = new ArrayList<>();
            List<ResultsResource> passedResults = new ArrayList<>();
            for (ResultsResource resultsResource : results) {
                if (resultsResource.getResult() == null || !resultsResource.getResult().wasSuccessful()) {
                    failedResults.add(resultsResource);
                } else {
                    passedResults.add(resultsResource);
                }
            }
            this.unitTestStatusLabel.setText("Unit Test Status: (" + passedResults.size() + "/" + results.size() + ") passed");
            StringBuilder failedUnitTestsText = new StringBuilder("Failed Unit Tests: \n\n");
            for (ResultsResource resultsResource : failedResults) {
                if (resultsResource.getResult() == null) {
                    failedUnitTestsText.append(resultsResource.getError());
                    continue;
                }
                for (Failure failure : resultsResource.getResult().getFailures()) {
                    failedUnitTestsText.append(failure.toString()).append("\n");
                }
                failedUnitTestsText.append("\n");
            }
            this.failedUnitTestsTextArea.setText(failedUnitTestsText.toString());

            if (failedResults.isEmpty()) {
                this.failedUnitTestsTextArea.setText("All unit tests passed!");
            } else {
                this.failedUnitTestsTextArea.setText(failedUnitTestsText.toString());
                boolean areNewResultsBetter = areNewResultsBetter(replacedClassInfoResource, results);
                System.out.println("Are new results better? " + areNewResultsBetter);

                if (areNewResultsBetter) {
                    ReplacedClassInfoResource newReplacedClassInfoResource = implementationFixerService.startFixing(finalImplementationFilePath, finalInterfaceFilePath1, results);

                    runUnitTestsAndGetFeedback(newReplacedClassInfoResource);
                } else {
                    System.out.println("The new results are not better than the old results");
                    unitTestStatusLabel.setText("Reverting unhelpful changes...");
                    rangeReplaceService.replaceRange(replacedClassInfoResource.getFilePath(), 0, replacedClassInfoResource.getNewCode().length(), replacedClassInfoResource.getOldCode(), true);

                    List<ResultsResource> formerResults = replacedClassInfoResource.getFormerResults();
                    List<ResultsResource> failedFormerResults = new ArrayList<>();
                    for (ResultsResource resultsResource : formerResults) {
                        if (resultsResource.getResult() == null || !resultsResource.getResult().wasSuccessful()) {
                            failedFormerResults.add(resultsResource);
                        }
                    }

                    if (!failedFormerResults.isEmpty()) {
                        ResultsResource firstResultsResource = formerResults.remove(formerResults.indexOf(failedFormerResults.get(0)));
                        formerResults.add(firstResultsResource);
                    }
                    replacedClassInfoResource.setFormerResults(formerResults);
                    //ReplacedClassInfoResource newReplacedClassInfoResource = implementationFixerService.startFixing(finalImplementationFilePath, formerResults);

                    runUnitTestsAndGetFeedback(null);
                }
            }
            /*StringBuilder passedUnitTestsText = new StringBuilder("Passed Unit Tests: \n\n");
            for (Result result : passedResults) {
                passedUnitTestsText.append(result).append(" tests passed\n");
            }
            this.passedUnitTestsTextArea.setText(passedUnitTestsText.toString());*/
        });
    }

    public void populateUnitTestsPanel(JPanel unitTestsPanel) {
        this.unitTestStatusLabel = new JLabel("Unit Test Status: Not Started");
        unitTestsPanel.add(this.unitTestStatusLabel, BorderLayout.NORTH);
        this.failedUnitTestsTextArea = new JTextArea("Failed Unit Tests: ");
        JBScrollPane unitTestsScrollPane = new JBScrollPane(failedUnitTestsTextArea);
        unitTestsPanel.add(unitTestsScrollPane, BorderLayout.CENTER);
        /*this.passedUnitTestsTextArea = new JTextArea("Passed Unit Tests: ");
        unitTestsPanel.add(this.passedUnitTestsTextArea, BorderLayout.SOUTH);*/
    }

    //

    private boolean areNewResultsBetter(ReplacedClassInfoResource replacedClassInfoResource, List<ResultsResource> newResults) {
        if (replacedClassInfoResource == null) {
            return true;
        }
        List<ResultsResource> oldResults = replacedClassInfoResource.getFormerResults();
        if (oldResults == null || oldResults.isEmpty()) {
            return true;
        }
        //Measurement: less failures OR, more total tests passed
        int oldFailures = 0;
        int newFailures = 0;
        for (ResultsResource resultsResource : oldResults) {
            if (resultsResource.getResult() == null || !resultsResource.getResult().wasSuccessful()) {
                oldFailures++;
            }
        }
        for (ResultsResource resultsResource : newResults) {
            if (resultsResource.getResult() == null || !resultsResource.getResult().wasSuccessful()) {
                newFailures++;
            }
        }
        if (newFailures == oldFailures) {
            return newResults.size() > oldResults.size();
        }
        return newFailures < oldFailures;
    }
}
