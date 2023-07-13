package com.translator.service.codactor.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.impl.ModuleImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.service.codactor.directory.FileDirectoryStructureQueryService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CodeSnippetExtractorServiceImpl implements CodeSnippetExtractorService {
    private final Project project;
    private final FileDirectoryStructureQueryService fileDirectoryStructureQueryService;

    @Inject
    public CodeSnippetExtractorServiceImpl(Project project,
                                           FileDirectoryStructureQueryService fileDirectoryStructureQueryService) {
        this.project = project;
        this.fileDirectoryStructureQueryService = fileDirectoryStructureQueryService;
    }

    public String getSnippet(String filePath, int startIndex, int endIndex) {
        // Convert the file path to a VirtualFile instance
        AtomicReference<String> snippet = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);

            if (virtualFile != null) {
                // Get the Document instance corresponding to the VirtualFile
                Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

                if (document != null) {
                    // Retrieve the text from the document using the start and end indices
                    int newEndIndex = Math.min(endIndex, document.getTextLength());
                    snippet.set(document.getText(new TextRange(startIndex, newEndIndex)));
                }
            }

        });
        return snippet.get();
    }

    @Override
    public Document getDocument(String filePath) {
        // Convert the file path to a VirtualFile instance
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);

        if (virtualFile != null) {
            // Get the Document instance corresponding to the VirtualFile
            return FileDocumentManager.getInstance().getDocument(virtualFile);
        } else {
            return null;
        }
    }

    public String getAllText(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAllTextAtPackage(String filePackage) {
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

    @Override
    public SelectionModel getSelectedText(String filePath) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

        // Get the Editor instance for the selected file
        Editor editor = fileEditorManager.getSelectedTextEditor();

        if (editor != null) {
            // Get the SelectionModel and the selected text

            // Do something with the selected text
            return editor.getSelectionModel();
        } else {
            return null;
        }
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
}