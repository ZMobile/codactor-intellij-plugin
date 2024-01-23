package com.translator.service.codactor.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.service.codactor.directory.FileDirectoryStructureQueryService;
import com.translator.service.codactor.file.FileOpenerService;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CodeSnippetExtractorServiceImpl implements CodeSnippetExtractorService {
    private final Project project;
    private final FileDirectoryStructureQueryService fileDirectoryStructureQueryService;
    private final EditorService editorService;
    private final FileOpenerService fileOpenerService;

    @Inject
    public CodeSnippetExtractorServiceImpl(Project project,
                                           FileDirectoryStructureQueryService fileDirectoryStructureQueryService,
                                           EditorService editorService,
                                           FileOpenerService fileOpenerService) {
        this.project = project;
        this.fileDirectoryStructureQueryService = fileDirectoryStructureQueryService;
        this.editorService = editorService;
        this.fileOpenerService = fileOpenerService;
    }

    public String getSnippet(String filePath, int startIndex, int endIndex) {
        if (filePath == null) {
            return null;
        }
        // Convert the file path to a VirtualFile instance
        AtomicReference<String> snippet = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);

            if (virtualFile != null) {
                // Get the Document instance corresponding to the VirtualFile
                Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

                if (document != null) {
                    // Retrieve the text from the document using the start and end indices
                    int newStartIndex = Math.max(startIndex, 0);
                    int newEndIndex = Math.min(endIndex, document.getText().length());
                    snippet.set(document.getText(new TextRange(newStartIndex, newEndIndex)));
                }
            }

        });
        return snippet.get();
    }

    @Override
    public Document getDocument(String filePath) {
        if (filePath == null) {
            return null;
        }
        // Convert the file path to a VirtualFile instance
        AtomicReference<Document> documentRef = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);

            if (virtualFile != null) {
                // Get the Document instance corresponding to the VirtualFile
                documentRef.set(FileDocumentManager.getInstance().getDocument(virtualFile));
            }
        });
        return documentRef.get();
    }

    public String getAllText(String filePath) {
        if (filePath == null) {
            return null;
        }
        AtomicReference<String> allTextRef = new AtomicReference<>();
        //ApplicationManager.getApplication().invokeAndWait(() -> {
            Path path = Paths.get(filePath);
            String content = null;
            try {
                System.out.println("This gets called 1");
                content = Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException ignored) {
                System.out.println("This gets called 2");
            }
            if (content == null) {
                Editor editor = editorService.getEditor(filePath);
                if (editor != null) {
                    content = editorService.getEditor(filePath).getDocument().getText();
                } else {
                    System.out.println("This gets called open sesame");
                    fileOpenerService.openFileInEditor(filePath);
                    content = editorService.getEditor(filePath).getDocument().getText();
                    //fileOpenerService.closeFileInEditor(filePath);
                }
            }
            allTextRef.set(content);
        //});
        return allTextRef.get();
    }

    public String getAllTextAtPackage(String filePackage) {
        if (filePackage == null) {
            return null;
        }
        VirtualFile virtualFile = getVirtualFileFromPackage(filePackage);

        if (virtualFile != null) {
            Path path = Paths.get(virtualFile.getPath());
            try {
                return Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    @Override
    public VirtualFile getVirtualFileFromPackage(String filePackage) {
        if (filePackage == null) {
            return null;
        }

        String packagePath = filePackage.replace('.', '/');

        VirtualFile directAccessFile = getVirtualFilesInDirectory(packagePath);
        if (directAccessFile != null) {
            return directAccessFile;
        }

        String[] packageDirectories = filePackage.split("\\.");
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getModules();

        List<String> modulePaths = new ArrayList<>();
        modulePaths.add(project.getBasePath());
        for (Module module : modules) {
            String modulePath = module.getModuleFilePath();
            modulePath = modulePath.substring(0, modulePath.lastIndexOf("/"));
            modulePaths.add(modulePath);
        }

        for (String modulePath : modulePaths) {
            VirtualFile sourceDirectory = findSourceDirectory(modulePath);
            if (sourceDirectory != null) {
                String rootPackageName = packageDirectories[0];
                String rootPackageDirectoryPath = fileDirectoryStructureQueryService.searchForChildDirectory(sourceDirectory.getPath(), rootPackageName, 4);
                if (rootPackageDirectoryPath != null) {
                    File folder = new File(rootPackageDirectoryPath);
                    for (int i = 1; i < packageDirectories.length; i++) {
                        String packageDirectory = packageDirectories[i];
                        if (i == packageDirectories.length - 1) {
                            File[] files = folder.listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    String fileName = packageDirectories[packageDirectories.length - 1];
                                    if (file.getName().startsWith(fileName)) {
                                        String fileWithExtension = fileName + "." + getFileExtension(file);
                                        if (file.getName().equals(fileWithExtension)) {
                                            return LocalFileSystem.getInstance().findFileByIoFile(file);
                                        }
                                    }
                                }
                            }
                        } else {
                            folder = new File(folder, packageDirectory);
                            if (!folder.exists()) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getFileExtension(File file) {
        if (file == null) {
            return null;
        }
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf+1);
    }


    private VirtualFile findSourceDirectory(String modulePath) {
        File sourceDirectory = new File(modulePath, "src");
        if (sourceDirectory.exists() && sourceDirectory.isDirectory()) {
            return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(sourceDirectory);
        }
        return null;
    }

    // new method
    private VirtualFile getVirtualFilesInDirectory(String packagePath){
        File directory = new File(project.getBasePath() + "/" + "src/main/java" + "/" + packagePath);
        System.out.println("Testo: " + directory.getPath());
        if (directory.exists()){
            return LocalFileSystem.getInstance().findFileByIoFile(directory);
        } else {
            return null;
        }
    }

    @Override
    public SelectionModel getSelectedText(String filePath) {
        AtomicReference<SelectionModel> selectionModelRef = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

            // Get the Editor instance for the selected file
            Editor editor = fileEditorManager.getSelectedTextEditor();

            if (editor != null) {
                // Get the SelectionModel and the selected text

                // Do something with the selected text
                selectionModelRef.set(editor.getSelectionModel());
            } else {
                selectionModelRef.set(null);
            }
        });
        return selectionModelRef.get();
    }

    @Override
    public String getSnippet(Editor editor, int startIndex, int endIndex) {
        // Retrieve the text from the document using the start and end indices
        return editor.getDocument().getText(new TextRange(startIndex, endIndex));
    }

    @Override
    public String getAllText(Editor editor) {
        // Retrieve the entire text from the document
        return editor.getDocument().getText();
    }

    @Override
    public SelectionModel getSelectedText(Editor editor) {
        // Get the SelectionModel and the selected text
        return editor.getSelectionModel();
    }

    @Override
    public String getCurrentAndNextLineCodeAfterIndex(String filePath, int startIndex) {
        Document document = getDocument(filePath);
        int lineEnd = document.getLineEndOffset(document.getLineNumber(startIndex));

        String currentLine = document.getText(new TextRange(startIndex, lineEnd));
        String restOfCode = document.getText(new TextRange(lineEnd + 1, document.getText().length()));

        String[] lines = restOfCode.split("\n", 2);
        String nextLine = "";
        if (lines.length > 1) {
            nextLine = lines[0];
        }

        return currentLine + "\n" + nextLine;
    }

    @Override
    public String getCurrentAndOneLinePreviousCodeBeforeIndex(String filePath, int endIndex) {
        Document document = getDocument(filePath);
        int lineStart = document.getLineStartOffset(document.getLineNumber(endIndex));

        String restOfCode = document.getText(new TextRange(0, lineStart));
        String previousLine = "";
        if (!restOfCode.isEmpty()) {
            previousLine = restOfCode.substring(restOfCode.lastIndexOf("\n") + 1);
        }

        String currentLine = document.getText(new TextRange(lineStart, endIndex));

        return previousLine + "\n" + currentLine;
    }

    @Override
    public String getCurrentLineCodeAtIndex(String filePath, int index) {
        if (filePath == null) {
            return null;
        }
        // Convert the file path to a VirtualFile instance
        AtomicReference<String> lineTextRef = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);

            if (virtualFile != null) {
                // Get the Document instance corresponding to the VirtualFile
                Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

                if (document != null) {
                    // Retrieve the line number from an index
                    int lineNumber = document.getLineNumber(index);
                    int lineStart = document.getLineStartOffset(lineNumber);
                    int lineEnd = document.getLineEndOffset(lineNumber);
                    // Retrieve the text of the line
                    lineTextRef.set(document.getText(new TextRange(lineStart, lineEnd)));
                }
            }
        });
        return lineTextRef.get();
    }
}