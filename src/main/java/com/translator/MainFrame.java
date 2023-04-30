            package com.translator;

/*import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.*;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;*/

            import javax.swing.*;
            import java.awt.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author zantehays
 */
public class MainFrame extends JFrame /*implements SearchListener*/ {
    /*private Injector injector;
    private final Map<String, String> extensionToSyntaxMap = new HashMap<>();
    private final Map<String, String> languageToSyntaxMap = new HashMap<>();
    private Map<String, JBTextArea> displayMap;
    private JTree currentEditingFileTree;
    private File currentEditingDirectory;
    private File currentEditingFile;
    private JBTextArea display;
    private CollapsibleSectionPanel csp;
    private LoginDialog loginDialog;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private FindToolBar findToolBar;
    private ReplaceToolBar replaceToolBar;
    private JToggleButton queuedModificationButton = new JToggleButton();
    private JToggleButton modificationViewerButton = new JToggleButton();
    private JToggleButton inquiryViewerButton = new JToggleButton();
    private Gson gson;
    private FirebaseTokenService firebaseTokenService;
    private OpenAiApiKeyService openAiApiKeyService;
    private OpenAiModelService openAiModelService;
    private CodeModificationService codeModificationService;
    private CodeModificationHistoryDao codeModificationHistoryDao;
    private ContextQueryDao contextQueryDao;
    private InquiryDao inquiryDao;
    private FileModificationTrackerService fileModificationTrackerService;
    private TabKeyListenerService tabKeyListenerService;
    private FileCreatorService fileCreatorService;
    private CodeFileGeneratorService codeFileGeneratorService;
    private ModificationQueueViewer modificationQueueViewer;
    private CodeSnippetListViewer codeSnippetListViewer;
    private InquiryViewer inquiryViewer;
    private HistoricalModificationListViewer historicalModificationListViewer;
    private InquiryListViewer inquiryListViewer;
    private SearchResultParserService searchResultParserService;
    private SplitPaneService splitPaneService;
    private DisplayProjectorService displayProjectorService;
    private PromptContextService promptContextService;
    private PromptContextBuilderFactory promptContextBuilderFactory;
    private DirectoryCopierService directoryCopierService;
    private ModificationQueueListButtonService modificationQueueListButtonService;
    private LimitedSwingWorkerExecutor aiTaskExecutor;
    private LimitedSwingWorkerExecutor inquiryTaskExecutor;
    private LimitedSwingWorkerExecutor historyFetchingTaskExecutor;

    public MainFrame() {
        String userHome = System.getProperty("user.home");
        String codactorFolderPath = userHome + "/Codactor";
        if (!Files.exists(Paths.get(codactorFolderPath))) {
            try {
                Files.createDirectory(Paths.get(codactorFolderPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String credentialsFolderPath = codactorFolderPath + "/credentials";
        if (!Files.exists(Paths.get(credentialsFolderPath))) {
            try {
                Files.createDirectory(Paths.get(credentialsFolderPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        injector = Guice.createInjector(Stage.DEVELOPMENT, new CodeTranslatorViewConfig());

        extensionToSyntaxMap.put("java", SyntaxConstants.SYNTAX_STYLE_JAVA);
        extensionToSyntaxMap.put("c", SyntaxConstants.SYNTAX_STYLE_C);
        extensionToSyntaxMap.put("cpp", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        extensionToSyntaxMap.put("cs", SyntaxConstants.SYNTAX_STYLE_CSHARP);
        extensionToSyntaxMap.put("css", SyntaxConstants.SYNTAX_STYLE_CSS);
        extensionToSyntaxMap.put("csv", SyntaxConstants.SYNTAX_STYLE_CSV);
        extensionToSyntaxMap.put("d", SyntaxConstants.SYNTAX_STYLE_D);
        extensionToSyntaxMap.put("dart", SyntaxConstants.SYNTAX_STYLE_DART);
        extensionToSyntaxMap.put("dpr", SyntaxConstants.SYNTAX_STYLE_DELPHI);
        extensionToSyntaxMap.put("dtd", SyntaxConstants.SYNTAX_STYLE_DTD);
        extensionToSyntaxMap.put("for", SyntaxConstants.SYNTAX_STYLE_FORTRAN);
        extensionToSyntaxMap.put("go", SyntaxConstants.SYNTAX_STYLE_GO);
        extensionToSyntaxMap.put("groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY);
        extensionToSyntaxMap.put("html", SyntaxConstants.SYNTAX_STYLE_HTML);
        extensionToSyntaxMap.put("ini", SyntaxConstants.SYNTAX_STYLE_INI);
        extensionToSyntaxMap.put("js", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        extensionToSyntaxMap.put("json", SyntaxConstants.SYNTAX_STYLE_JSON);
        extensionToSyntaxMap.put("jsp", SyntaxConstants.SYNTAX_STYLE_JSP);
        extensionToSyntaxMap.put("kt", SyntaxConstants.SYNTAX_STYLE_KOTLIN);
        extensionToSyntaxMap.put("tex", SyntaxConstants.SYNTAX_STYLE_LATEX);
        extensionToSyntaxMap.put("less", SyntaxConstants.SYNTAX_STYLE_LESS);
        extensionToSyntaxMap.put("lisp", SyntaxConstants.SYNTAX_STYLE_LISP);
        extensionToSyntaxMap.put("lua", SyntaxConstants.SYNTAX_STYLE_LUA);
        extensionToSyntaxMap.put("makefile", SyntaxConstants.SYNTAX_STYLE_MAKEFILE);
        extensionToSyntaxMap.put("md", SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        extensionToSyntaxMap.put("mxml", SyntaxConstants.SYNTAX_STYLE_MXML);
        extensionToSyntaxMap.put("nsi", SyntaxConstants.SYNTAX_STYLE_NSIS);
        extensionToSyntaxMap.put("perl", SyntaxConstants.SYNTAX_STYLE_PERL);
        extensionToSyntaxMap.put("php", SyntaxConstants.SYNTAX_STYLE_PHP);
        extensionToSyntaxMap.put("proto", SyntaxConstants.SYNTAX_STYLE_PROTO);
        extensionToSyntaxMap.put("properties", SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
        extensionToSyntaxMap.put("py", SyntaxConstants.SYNTAX_STYLE_PYTHON);
        extensionToSyntaxMap.put("rb", SyntaxConstants.SYNTAX_STYLE_RUBY);
        extensionToSyntaxMap.put("sas", SyntaxConstants.SYNTAX_STYLE_SAS);
        extensionToSyntaxMap.put("scala", SyntaxConstants.SYNTAX_STYLE_SCALA);
        extensionToSyntaxMap.put("sql", SyntaxConstants.SYNTAX_STYLE_SQL);
        extensionToSyntaxMap.put("tcl", SyntaxConstants.SYNTAX_STYLE_TCL);
        extensionToSyntaxMap.put("ts", SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT);
        extensionToSyntaxMap.put("sh", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        extensionToSyntaxMap.put("vb", SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC);
        extensionToSyntaxMap.put("bat", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
        extensionToSyntaxMap.put("xml", SyntaxConstants.SYNTAX_STYLE_XML);
        extensionToSyntaxMap.put("yaml", SyntaxConstants.SYNTAX_STYLE_YAML);

        languageToSyntaxMap.put("java", SyntaxConstants.SYNTAX_STYLE_JAVA);
        languageToSyntaxMap.put("c", SyntaxConstants.SYNTAX_STYLE_C);
        languageToSyntaxMap.put("c++", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        languageToSyntaxMap.put("c#", SyntaxConstants.SYNTAX_STYLE_CSHARP);
        languageToSyntaxMap.put("css", SyntaxConstants.SYNTAX_STYLE_CSS);
        languageToSyntaxMap.put("csv", SyntaxConstants.SYNTAX_STYLE_CSV);
        languageToSyntaxMap.put("d", SyntaxConstants.SYNTAX_STYLE_D);
        languageToSyntaxMap.put("dart", SyntaxConstants.SYNTAX_STYLE_DART);
        languageToSyntaxMap.put("dpr", SyntaxConstants.SYNTAX_STYLE_DELPHI);
        languageToSyntaxMap.put("dtd", SyntaxConstants.SYNTAX_STYLE_DTD);
        languageToSyntaxMap.put("for", SyntaxConstants.SYNTAX_STYLE_FORTRAN);
        languageToSyntaxMap.put("go", SyntaxConstants.SYNTAX_STYLE_GO);
        languageToSyntaxMap.put("groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY);
        languageToSyntaxMap.put("html", SyntaxConstants.SYNTAX_STYLE_HTML);
        languageToSyntaxMap.put("ini", SyntaxConstants.SYNTAX_STYLE_INI);
        languageToSyntaxMap.put("js", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        languageToSyntaxMap.put("json", SyntaxConstants.SYNTAX_STYLE_JSON);
        languageToSyntaxMap.put("jsp", SyntaxConstants.SYNTAX_STYLE_JSP);
        languageToSyntaxMap.put("kt", SyntaxConstants.SYNTAX_STYLE_KOTLIN);
        languageToSyntaxMap.put("tex", SyntaxConstants.SYNTAX_STYLE_LATEX);
        languageToSyntaxMap.put("less", SyntaxConstants.SYNTAX_STYLE_LESS);
        languageToSyntaxMap.put("lisp", SyntaxConstants.SYNTAX_STYLE_LISP);
        languageToSyntaxMap.put("lua", SyntaxConstants.SYNTAX_STYLE_LUA);
        languageToSyntaxMap.put("makefile", SyntaxConstants.SYNTAX_STYLE_MAKEFILE);
        languageToSyntaxMap.put("md", SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        languageToSyntaxMap.put("mxml", SyntaxConstants.SYNTAX_STYLE_MXML);
        languageToSyntaxMap.put("nsi", SyntaxConstants.SYNTAX_STYLE_NSIS);
        languageToSyntaxMap.put("perl", SyntaxConstants.SYNTAX_STYLE_PERL);
        languageToSyntaxMap.put("php", SyntaxConstants.SYNTAX_STYLE_PHP);
        languageToSyntaxMap.put("proto", SyntaxConstants.SYNTAX_STYLE_PROTO);
        languageToSyntaxMap.put("properties", SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
        languageToSyntaxMap.put("py", SyntaxConstants.SYNTAX_STYLE_PYTHON);
        languageToSyntaxMap.put("rb", SyntaxConstants.SYNTAX_STYLE_RUBY);
        languageToSyntaxMap.put("sas", SyntaxConstants.SYNTAX_STYLE_SAS);
        languageToSyntaxMap.put("scala", SyntaxConstants.SYNTAX_STYLE_SCALA);
        languageToSyntaxMap.put("sql", SyntaxConstants.SYNTAX_STYLE_SQL);
        languageToSyntaxMap.put("tcl", SyntaxConstants.SYNTAX_STYLE_TCL);
        languageToSyntaxMap.put("ts", SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT);
        languageToSyntaxMap.put("sh", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        languageToSyntaxMap.put("vb", SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC);
        languageToSyntaxMap.put("bat", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
        languageToSyntaxMap.put("xml", SyntaxConstants.SYNTAX_STYLE_XML);
        languageToSyntaxMap.put("yaml", SyntaxConstants.SYNTAX_STYLE_YAML);

        currentEditingFile = null;

        initComponents();

        initSearchDialogs();
        
        csp = new CollapsibleSectionPanel();
        
      
        jPanel4.add(csp);

        setJMenuBar(createMenuBar());
        pack();

        setTitle("Codactor");

        firebaseTokenService = injector.getInstance(FirebaseTokenService.class);
        firebaseTokenService.refreshFirebaseToken();

        openAiApiKeyService = injector.getInstance(OpenAiApiKeyService.class);
        openAiModelService = injector.getInstance(OpenAiModelService.class);
        codeModificationService = injector.getInstance(CodeModificationService.class);
        codeModificationHistoryDao = injector.getInstance(CodeModificationHistoryDao.class);
        inquiryDao = injector.getInstance(InquiryDao.class);
        contextQueryDao = injector.getInstance(ContextQueryDao.class);
        fileModificationTrackerService = injector.getInstance(FileModificationTrackerService.class);
        fileCreatorService = injector.getInstance(FileCreatorService.class);
        modificationQueueListButtonService = injector.getInstance(ModificationQueueListButtonService.class);
        splitPaneService = injector.getInstance(SplitPaneService.class);
        tabKeyListenerService = injector.getInstance(TabKeyListenerService.class);
        codeFileGeneratorService = injector.getInstance(CodeFileGeneratorService.class);
        directoryCopierService = injector.getInstance(DirectoryCopierService.class);
        searchResultParserService = injector.getInstance(SearchResultParserService.class);
        promptContextService = injector.getInstance(PromptContextService.class);
        displayProjectorService = injector.getInstance(DisplayProjectorService.class);
        this.displayMap = displayProjectorService.getDisplayMap();

        projectNavigator.setViewportView(currentEditingFileTree);
        splitPaneService.setToolBar(jToolBar1);
        splitPaneService.setSplitPane(jSplitPane3);
        splitPaneService.retractRightPanel();
        modificationQueueListButtonService.setModificationQueueListButton(queuedModificationButton);
        fileModificationTrackerService.setModificationQueueListButtonService(modificationQueueListButtonService);
        splitPaneService.setFileModificationTrackerService(fileModificationTrackerService);
        promptContextService.setStatusLabel(jLabel2);
        searchResultParserService.setScrollPane(rTextScrollPane1);

        codeSnippetListViewer = injector.getInstance(CodeSnippetListViewer.class);
        inquiryViewer = injector.getInstance(InquiryViewer.class);
        inquiryListViewer = injector.getInstance(InquiryListViewer.class);
        historicalModificationListViewer = injector.getInstance(HistoricalModificationListViewer.class);
        modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);

        fileModificationTrackerService.setModificationQueueViewer(modificationQueueViewer);
        inquiryViewer.setCodeFileGeneratorService(codeFileGeneratorService);
        inquiryViewer.setHistoricalModificationListViewer(historicalModificationListViewer);
        inquiryViewer.setInquiryListViewer(inquiryListViewer);
        historicalModificationListViewer.setInquiryListViewer(inquiryListViewer);
        inquiryListViewer.setHistoricalModificationListViewer(historicalModificationListViewer);

        promptContextBuilderFactory = injector.getInstance(PromptContextBuilderFactory.class);

        display = new JBTextArea("");
        display.setCodeFoldingEnabled(true);
        display.setCurrentLineHighlightColor(new Color(242, 242, 242));

        displayProjectorService.setDisplayInitializer(new DisplayProjectorServiceImpl.DisplayInitializer() {
            @Override
            public JBTextArea initializeDisplay(String filePath, JBTextArea display) {
                return MainFrame.this.initializeNewDisplay(filePath, display);
            }

            @Override
            public void projectDisplay(String filePath, JBTextArea projectedDisplay) {
                if (filePath != null && !filePath.equals("Untitled")) {
                    MainFrame.this.currentEditingFile = new File(filePath);
                }
                String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                MainFrame.this.display.setSyntaxEditingStyle(extensionToSyntaxMap.get(extension));
                MainFrame.this.display = projectedDisplay;
                MainFrame.this.rTextScrollPane1.setLineNumbersEnabled(true);
                MainFrame.this.rTextScrollPane1.setViewportView(display);
            }
        });
        displayProjectorService.setTextScrollPane(rTextScrollPane1);
        this.display = new JBTextArea();
        displayProjectorService.projectDisplay("Untitled", display);
        modificationQueueViewer.setDisplayProjectorService(displayProjectorService);

        if (firebaseTokenService.getFirebaseToken() == null) {
            loginDialog = new LoginDialog(firebaseTokenService);
            if (firebaseTokenService.getFirebaseToken() == null) {
                System.exit(0);
            }
        }

        if (openAiApiKeyService.getOpenAiApiKey() == null || openAiApiKeyService.getOpenAiApiKey().isEmpty()) {
            OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
            openAiApiKeyDialog.setVisible(true);
        }

        codeSnippetListViewer.setModificationActionSelectedListener(new CodeSnippetListViewer.ModificationActionSelectedListener() {
            @Override
            public void onModificationAccepted(String fileModificationId, String modification) {
                fileModificationTrackerService.implementModificationUpdate(fileModificationId, modification);
            }

            @Override
            public void onModificationsRejected(String fileModificationId) {
                fileModificationTrackerService.removeModification(fileModificationId);
            }
        });

        final AtomicBoolean isRunning = new AtomicBoolean(false);
        Timer timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isRunning.compareAndSet(false, true)) {
                    fileModificationTrackerService.implementQueuedModificationUpdates();
                    isRunning.set(false);
                }
            }
        });
        timer.start();

        final TreePath[] currentPath = new TreePath[1]; // initialize with an empty TreePath object
        JBPopupMenu1.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // Get the current path when the popup menu is opened
                currentPath[0] = currentEditingFileTree.getSelectionPath();;
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Clear the current path when the popup menu is closed
                //currentPath[0] = null;
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                // Clear the current path when the popup menu is canceled
                //currentPath[0] = null;
            }
        });


        // Create menu items for copy/paste
        JBMenuItem copyItem = new JBMenuItem("Copy");
        JBMenuItem pasteItem = new JBMenuItem("Paste");

        // Create menu items for rename, create, and delete
        JBMenuItem renameItem = new JBMenuItem("Rename");
        JBMenuItem createItem = new JBMenuItem("New");
        JBMenuItem deleteItem = new JBMenuItem("Delete");

        copyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected node's user object
                Object node = currentPath[0].getLastPathComponent();

                // Get the file object associated with the selected node
                File rootDirectory = new File(currentEditingDirectory.getAbsolutePath());
                Object[] pathComponents = currentPath[0].getPath();
                File selectedFile = rootDirectory;
                if (pathComponents.length > 1) {
                    for (int i = 1; i < pathComponents.length; i++) {
                        selectedFile = new File(selectedFile, pathComponents[i].toString());
                    }
                }

                try {
                    directoryCopierService.copy(selectedFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                // Show confirmation message
                JOptionPane.showMessageDialog(currentEditingFileTree, "The file/folder has been copied.");
            }
        });

        pasteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected node's user object
                Object node = currentPath[0].getLastPathComponent();
                // Get the file object associated with the selected node
                File rootDirectory = new File(currentEditingDirectory.getAbsolutePath());
                Object[] pathComponents = currentPath[0].getPath();
                File selectedFile = rootDirectory;
                if (pathComponents.length > 1) {
                    for (int i = 1; i < pathComponents.length; i++) {
                        selectedFile = new File(selectedFile, pathComponents[i].toString());
                    }
                }
                File pastedFile = directoryCopierService.getCopiedDirectory();
                try {
                    directoryCopierService.paste(selectedFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if (selectedFile.isDirectory()) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) currentPath[0].getLastPathComponent();
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(pastedFile.getName() + "_copy");
                    selectedNode.add(newNode);
                    // Add the contents of the pasted directory to the new node
                    addDirectoryContentsToTree(pastedFile, newNode);
                    // Reload the tree model to reflect the changes
                    ((DefaultTreeModel) currentEditingFileTree.getModel()).reload(selectedNode);
                } else {
                    // Add the pasted file/folder as a sibling of the selected file in the JTree
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) currentPath[0].getLastPathComponent();
                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(pastedFile.getName() + "_copy");
                    int index = parentNode.getIndex(selectedNode) + 1;
                    parentNode.insert(newNode, index);
                    // Reload the tree model to reflect the changes
                    ((DefaultTreeModel) currentEditingFileTree.getModel()).reload(parentNode);
                }
                // Show confirmation message
                JOptionPane.showMessageDialog(currentEditingFileTree, "The file/folder has been pasted.");
            }
        });

        renameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected node's user object
                Object node = currentPath[0].getLastPathComponent();
                //Print the current path

                // Show input dialog to get new name
                String newName = (String) JOptionPane.showInputDialog(
                        display,
                        "Enter new name:",
                        "Rename",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        node.toString());

                // Get the file object associated with the selected node
                File rootDirectory = new File(currentEditingDirectory.getAbsolutePath());
                Object[] pathComponents = currentPath[0].getPath();
                File selectedFile = rootDirectory;
                for (int i = 1; i < pathComponents.length; i++) {
                    selectedFile = new File(selectedFile, pathComponents[i].toString());
                }

                File newFile = new File(selectedFile.getParent(), newName);
                if (newFile.exists()) {
                    JOptionPane.showMessageDialog(currentEditingFileTree, "A file/folder with the same name already exists in the same directory.");
                    return;
                }
                boolean renamed = selectedFile.renameTo(newFile);
                if (renamed) {
                    //Get file name (last path component):
                    String fileName = newFile.getName();
                    // Update the node's user object with the new file object
                    ((DefaultMutableTreeNode) currentPath[0].getLastPathComponent()).setUserObject(newFile.getName());
                    // Reload the tree model to reflect the changes
                    ((DefaultTreeModel) currentEditingFileTree.getModel()).reload();
                    // Show confirmation message
                    JOptionPane.showMessageDialog(currentEditingFileTree, "The file/folder has been renamed.");
                } else {
                    JOptionPane.showMessageDialog(currentEditingFileTree, "Failed to rename file/folder.");
                }
            }
        });
        createItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected node's user object
                Object node = currentPath[0].getLastPathComponent();

                // Show input dialog to get file name
                String fileName = (String) JOptionPane.showInputDialog(
                        display,
                        "Enter name of file (or folder if no extension):",
                        "Create",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");

                // Get the file object associated with the selected node
                File rootDirectory = new File(currentEditingDirectory.getAbsolutePath());
                Object[] pathComponents = currentPath[0].getPath();
                File selectedFile = rootDirectory;
                if (pathComponents.length > 1) {
                    for (int i = 1; i < pathComponents.length; i++) {
                        selectedFile = new File(selectedFile, pathComponents[i].toString());
                    }
                }

                if (selectedFile.isDirectory()) {
                    // Create the new file inside the selected directory
                    File newFile = new File(selectedFile.getAbsolutePath() + File.separator + fileName);
                    if (newFile.exists()) {
                        JOptionPane.showMessageDialog(currentEditingFileTree, "A file/folder with the same name already exists in the same directory.");
                        return;
                    }
                    boolean fileCreated = false;
                    if (fileName.contains(".")) {
                        try {
                            fileCreated = newFile.createNewFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        if (fileCreated) {
                            // Create a new node for the new file and add it to the JTree
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFile.getName());
                            ((DefaultMutableTreeNode) currentPath[0].getLastPathComponent()).add(newNode);
                            // Reload the tree model to reflect the changes
                            ((DefaultTreeModel) currentEditingFileTree.getModel()).reload();
                            // Show confirmation message
                            JOptionPane.showMessageDialog(currentEditingFileTree, "The file has been created.");
                        } else {
                            // Show error message
                            JOptionPane.showMessageDialog(currentEditingFileTree, "Failed to create file.");
                        }
                    } else {
                        fileCreated = newFile.mkdir();
                        if (fileCreated) {
                            // Create a new node for the new folder and add it to the JTree
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFile.getName());
                            ((DefaultMutableTreeNode) currentPath[0].getLastPathComponent()).add(newNode);
                            // Reload the tree model to reflect the changes
                            ((DefaultTreeModel) currentEditingFileTree.getModel()).reload();
                            // Show confirmation message
                            JOptionPane.showMessageDialog(currentEditingFileTree, "The folder has been created.");
                        } else {
                            // Show error message
                            JOptionPane.showMessageDialog(currentEditingFileTree, "Failed to create folder.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(currentEditingFileTree, "Cannot create file here.");
                }
            }
        });

        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected node's user object
                Object node = currentPath[0].getLastPathComponent();

                // Show confirmation dialog to confirm deletion
                int choice = JOptionPane.showConfirmDialog(
                        display,
                        "Are you sure you want to delete this file/folder?",
                        "Delete",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {

                    // Get the file object associated with the selected node
                    File rootDirectory = new File(currentEditingDirectory.getAbsolutePath());
                    Object[] pathComponents = currentPath[0].getPath();
                    File selectedFile = rootDirectory;
                    if (pathComponents.length > 1) {
                        for (int i = 1; i < pathComponents.length; i++) {
                            selectedFile = new File(selectedFile, pathComponents[i].toString());
                        }
                    }
                    boolean fileDeleted = false;
                    if (selectedFile.isDirectory()) {
                        // Delete the selected directory and all its children
                        fileDeleted = deleteDirectory(selectedFile);
                    } else {
                        // Delete the selected file
                        fileDeleted = selectedFile.delete();
                    }

                    if (fileDeleted) {
                        // Remove the node from its parent in the JTree
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) currentPath[0].getLastPathComponent();
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                        if (parentNode != null) {
                            parentNode.remove(selectedNode);
                            // Reload the tree model to reflect the changes
                            ((DefaultTreeModel) currentEditingFileTree.getModel()).reload(parentNode);
                        } else {
                            // If the parent node is null, it means the selected node is the root node
                            // So, we need to set a new root node to reflect the changes
                            DefaultMutableTreeNode newRootNode = new DefaultMutableTreeNode(rootDirectory.getName());
                            ((DefaultTreeModel) currentEditingFileTree.getModel()).setRoot(newRootNode);
                        }
                        // Show confirmation message
                        JOptionPane.showMessageDialog(currentEditingFileTree, "The file/folder has been deleted.");
                    } else {
                        // Show error message
                        JOptionPane.showMessageDialog(currentEditingFileTree, "Failed to delete file/folder.");
                    }
                }
            }
        });



        // Add the menu items to the popup menu
        JBPopupMenu1.add(createItem);
        JBPopupMenu1.add(renameItem);
        JBPopupMenu1.add(copyItem);
        JBPopupMenu1.add(pasteItem);
        JBPopupMenu1.add(deleteItem);


        promptInput.setCurrentLineHighlightColor(new Color(0, 0, 0, 0));
        
        jToolBar2.setFloatable(false);
        jToolBar2.setBorderPainted(false);
        jToolBar1.setFloatable(false);
        jToolBar1.setBorderPainted(false);
        jToolBar1.setLayout(new BoxLayout(jToolBar1, BoxLayout.Y_AXIS));




// Create an array of selection objects
        List<JPanelSelectionObject> selectionObjects = new ArrayList<>();
        selectionObjects.add(new JPanelSelectionObject("Queued Modifications", modificationQueueViewer));
        //selectionObjects.add(new JPanelSelectionObject("Modification Viewer", codeSnippetListViewer));
        selectionObjects.add(new JPanelSelectionObject("Inquiry Viewer", inquiryViewer));
        //selectionObjects.add(new JPanelSelectionObject("Local History", new JPanel()));


// Create a button for each selection object and add it to the toolbar
        for (JPanelSelectionObject selectionObject : selectionObjects) {
            // Create the button and set its text and alignment
            JToggleButton button = null;
            if (selectionObject.getName().equals("Queued Modifications")) {
                button = queuedModificationButton;
            } else if (selectionObject.getName().equals("Inquiry Viewer")) {
                button = inquiryViewerButton;
            }

            assert button != null;
            //button.setVerticalTextPosition(JToggleButton.CENTER);
            //button.setHorizontalTextPosition(JToggleButton.LEFT);
            button.setUI(new VerticalButtonUI());
            button.setFocusPainted(false);
            String htmlText = "<html><body style='transform: rotate(-90deg); white-space: nowrap;'>" + selectionObject.getName() + "</body></html>";
            button.setText(htmlText);


            // Set the preferred size of the button based on the text
            int width = 20;
            int height = 145;
            button.setPreferredSize(new Dimension(width, height));
            final JToggleButton jToggleButton = button;
            // Add the action listener to expand the right component with the selection object's panel
            button.addActionListener(e -> {
                boolean isSelected;
                if (jToggleButton == queuedModificationButton) {
                    isSelected = !jToggleButton.getBackground().equals(fileModificationTrackerService.getModificationQueueListButtonColor());
                } else {
                    isSelected = !jToggleButton.getBackground().equals(jToolBar1.getBackground());
                }
                if (isSelected) {
                    splitPaneService.retractRightPanel();
                } else {
                    int index = jToolBar1.getComponentIndex(jToggleButton);
                    splitPaneService.expandRightPanel(index, selectionObject.getPanel());
                }
            });


            // Add the button to the toolbar
            jToolBar1.add(button);
            jToolBar1.setMargin(new Insets(0, 0, 0, 0));
        }

// Add a glue component to push the buttons to the top
        jToolBar1.add(Box.createVerticalGlue());


        jLabel1.setText(" Implement the following modification(s) to the code file:");
        jButton1.setText("Modify");
        jButton2.setMnemonic(0);

        jToolBar3.setFloatable(false);
        jToolBar3.setBorderPainted(false);
        jButton3.setText("(Advanced) Add Context");
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PromptContextBuilder promptContextBuilder = promptContextBuilderFactory.create(promptContextService);
                promptContextBuilder.setVisible(true);
            }
        });
        
        languageInputTextField.setVisible(false);
        jLabel3.setVisible(false);
        fileTypeInputTextField.setVisible(false);
        
        List<String> options = Arrays.asList("Modify", "Modify Selected", "Fix", "Fix Selected", "Create", "Create Files", "Translate", "Inquire", "Inquire Selected");
        ComboBoxModel<String> model = new DefaultComboBoxModel<>(options.toArray(new String[0]));
        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) jComboBox1.getSelectedItem();
                assert selected != null;
                if (selected.equals("Translate")) {
                    jButton1.setText("Translate");
                    jButton2.setVisible(false);
                    jLabel1.setText(" to language: ");
                    languageInputTextField.setVisible(true);
                    jLabel3.setVisible(true);
                    fileTypeInputTextField.setVisible(true);
                    promptInput.setVisible(false);
                } else {
                    jButton2.setVisible(true);
                    jLabel3.setVisible(false);
                    languageInputTextField.setVisible(false);
                    fileTypeInputTextField.setVisible(false);
                    promptInput.setVisible(true);
                    if (selected.equals("Modify")) {
                        jButton1.setText("Modify");
                        jLabel1.setText(" Implement the following modification(s) to this code file:");
                    } else if (selected.equals("Fix")) {
                        jButton1.setText("Fix");
                        jLabel1.setText(" Fix the following error/problem in this code file:");
                    } else if (selected.equals("Create")) {
                        jButton1.setText("Create");
                        jLabel1.setText(" Create new code from scratch with the following description:");
                    } else if (selected.equals("Create Files")) {
                        jButton1.setText("Create");
                        jLabel1.setText(" (Experimental) Create multiple code files from the following description:");
                    } else if (selected.equals("Inquire")) {
                        jButton1.setText("Ask");
                        jLabel1.setText(" Ask the following question regarding this code file:");
                    } else if (selected.equals("Modify Selected")) {
                        jButton1.setText("Modify");
                        jLabel1.setText(" Implement the following modification(s) to the selected code:");
                    } else if (selected.equals("Fix Selected")) {
                        jButton1.setText("Fix");
                        jLabel1.setText(" Fix the following error/problem in this selected code:");
                    } else if (selected.equals("Inquire Selected")) {
                        jButton1.setText("Ask");
                        jLabel1.setText(" Ask the following question regarding this selected code:");
                    }
                }
            }
        });
        
        jComboBox1.setModel(model);
        
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) jComboBox1.getSelectedItem();
                if (selected.equals("Modify")) {
                    if (!display.getText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String filePath;
                        if (currentEditingFile == null) {
                            filePath = null;
                        } else {
                            filePath = currentEditingFile.getAbsolutePath();
                        }
                        String code = display.getText();
                        String modification = promptInput.getText();
                        String modificationId = fileModificationTrackerService.addModification(filePath, 0, code.length(), ModificationType.MODIFY);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, ModificationType.MODIFY, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
                                if (desktopCodeModificationResponseResource.getModificationSuggestions() != null) {
                                    fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, desktopCodeModificationResponseResource.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Modify Selected")) {
                    if (display.getSelectedText() != null && !display.getSelectedText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String filePath;
                        if (currentEditingFile == null) {
                            filePath = null;
                        } else {
                            filePath = currentEditingFile.getAbsolutePath();
                        }
                        String code = display.getSelectedText();
                        String modification = promptInput.getText();
                        int startIndex = display.getSelectionStart();
                        int endIndex = display.getSelectionEnd();
                        String modificationId = fileModificationTrackerService.addModification(filePath, startIndex, endIndex, ModificationType.MODIFY_SELECTION);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, modification, ModificationType.MODIFY_SELECTION, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getModifiedCode(desktopCodeModificationRequestResource);
                                if (desktopCodeModificationResponseResource.getModificationSuggestions() != null) {
                                    fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, desktopCodeModificationResponseResource.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Fix")) {
                    if (!display.getText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String filePath;
                        if (currentEditingFile == null) {
                            filePath = null;
                        } else {
                            filePath = currentEditingFile.getAbsolutePath();
                        }
                        String code = display.getText();
                        String error = promptInput.getText();
                        String modificationId = fileModificationTrackerService.addModification(filePath, 0, code.length(), ModificationType.FIX);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, ModificationType.FIX, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getFixedCode(desktopCodeModificationRequestResource);
                                if (desktopCodeModificationResponseResource.getModificationSuggestions() != null) {
                                    fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, desktopCodeModificationResponseResource.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Fix Selected")) {
                    if (display.getSelectedText() != null && !display.getSelectedText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String filePath;
                        if (currentEditingFile == null) {
                            filePath = null;
                        } else {
                            filePath = currentEditingFile.getAbsolutePath();
                        }
                        String code = display.getSelectedText();
                        String error = promptInput.getSelectedText();
                        int startIndex = display.getSelectionStart();
                        int endIndex = display.getSelectionEnd();
                        String modificationId = fileModificationTrackerService.addModification(filePath, startIndex, endIndex, ModificationType.FIX_SELECTION);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeModificationRequestResource desktopCodeModificationRequestResource = new DesktopCodeModificationRequestResource(filePath, code, error, ModificationType.FIX_SELECTION, openAiApiKey,  openAiModelService.getSelectedOpenAiModel(), priorContext);
                                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationService.getFixedCode(desktopCodeModificationRequestResource);
                                if (desktopCodeModificationResponseResource.getModificationSuggestions() != null) {
                                    fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeModificationResponseResource.getModificationSuggestions());
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (desktopCodeModificationResponseResource.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, desktopCodeModificationResponseResource.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Create")) {
                    if (!promptInput.getText().isEmpty()) {
                        String filePath;
                        if (currentEditingFile == null) {
                            filePath = null;
                        } else {
                            filePath = currentEditingFile.getAbsolutePath();
                        }
                        String description = promptInput.getText();
                        String modificationId = fileModificationTrackerService.addModification(filePath, 0, 0, ModificationType.CREATE);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(filePath, description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
                                if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                    fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions());
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, desktopCodeCreationResponseResource.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Create Files")) {
                    if (!promptInput.getText().isEmpty()) {
                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                        List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                        if (priorContextData != null) {
                            for (HistoricalContextObjectDataHolder data : priorContextData) {
                                priorContext.add(new HistoricalContextObjectHolder(data));
                            }
                        }
                        String description = promptInput.getText();
                        FileChooserWindow fileChooserWindow = new FileChooserWindow(description, priorContext, codeFileGeneratorService);
                        fileChooserWindow.setVisible(true);
                    }
                } else if (selected.equals("Inquire")) {
                    if (!promptInput.getText().isEmpty()) {
                        String filePath;
                        if (currentEditingFile == null) {
                            filePath = null;
                        } else {
                            filePath = currentEditingFile.getAbsolutePath();
                        }
                        String code = display.getText();
                        String question = promptInput.getText();
                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                        List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                        if (priorContextData != null) {
                            for (HistoricalContextObjectDataHolder data : priorContextData) {
                                priorContext.add(new HistoricalContextObjectHolder(data));
                            }
                        }
                        Inquiry temporaryInquiry = new Inquiry(null, filePath, code, question, priorContext);
                        inquiryViewer.updateInquiryContents(temporaryInquiry);
                        inquiryViewer.setLoadingChat(true);
                        splitPaneService.expandRightPanel(1, inquiryViewer);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                Inquiry inquiry = inquiryDao.createInquiry(filePath, code, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                inquiryViewer.updateInquiryContents(inquiry);
                                inquiryViewer.setLoadingChat(false);
                                splitPaneService.expandRightPanel(1, inquiryViewer);
                                promptContextService.clearPromptContext();
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Inquire Selected")) {
                    if (display.getSelectedText() != null && !display.getSelectedText().isEmpty() && !promptInput.getText().isEmpty()) {
                        String filePath;
                        if (currentEditingFile == null) {
                            filePath = null;
                        } else {
                            filePath = currentEditingFile.getAbsolutePath();
                        }
                        String code = display.getSelectedText();
                        String question = promptInput.getText();
                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                        List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                        if (priorContextData != null) {
                            for (HistoricalContextObjectDataHolder data : priorContextData) {
                                priorContext.add(new HistoricalContextObjectHolder(data));
                            }
                        }
                        Inquiry temporaryInquiry = new Inquiry(null, filePath, code, question, priorContext);
                        inquiryViewer.updateInquiryContents(temporaryInquiry);
                        inquiryViewer.setLoadingChat(true);
                        splitPaneService.expandRightPanel(1, inquiryViewer);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                Inquiry inquiry = inquiryDao.createInquiry(filePath, code, question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                inquiryViewer.updateInquiryContents(inquiry);
                                inquiryViewer.setLoadingChat(false);
                                splitPaneService.expandRightPanel(1, inquiryViewer);
                                promptContextService.clearPromptContext();
                                return null;
                            }
                        };
                        worker.execute();
                    }
                } else if (selected.equals("Translate")) {
                    if (!display.getText().isEmpty()) {
                        String filePath;
                        if (currentEditingFile == null) {
                            filePath = null;
                        } else {
                            filePath = currentEditingFile.getAbsolutePath();
                        }
                        String code = display.getText();
                        String newLanguage = languageInputTextField.getText();
                        String newFileType;
                        if (fileTypeInputTextField.getText().contains(".")) {
                            newFileType = fileTypeInputTextField.getText().substring(fileTypeInputTextField.getText().lastIndexOf(".") + 1);
                        } else {
                            newFileType = fileTypeInputTextField.getText();
                        }
                        String modificationId = fileModificationTrackerService.addModification(filePath, 0, code.length(), ModificationType.TRANSLATE);
                        LimitedSwingWorker worker = new LimitedSwingWorker(aiTaskExecutor) {
                            @Override
                            protected Void doInBackground() {
                                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                List<HistoricalContextObjectDataHolder> priorContextData = promptContextService.getPromptContext();
                                if (priorContextData != null) {
                                    for (HistoricalContextObjectDataHolder data : priorContextData) {
                                        priorContext.add(new HistoricalContextObjectHolder(data));
                                    }
                                }
                                String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
                                DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource = new DesktopCodeTranslationRequestResource(filePath, code, newLanguage, newFileType, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                DesktopCodeTranslationResponseResource desktopCodeTranslationResponseResource = codeModificationService.getTranslatedCode(desktopCodeTranslationRequestResource);
                                if (desktopCodeTranslationResponseResource.getModificationSuggestions() != null) {
                                    String styleKey = extensionToSyntaxMap.get(newFileType);
                                    if (styleKey == null) {
                                        styleKey = SyntaxConstants.SYNTAX_STYLE_NONE;
                                    }
                                    JBTextArea newDisplay;
                                    if (filePath != null) {
                                        String newFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "." + newFileType;
                                        File file = new File(filePath);
                                        File newFile = new File(newFilePath);
                                        file.renameTo(newFile);

                                        newDisplay = displayMap.get(filePath);
                                        displayMap.remove(filePath);
                                        initializeNewDisplay(newFilePath, newDisplay);
                                    } else {
                                        newDisplay = displayMap.get("Untitled");
                                        initializeNewDisplay("Untitled", newDisplay);
                                    }
                                    newDisplay.setSyntaxEditingStyle(styleKey);
                                    fileModificationTrackerService.readyFileModificationUpdate(modificationId, desktopCodeTranslationResponseResource.getModificationSuggestions());
                                    promptContextService.clearPromptContext();
                                } else {
                                    if (desktopCodeTranslationResponseResource.getError().equals("null: null")) {
                                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                        openAiApiKeyDialog.setVisible(true);
                                    } else {
                                        JOptionPane.showMessageDialog(display, desktopCodeTranslationResponseResource.getError(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    fileModificationTrackerService.removeModification(modificationId);
                                }
                                return null;
                            }
                        };
                        worker.execute();
                    }
                }
            }
        });

        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Robot robot = new Robot();
                    promptInput.requestFocusInWindow();
                    promptInput.setText("");
                    // Simulate a key press event for the CNTRL key.
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyRelease(KeyEvent.VK_CONTROL);

                    // Simulate another key press event for the CNTRL key.
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
        });

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
                
                
            }
        };
        //fileOpener.setFileFilter(filter);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileOpener = new JFileChooser();
        saveDialog = new JFileChooser();
        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();
        jFileChooser1 = new JFileChooser();
        jBTextAreaEditorKit1 = new org.fife.ui.rsyntaxtextarea.JBTextAreaEditorKit();
        JBPopupMenu1 = new JBPopupMenu();
        jDialog1 = new JDialog();
        jPanel6 = new JPanel();
        jTextField1 = new JTextField();
        jPanel1 = new JPanel();
        jSplitPane1 = new JSplitPane();
        jPanel2 = new JPanel();
        projectNavigator = new JBScrollPane();
        jPanel5 = new JPanel();
        jPanel3 = new JPanel();
        jToolBar2 = new JToolBar();
        jComboBox1 = new JComboBox<>();
        jLabel1 = new JLabel();
        languageInputTextField = new JTextField();
        jLabel3 = new JLabel();
        fileTypeInputTextField = new JTextField();
        jButton2 = new JButton();
        jButton1 = new JButton();
        jBScrollPane1 = new JBScrollPane();
        promptInput = new org.fife.ui.rtextarea.JBTextArea();
        jToolBar3 = new JToolBar();
        jLabel2 = new JLabel();
        jButton3 = new JButton();
        jPanel4 = new JPanel();
        jSplitPane3 = new JSplitPane();
        rTextScrollPane1 = new org.fife.ui.rtextarea.JBScrollPane();
        jBTextArea2 = new org.fife.ui.rsyntaxtextarea.JBTextArea();
        jBScrollPane2 = new JBScrollPane();
        jList2 = new JList<>();
        jToolBar1 = new JToolBar();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jTextField1.setText("jTextField2");

        GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(160, 160, 160)
                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(162, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        GroupLayout jDialog1Layout = new GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(255, 255, 255));

        projectNavigator.setBorder(null);
        projectNavigator.setMinimumSize(new Dimension(160, 15));
        projectNavigator.setPreferredSize(new Dimension(200, 0));

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectNavigator, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectNavigator, GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jToolBar2.setRollover(true);

        jComboBox1.setPreferredSize(new Dimension(125, 23));
        jToolBar2.add(jComboBox1);

        jLabel1.setText("jLabel1");
        jToolBar2.add(jLabel1);

        languageInputTextField.setMinimumSize(new Dimension(50, 30));
        languageInputTextField.setPreferredSize(new Dimension(50, 30));
        jToolBar2.add(languageInputTextField);

        jLabel3.setText(" to file type: ");
        jToolBar2.add(jLabel3);
        jLabel3.getAccessibleContext().setAccessibleName(" to file type:");
        jLabel3.getAccessibleContext().setAccessibleDescription("");

        fileTypeInputTextField.setMinimumSize(new Dimension(50, 30));
        fileTypeInputTextField.setPreferredSize(new Dimension(50, 30));
        fileTypeInputTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fileTypeInputTextFieldActionPerformed(evt);
            }
        });
        jToolBar2.add(fileTypeInputTextField);


        //String userHome = System.getProperty("user.home");
        //jButton2.setIcon(new javax.swing.ImageIcon(userHome + "/Codactor/resources/microphone_icon.png"));
        jButton2.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));
        jButton2.setMaximumSize(new Dimension(80, 23));
        jButton2.setMinimumSize(new Dimension(80, 23));
        jButton2.setPreferredSize(new Dimension(80, 23));
        jButton2.setSize(new Dimension(80, 23));
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Enter");
        jButton1.setToolTipText("");
        jButton1.setMaximumSize(new Dimension(80, 23));
        jButton1.setMinimumSize(new Dimension(80, 23));
        jButton1.setPreferredSize(new Dimension(80, 23));
        jButton1.setSize(new Dimension(80, 23));

        jBScrollPane1.setBorder(null);

        promptInput.setColumns(20);
        promptInput.setRows(5);
        promptInput.setCurrentLineHighlightColor(new Color(242, 242, 242));
        jBScrollPane1.setViewportView(promptInput);

        jToolBar3.setRollover(true);

        jLabel2.setText("jLabel2");
        jToolBar3.add(jLabel2);

        jButton3.setText("jButton3");
        jButton3.setBorder(null);
        jButton3.setBorderPainted(false);
        jButton3.setOpaque(true);
        jToolBar3.add(jButton3);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 608, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar3, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jButton1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jBScrollPane1, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
        );

        jSplitPane3.setResizeWeight(0.5);
        jSplitPane3.setToolTipText("");

        jBTextArea2.setColumns(20);
        jBTextArea2.setRows(5);
        rTextScrollPane1.setLineNumbersEnabled(true);
        rTextScrollPane1.setViewportView(jBTextArea2);

        jSplitPane3.setLeftComponent(rTextScrollPane1);

        jList2.setModel(new AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jBScrollPane2.setViewportView(jList2);

        jSplitPane3.setRightComponent(jBScrollPane2);

        jToolBar1.setBorder(null);
        jToolBar1.setOrientation(SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSplitPane3, GroupLayout.DEFAULT_SIZE, 1142, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
            .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
        );

        GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 1164, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(2, 2, 2)
                    .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(0, 0, 0)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 636, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(0, 0, 0)
                    .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(4, 4, 4)))
        );

        jSplitPane1.setRightComponent(jPanel5);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSplitPane1)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void fileTypeInputTextFieldActionPerformed(ActionEvent evt) {//GEN-FIRST:event_fileTypeInputTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fileTypeInputTextFieldActionPerformed

    private void saveAction() {
        //If we are editing a file opened, then we have to save the contents on the same file, currentEditingFile.
        if(currentEditingFile != null && display.canUndo()) {
            //Show save Dialog and get director.
            saveFileContents();
        } else {
            int status = saveDialog.showSaveDialog(rootPane);
            if (status == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile().getParentFile();

                String selectedFileName = saveDialog.getSelectedFile().getName();
                String fileName = selectedFileName.contains(".") ? selectedFileName : JOptionPane.showInputDialog("File Name", selectedFileName + ".txt");
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                String styleKey = extensionToSyntaxMap.get(fileExtension);
                if (styleKey == null) {
                    styleKey = SyntaxConstants.SYNTAX_STYLE_NONE;
                }
                display.setSyntaxEditingStyle(styleKey);
                File f = new File(selectedFile, fileName);
                if (f.exists()) {
                    JOptionPane.showMessageDialog(rootPane, "File Already Exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    try {
                        f.createNewFile();
                        PrintWriter printWriter = new PrintWriter(f);
                        printWriter.write(display.getText());
                        printWriter.close();
                        JOptionPane.showMessageDialog(rootPane, "Saved", "Done", JOptionPane.INFORMATION_MESSAGE);
                        displayMap.put(f.getAbsolutePath(), display);
                        currentEditingFile = f;
                        initializeNewDisplay(f.getAbsolutePath(), display);
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private void saveFileContents() {
        //Show save Dialog and get director.
        try {
            PrintWriter printWriter = new PrintWriter(currentEditingFile);
            printWriter.write(display.getText());
            printWriter.close();
            //display.discardAllEdits();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateTree(File dir, DefaultMutableTreeNode node) {
        File[] files = dir.listFiles();
        for (File file : files) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
            node.add(childNode);
            if (file.isDirectory()) {
                populateTree(file, childNode);
            }
        }
    }

    private String getPathString(TreePath path) {
        Object[] nodes = path.getPath();
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            if (i == 0) {
                pathBuilder.append(nodes[i]);
            } else {
                pathBuilder.append("/").append(nodes[i]);
            }
        }
        return pathBuilder.toString();
    }

    private JBTextArea initializeNewDisplay(String filePath, JBTextArea newDisplay) {
        newDisplay.setCurrentLineHighlightColor(new Color(242, 242, 242));
        newDisplay.setSyntaxEditingStyle(extensionToSyntaxMap.get(filePath.substring(filePath.lastIndexOf(".") + 1)));
        newDisplay.setColumns(20);
        newDisplay.setRows(5);
        newDisplay.setCodeFoldingEnabled(true);
        newDisplay.setMarkOccurrences(true);
        newDisplay.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                newDisplay.setCurrentLineHighlightColor(new Color(242, 242, 242));
            }

            @Override
            public void focusLost(FocusEvent e) {
                newDisplay.setCurrentLineHighlightColor(new Color(0, 0, 0, 0));
            }
        });
        for (MouseListener mouseListener : newDisplay.getMouseListeners()) {
            if (mouseListener instanceof DisplayMouseListener) {
                newDisplay.removeMouseListener(mouseListener);
                break;
            }
        }
        newDisplay.addMouseListener(new DisplayMouseListener(filePath, displayMap, fileModificationTrackerService, codeSnippetListViewer, modificationQueueViewer, splitPaneService));
        for (KeyListener keyListener : newDisplay.getKeyListeners()) {
            if (keyListener instanceof SelectAllKeyListener) {
                newDisplay.removeKeyListener(keyListener);
                break;
            }
        }
        newDisplay.addKeyListener(new SelectAllKeyListener(newDisplay));

        tabKeyListenerService.setFilePath(newDisplay, filePath);
        displayMap.put(filePath, newDisplay);
        fileModificationTrackerService.getDocumentListenerService().insertDocumentListener(filePath);
        return newDisplay;
    }

    private class NewAction extends AbstractAction {
        NewAction() {
            super("New");
            int c = getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            display = displayMap.get("Untitled");
            display.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
            display.setText("");
            display.discardAllEdits();
            displayProjectorService.projectDisplay("Untitled", display);
            currentEditingFileTree = null;
            projectNavigator.setViewportView(null);
            currentEditingFile = null;
        }
    }

    private class SaveAction extends AbstractAction {
        SaveAction() {
            super("Save");
            int c = getToolkit().getMenuShortcutKeyMask();

            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveAction();
        }
    }

    private class OpenAction extends AbstractAction {
        OpenAction() {
            super("Open");
            int c = getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(MainFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                currentEditingDirectory = chooser.getSelectedFile();
                DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentEditingDirectory.getName());
                DefaultTreeModel model = new DefaultTreeModel(root);
                populateTree(currentEditingDirectory, root);
                currentEditingFileTree = new JTree(model);
                currentEditingFileTree.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        int row = currentEditingFileTree.getRowForLocation(e.getX(), e.getY());
                        if (row != -1) {
                            currentEditingFileTree.setSelectionRow(row); // select the row
                            //TreePath path = currentEditingFileTree.getPathForRow(row);
                            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                            if (e.getButton() == MouseEvent.BUTTON3) {
                                JBPopupMenu1.show(projectNavigator, e.getX(), e.getY());
                            }
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent me) {
                        if (me.getClickCount() == 2) {
                            TreePath tp = currentEditingFileTree.getPathForLocation(me.getX(), me.getY());
                            if (tp != null) {
                                String path = currentEditingDirectory.getParent() + "/" + getPathString(tp);
                                File selectedFile = new File(path);
                                if (!selectedFile.isDirectory()) {
                                    if (currentEditingFile == null) {
                                        FileModificationTracker fileModificationTracker = fileModificationTrackerService.getModificationTracker("Untitled");
                                        if (display.canUndo() || (fileModificationTracker != null && !fileModificationTracker.getModifications().isEmpty())) {
                                            int choice = JOptionPane.showOptionDialog(MainFrame.this, "Do you want to save this file before opening a new file?", "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Save", "Don't Save", "Cancel"}, null);
                                            if (choice == JOptionPane.YES_OPTION) {
                                                new SaveAction().actionPerformed(null);
                                            } else if (choice == JOptionPane.CANCEL_OPTION) {
                                                return;
                                            } else if (choice == JOptionPane.NO_OPTION) {
                                                if (fileModificationTracker != null) {
                                                    for (FileModification fileModification : fileModificationTracker.getModifications()) {
                                                        fileModificationTrackerService.removeModification(fileModification.getId());
                                                    }
                                                }
                                            }
                                        }
                                    } else if (display.canUndo()) {
                                        System.out.println("Saving file " + currentEditingFile.getName());
                                        saveFileContents();
                                    }
                                    currentEditingFile = selectedFile;
                                    String fileName = selectedFile.getName();
                                    String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                                    String styleKey = extensionToSyntaxMap.get(extension);
                                    if (styleKey == null) {
                                        styleKey = SyntaxConstants.SYNTAX_STYLE_NONE;
                                    }
                                    try {
                                        if (displayMap.containsKey(selectedFile.getAbsolutePath())) {
                                            display = displayMap.get(selectedFile.getAbsolutePath());
                                            display = initializeNewDisplay(selectedFile.getAbsolutePath(), display);
                                        } else {
                                            String contents = new String(Files.readAllBytes(selectedFile.toPath()));
                                            JBTextArea newDisplay = new JBTextArea();
                                            newDisplay.setCurrentLineHighlightColor(new Color(242, 242, 242));
                                            newDisplay.setText(contents);
                                            newDisplay.discardAllEdits();
                                            newDisplay.setSyntaxEditingStyle(styleKey);
                                            display = initializeNewDisplay(selectedFile.getAbsolutePath(), newDisplay);
                                        }
                                        displayProjectorService.projectDisplay(selectedFile.getAbsolutePath(), display);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
                projectNavigator.setViewportView(currentEditingFileTree);
                //fileModificationTrackerService.getJTreeHighlighterService().setJTree(currentEditingFileTree);
                // add the tree to your project navigator JPanel or any other container
            }
        }
    }

    private class GoToLineAction extends AbstractAction {

        GoToLineAction() {
            super("Go To Line...");
            int c = getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            GoToDialog dialog = new GoToDialog(MainFrame.this);
            dialog.setMaxLineNumberAllowed(display.getLineCount());
            dialog.setVisible(true);
            int line = dialog.getLineNumber();
            if (line>0) {
                try {
                    display.setCaretPosition(display.getLineStartOffset(line-1));
                } catch (BadLocationException ble) { // Never happens
                    UIManager.getLookAndFeel().provideErrorFeedback(display);
                    ble.printStackTrace();
                }
            }
        }

    }

    private class NewFileAction extends AbstractAction {

        NewFileAction() {
            super("New File");
            //int c = getToolkit().getMenuShortcutKeyMask();
            //putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            GoToDialog dialog = new GoToDialog(MainFrame.this);
            dialog.setMaxLineNumberAllowed(display.getLineCount());
            dialog.setVisible(true);
            int line = dialog.getLineNumber();
            if (line>0) {
                try {
                    display.setCaretPosition(display.getLineStartOffset(line-1));
                } catch (BadLocationException ble) { // Never happens
                    UIManager.getLookAndFeel().provideErrorFeedback(display);
                    ble.printStackTrace();
                }
            }
        }

    }

    private class ShowFindDialogAction extends AbstractAction {

        ShowFindDialogAction() {
            super("Find...");
            int c = getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            findDialog.setVisible(true);
        }

    }



    private class ShowReplaceDialogAction extends AbstractAction {

        ShowReplaceDialogAction() {
            super("Replace...");
            int c = getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            replaceDialog.setVisible(true);
        }

    }

    private void initSearchDialogs() {

        findDialog = new FindDialog(this, this);

        replaceDialog = new ReplaceDialog(this, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);

        // Create toolbars and tie their search contexts together also.
        findToolBar = new FindToolBar(this);
        findToolBar.setSearchContext(context);
        replaceToolBar = new ReplaceToolBar(this);
        replaceToolBar.setSearchContext(context);

    }


    @Override
    public void searchEvent(SearchEvent e) {

        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                result = SearchEngine.markAll(display, context);
                break;
            case FIND:
                result = SearchEngine.markAll(display, context);
                if (result.getMarkedCount() == 0) {
                    UIManager.getLookAndFeel().provideErrorFeedback(display);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(display, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(display);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(display, context);
                JOptionPane.showMessageDialog(null, result.getCount() +
                        " occurrences replaced.");
                break;
        }

        String text;
        if (result.wasFound()) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
        } else if (type == SearchEvent.Type.FIND && result.getMarkedCount()>0) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
            searchResultParserService.findNext(context.getSearchFor());
        } else if (type == SearchEvent.Type.MARK_ALL) {
            if (result.getMarkedCount()>0) {
                text = "Occurrences marked: " + result.getMarkedCount();
            }
            else {
                text = "";
            }
        }
        else {
            text = "Text not found";
            JOptionPane.showMessageDialog(display, "Text not found", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addItem(Action a, ButtonGroup bg, JMenu menu) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
        bg.add(item);
        menu.add(item);
    }

    private JMenuBar createMenuBar() {

        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        file.add(new JBMenuItem(new NewAction()));
        file.add(new JBMenuItem(new OpenAction()));
        file.add(new JBMenuItem(new SaveAction()));
        file.addSeparator();
        JBMenuItem settings = new JBMenuItem("Settings");
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsPage settingsPage = new SettingsPage(firebaseTokenService, openAiModelService);
                settingsPage.setVisible(true);
            }
        });

        file.add(settings);
        //file.addSeparator();
        //file.add(new JBMenuItem(new MainFrame.SettingsAction()));

        mb.add(file);

        JMenu search = new JMenu("Search");
        search.add(new JBMenuItem(new ShowFindDialogAction()));
        search.add(new JBMenuItem(new ShowReplaceDialogAction()));
        search.add(new JBMenuItem(new GoToLineAction()));

        mb.add(search);



        int ctrl = getToolkit().getMenuShortcutKeyMask();
        int shift = InputEvent.SHIFT_MASK;
        /*KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl|shift);
        Action a = csp.addBottomComponent(ks, findToolBar);
        a.putValue(Action.NAME, "Show Find Search Bar");
        menu.add(new JBMenuItem(a));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_R, ctrl|shift);
        a = csp.addBottomComponent(ks, replaceToolBar);
        a.putValue(Action.NAME, "Show Replace Search Bar");
        menu.add(new JBMenuItem(a));*



        return mb;
    }

    public String getSelectedText() {
        return display.getSelectedText();
    }*/

    public static void main(String args[]) {


       /*try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        //</editor-fold>
        //</editor-fold>


        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

/*
    private boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return false;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return directory.delete();
    }

    private void addDirectoryContentsToTree(File directory, DefaultMutableTreeNode parentNode) {
        File[] files = directory.listFiles();
        for (File file : files) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file.getName());
            if (file.isDirectory()) {
                parentNode.add(newNode);
                addDirectoryContentsToTree(file, newNode);
            } else {
                parentNode.add(newNode);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JFileChooser fileOpener;
    private JTextField fileTypeInputTextField;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JDialog jDialog1;
    private JFileChooser jFileChooser1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JList<String> jList2;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JBPopupMenu JBPopupMenu1;
    private JBScrollPane jBScrollPane1;
    private JBScrollPane jBScrollPane2;
    private JComboBox<String> jComboBox1;
    private JSplitPane jSplitPane1;
    private JSplitPane jSplitPane3;
    private JTextField jTextField1;
    private JToolBar jToolBar1;
    private JToolBar jToolBar2;
    private JToolBar jToolBar3;
    private JTextField languageInputTextField;
    private JBScrollPane projectNavigator;
    private org.fife.ui.rtextarea.JBTextArea promptInput;
    private org.fife.ui.rsyntaxtextarea.JBTextArea jBTextArea2;
    private org.fife.ui.rsyntaxtextarea.JBTextAreaEditorKit jBTextAreaEditorKit1;
    private org.fife.ui.rtextarea.JBScrollPane rTextScrollPane1;
    private JFileChooser saveDialog;*/
    // End of variables declaration//GEN-END:variables
}
