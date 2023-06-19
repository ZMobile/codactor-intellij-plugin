package com.translator.service.codactor.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;

public class EditorExtractorServiceImpl implements EditorExtractorService {

    @Inject
    public EditorExtractorServiceImpl() {
    }

    public Editor getEditorForVirtualFile(Project project, VirtualFile virtualFile) {
        return ApplicationManager.getApplication().runReadAction((Computable<Editor>) () -> {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document == null) {
                return null;
            }
            return EditorFactory.getInstance().getEditors(document, project).length > 0 ?
                    EditorFactory.getInstance().getEditors(document, project)[0] : null;
        });
    }
}
