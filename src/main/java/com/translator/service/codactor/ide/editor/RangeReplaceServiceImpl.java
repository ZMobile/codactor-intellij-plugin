package com.translator.service.codactor.ide.editor;

import com.google.inject.Inject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Objects;

public class RangeReplaceServiceImpl implements RangeReplaceService {
    private Project project;

    @Inject
    public RangeReplaceServiceImpl(Project project) {
        this.project = project;
    }

    public void replaceRange(String filePath, int startOffset, int endOffset, String replacementString, boolean silent) {
        File file = new File(filePath);
        if (silent) {
            try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                out.println(replacementString);
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

                // Obtain the VirtualFile for the file you want to refresh
                LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
                VirtualFile virtualFile = localFileSystem.refreshAndFindFileByPath(filePath);

                if (virtualFile != null) {
                    // Refresh the currently opened file
                    fileEditorManager.updateFilePresentation(virtualFile);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            final Document[] documentHolder = new Document[1];
            documentHolder[0] = null;

            ApplicationManager.getApplication().runReadAction(() -> {
                VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
                if (virtualFile == null) {
                    throw new IllegalStateException("File not found: " + filePath);
                }

                Document document = PsiDocumentManager.getInstance(project).getDocument(Objects.requireNonNull(PsiManager.getInstance(project).findFile(virtualFile)));

                documentHolder[0] = document;
            });

            if (documentHolder[0] == null && file.length() == 0) {
                try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                    out.println(replacementString);
                    //FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

                    /*// Obtain the VirtualFile for the file you want to refresh
                    LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
                    VirtualFile virtualFile = localFileSystem.refreshAndFindFileByPath(filePath);

                    if (virtualFile != null) {
                        System.out.println("This gets called 5");
                        // Refresh the currently opened file
                        fileEditorManager.updateFilePresentation(virtualFile);
                    }
                    System.out.println("This gets called 6");*/
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (!documentHolder[0].getText().substring(startOffset, endOffset).trim().equals(replacementString.trim())) {
                // Replace the text range with the replacement string
                WriteCommandAction.runWriteCommandAction(project, () -> documentHolder[0].replaceString(startOffset, endOffset, replacementString));
            }
        }
    }


    public void replaceRange(Editor editor, int startOffset, int endOffset, String replacementString) {
        Document document = editor.getDocument();

        // Do nothing if the trimmed texts are the same:
        if (document.getText().substring(startOffset, endOffset).trim().equals(replacementString.trim())) {
            return;
        }
        // Replace the text range with the replacement string
        WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(startOffset, endOffset, replacementString));
    }

    public String replaceRange(String code, int startOffset, int endOffset, String replacementString) {
        // Ensure offsets are within the code's bounds
        System.out.println("Replacing range...");
        int codeLength = code.length();
        startOffset = Math.max(0, Math.min(startOffset, codeLength));
        endOffset = Math.max(0, Math.min(endOffset, codeLength));
        System.out.println("Start offset: " + startOffset);
        System.out.println("End offset: " + endOffset);
        // Extract the original text in the specified range
        String originalText = code.substring(startOffset, endOffset);
        System.out.println("Original text: " + originalText);
        // Do nothing if the trimmed texts are the same
        if (originalText.trim().equals(replacementString.trim())) {
            return code;
        }

        // Perform the replacement
        String replacement = code.substring(0, startOffset) +
                replacementString +
                code.substring(endOffset);
        System.out.println("Replacement: " + replacement);
        return replacement;
    }
}
