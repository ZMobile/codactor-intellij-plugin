package com.translator.service.code;

import com.google.inject.Inject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;

import java.util.Objects;

@Service
public class RangeReplaceServiceImpl implements RangeReplaceService {
    private Project project;

    @Inject
    public RangeReplaceServiceImpl(Project project) {
        this.project = project;
    }

    public void replaceRange(String filePath, int startOffset, int endOffset, String replacementString) {
        final Document[] documentHolder = new Document[1];

        ApplicationManager.getApplication().runReadAction(() -> {
            VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
            if (virtualFile == null) {
                throw new IllegalStateException("File not found: " + filePath);
            }

            Document document = PsiDocumentManager.getInstance(project).getDocument(Objects.requireNonNull(PsiManager.getInstance(project).findFile(virtualFile)));
            if (document == null) {
                throw new IllegalStateException("Could not get document for file: " + filePath);
            }

            documentHolder[0] = document;
        });

        // Do nothing if the trimmed texts are the same:
        if (documentHolder[0].getText().substring(startOffset, endOffset).trim().equals(replacementString.trim())) {
            return;
        }

        // Replace the text range with the replacement string
        WriteCommandAction.runWriteCommandAction(project, () -> documentHolder[0].replaceString(startOffset, endOffset, replacementString));
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
}
