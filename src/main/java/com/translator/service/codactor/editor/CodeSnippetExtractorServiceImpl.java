package com.translator.service.codactor.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

public class CodeSnippetExtractorServiceImpl implements CodeSnippetExtractorService {
    private final Project project;

    @Inject
    public CodeSnippetExtractorServiceImpl(Project project) {
        this.project = project;
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
        String packagePath = filePackage.replace('.', '/') + ".java";
        String baseProjectDirectory = project.getBasePath();
        String srcCodePath = baseProjectDirectory + "/src/main/java/";
        String filePath = srcCodePath + packagePath;
        return LocalFileSystem.getInstance().findFileByPath(filePath);
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